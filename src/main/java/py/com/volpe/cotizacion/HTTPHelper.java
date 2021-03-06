package py.com.volpe.cotizacion;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Service
public class HTTPHelper {

    public String doGet(String uri) {

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");


            return handleResponse(uri, con);
        } catch (IOException e) {
            throw new AppException(500, "Invalid response", e);
        }
    }

    public String doGet(String uri, int timeout,
                        Map<String, String> headers,
                        boolean ignoreSSL) throws SocketTimeoutException {

        try {
            URL url = new URL(uri);
            HttpURLConnection con = null;
            if (ignoreSSL) {
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setSSLSocketFactory(buildIgnoreCertSocketFactory());
                con = https;
            } else {
                con = (HttpURLConnection) url.openConnection();
            }
            con.setRequestMethod("GET");
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);

            con.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
            headers.forEach(con::setRequestProperty);

            return handleResponse(uri, con);
        } catch (java.net.SocketTimeoutException e) {
            throw e;
        } catch (IOException e) {
            throw new AppException(500, "Invalid response", e);
        }
    }

    public String doGet(String uri, int timeout) throws SocketTimeoutException {
        return this.doGet(uri, timeout, Collections.emptyMap(), false);
    }

    private String handleResponse(String uri, HttpURLConnection con) throws IOException {
        int status = con.getResponseCode();
        if (status != 200)
            throw new AppException(status, "Invalid status returned querying URL " + uri + " (the status is: " + status + ")");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    public String doPost(String uri, Map<String, String> data) {
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoOutput(true);


            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : data.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            con.setInstanceFollowRedirects(true);
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
            con.connect();
            try (OutputStream os = con.getOutputStream()) {
                os.write(out);
            }

            return handleResponse(uri, con);
        } catch (IOException e) {
            throw new AppException(500, "Invalid response", e);
        }
    }


    SSLSocketFactory buildIgnoreCertSocketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                }
        };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            // ignore this case
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            // ignore this case
        }
        return sc.getSocketFactory();
    }
}
