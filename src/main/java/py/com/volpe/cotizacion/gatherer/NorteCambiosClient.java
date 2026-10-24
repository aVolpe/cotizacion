package py.com.volpe.cotizacion.gatherer;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import py.com.volpe.cotizacion.AppException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;

/**
 * HTTP client for Norte Cambios, the {@link py.com.volpe.cotizacion.HTTPHelper} can't be used because:
 *
 * <ul>
 *     <li>Livewire calls need the session cookie of the page that provides the token.</li>
 *     <li>The server only sends its own certificate, without the intermediate, browsers download the missing one
 *     but java doesn't, so we ship the intermediate and trust it along the default ones.</li>
 * </ul>
 *
 * @author Arturo Volpe
 * @since 2026-10-24
 */
@Component
public class NorteCambiosClient {

    private static final String INTERMEDIATE_CERT = "/certs/sectigo-public-server-authentication-ca-dv-r36.pem";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient client;

    public NorteCambiosClient() throws GeneralSecurityException, IOException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(buildTrustStore());
        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(null, tmf.getTrustManagers(), null);

        this.client = HttpClient.newBuilder()
                .sslContext(ssl)
                .cookieHandler(new CookieManager())
                .connectTimeout(TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public String get(String url) {
        return send(HttpRequest.newBuilder(URI.create(url)).timeout(TIMEOUT).GET().build());
    }

    public String postJson(String url, String body) {
        return send(HttpRequest.newBuilder(URI.create(url))
                .timeout(TIMEOUT)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header("X-Livewire", "1")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build());
    }

    private String send(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                throw new AppException(response.statusCode(), "Invalid status returned querying URL " + request.uri() + " (the status is: " + response.statusCode() + ")");
            return response.body();
        } catch (IOException e) {
            throw new AppException(500, "Invalid response", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppException(500, "Interrupted querying URL " + request.uri(), e);
        }
    }

    /**
     * The default JVM trust anchors plus the intermediate that Norte Cambios doesn't send.
     */
    static KeyStore buildTrustStore() throws GeneralSecurityException, IOException {
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        store.load(null, null);

        TrustManagerFactory defaults = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        defaults.init((KeyStore) null);
        for (var tm : defaults.getTrustManagers()) {
            if (tm instanceof X509TrustManager x509) {
                for (X509Certificate cert : x509.getAcceptedIssuers()) {
                    store.setCertificateEntry(cert.getSubjectX500Principal().getName(), cert);
                }
            }
        }

        try (InputStream is = NorteCambiosClient.class.getResourceAsStream(INTERMEDIATE_CERT)) {
            if (is == null) throw new IOException("Missing certificate " + INTERMEDIATE_CERT);
            Certificate intermediate = CertificateFactory.getInstance("X.509").generateCertificate(is);
            store.setCertificateEntry("norte-cambios-intermediate", intermediate);
        }
        return store;
    }
}
