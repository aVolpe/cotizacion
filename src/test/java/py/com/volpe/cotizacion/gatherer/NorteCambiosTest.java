package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;

/**
 * @author Arturo Volpe
 * @since 2026-10-24
 */
@ExtendWith(MockitoExtension.class)
class NorteCambiosTest {

    @Mock
    private NorteCambiosClient client;

    @InjectMocks
    private NorteCambios gatherer;

    @Test
    void doQuery() throws IOException {

        Mockito.when(client.get(NorteCambios.URL)).thenReturn(load("norte_home.html"));
        Mockito.when(client.postJson(eq(NorteCambios.UPDATE_URL), contains("\"params\":[1]"))).thenReturn(load("norte_branch_1.json"));
        Mockito.when(client.postJson(eq(NorteCambios.UPDATE_URL), contains("\"params\":[2]"))).thenReturn(load("norte_branch_2.json"));

        Place p = build();
        List<QueryResponse> result = gatherer.doQuery(p, p.getBranches());

        assertEquals(2, result.size());

        QueryResponse pjc = result.get(0);
        QueryResponse asu = result.get(1);

        assertEquals("Casa Matriz", pjc.getBranch().getName());
        assertEquals(4, pjc.getDetails().size());
        assertDetail(pjc.getDetails().get(0), Quote.of("USD", 5660), Quote.of("USD", 5800));
        assertDetail(pjc.getDetails().get(1), Quote.of("BRL", 1110), Quote.of("BRL", 1150));
        assertDetail(pjc.getDetails().get(2), Quote.of("EUR", 6350), Quote.of("EUR", 6800));
        assertDetail(pjc.getDetails().get(3), Quote.of("ARS", 3), Quote.of("ARS", 5));

        // cross rates (BRL•USD, USD•EUR, ARS•USD) and checks (USD CH) are ignored
        assertEquals("Asunción", asu.getBranch().getName());
        assertEquals(14, asu.getDetails().size());
        assertDetail(asu.getDetails().get(0), Quote.of("USD", 5660), Quote.of("USD", 5780));
        assertDetail(asu.getDetails().get(3), Quote.of("ARS", "3.40"), Quote.of("ARS", "3.80"));
        assertDetail(asu.getDetails().get(4), Quote.of("CLP", 5), Quote.of("CLP", 8));
        assertDetail(asu.getDetails().get(13), Quote.of("PEN", 1600), Quote.of("PEN", 2000));
    }

    @Test
    void doQuerySendsSessionDataToLivewire() throws IOException {

        Mockito.when(client.get(NorteCambios.URL)).thenReturn(load("norte_home.html"));
        Mockito.when(client.postJson(eq(NorteCambios.UPDATE_URL), anyString())).thenReturn(load("norte_branch_1.json"));

        Place p = build();
        gatherer.doQuery(p, p.getBranches().subList(0, 1));

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        Mockito.verify(client).postJson(eq(NorteCambios.UPDATE_URL), body.capture());

        assertTrue(body.getValue().contains("\"_token\":\"TpBSl8tq9A45mO49FaS1oGRNVx1UKWnHFSrbUGsW\""), body.getValue());
        assertTrue(body.getValue().contains("\"method\":\"setActiveBranch\""), body.getValue());
        assertTrue(body.getValue().contains("rates-board"), body.getValue());
    }

    @Test
    void doQuerySkipsTheBranchesThatFail() throws IOException {

        Mockito.when(client.get(NorteCambios.URL)).thenReturn(load("norte_home.html"));
        Mockito.when(client.postJson(eq(NorteCambios.UPDATE_URL), contains("\"params\":[1]"))).thenThrow(new AppException(500, "timeout"));
        Mockito.when(client.postJson(eq(NorteCambios.UPDATE_URL), contains("\"params\":[2]"))).thenReturn(load("norte_branch_2.json"));

        Place p = build();
        List<QueryResponse> result = gatherer.doQuery(p, p.getBranches());

        assertEquals(1, result.size());
        assertEquals("Asunción", result.get(0).getBranch().getName());
    }

    /**
     * Queries the real site, run it with {@code ./mvnw -B test -Dtest=NorteCambiosTest#live -Dlive=true}.
     * <p>
     * The raw responses are saved in target/norte-live to compare them with the fixtures.
     */
    @Test
    @EnabledIfSystemProperty(named = "live", matches = "true")
    void live() throws Exception {

        Path out = Files.createDirectories(Path.of("target", "norte-live"));
        NorteCambiosClient liveClient = new NorteCambiosClient() {
            @Override
            public String get(String url) {
                return save(out.resolve("home.html"), super.get(url));
            }

            @Override
            public String postJson(String url, String body) {
                String branch = body.replaceAll(".*\"params\":\\[(\\d+)].*", "$1");
                return save(out.resolve("branch_" + branch + ".json"), super.postJson(url, body));
            }
        };

        Place p = new Place("Norte Cambios", "NORTE");
        p.setBranches(Stream.of("1", "2", "3", "4", "5", "6", "8").map(code -> new PlaceBranch(0, code)).toList());

        List<QueryResponse> result = new NorteCambios(liveClient).doQuery(p, p.getBranches());

        assertEquals(7, result.size());
        for (QueryResponse qr : result) {
            assertTrue(qr.getDetails().stream().anyMatch(d -> d.getIsoCode().equals("USD")),
                    () -> "Branch " + qr.getBranch().getRemoteCode() + ": " + qr.getDetails());
        }
    }

    private static String save(Path file, String content) {
        try {
            Files.writeString(file, content);
            return content;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void assertDetail(QueryResponseDetail detail, Quote purchase, Quote sale) {
        assertEquals(purchase, detail.getPurchase());
        assertEquals(sale, detail.getSale());
    }

    private String load(String name) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(name), StandardCharsets.UTF_8);
    }

    private static Place build() {

        Place place = new Place("Norte Cambios", "NORTE");

        PlaceBranch pjc = new PlaceBranch();
        pjc.setName("Casa Matriz");
        pjc.setRemoteCode("1");

        PlaceBranch asu = new PlaceBranch();
        asu.setName("Asunción");
        asu.setRemoteCode("2");

        place.setBranches(Arrays.asList(pjc, asu));
        return place;
    }
}
