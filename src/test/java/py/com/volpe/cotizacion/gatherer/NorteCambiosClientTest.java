package py.com.volpe.cotizacion.gatherer;

import org.junit.jupiter.api.Test;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arturo Volpe
 * @since 2026-10-24
 */
class NorteCambiosClientTest {

    @Test
    void trustStoreAddsTheMissingIntermediateToTheDefaultOnes() throws Exception {

        KeyStore store = NorteCambiosClient.buildTrustStore();

        List<String> subjects = Collections.list(store.aliases()).stream()
                .map(alias -> getSubject(store, alias))
                .toList();

        assertTrue(subjects.stream().anyMatch(s -> s.contains("CN=Sectigo Public Server Authentication CA DV R36")), subjects::toString);
        assertTrue(subjects.stream().anyMatch(s -> s.contains("CN=Sectigo Public Server Authentication Root R46")), subjects::toString);
    }

    private static String getSubject(KeyStore store, String alias) {
        try {
            return ((X509Certificate) store.getCertificate(alias)).getSubjectX500Principal().getName();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
