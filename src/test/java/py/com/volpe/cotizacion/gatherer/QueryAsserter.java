package py.com.volpe.cotizacion.gatherer;

import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

final class QueryAsserter {

    private final List<QueryResponse> result;

    QueryAsserter(List<QueryResponse> result) {
        this.result = result;
    }

    QueryAsserter shouldReturnBranches(String... remoteCodes) {
        assertEquals(List.of(remoteCodes), remoteCodes());
        return this;
    }

    /**
     * Every returned branch has exactly these currencies, in this order.
     */
    QueryAsserter shouldHaveOnlyCurrencies(String... isoCodes) {
        result.forEach(qr -> assertEquals(List.of(isoCodes), isoCodes(qr),
                "currencies of " + qr.getBranch().getRemoteCode()));
        return this;
    }

    QueryAsserter shouldQuote(String remoteCode, String isoCode, String purchase, String sale) {
        QueryResponseDetail detail = branch(remoteCode).getDetails().stream()
                .filter(d -> d.getIsoCode().equals(isoCode))
                .findFirst()
                .orElseGet(() -> fail("The branch " + remoteCode + " has no " + isoCode + ", only " + isoCodes(branch(remoteCode))));
        assertEquals(Quote.parseLocal(isoCode, purchase), detail.getPurchase(), isoCode + " purchase in " + remoteCode);
        assertEquals(Quote.parseLocal(isoCode, sale), detail.getSale(), isoCode + " sale in " + remoteCode);
        return this;
    }

    QueryAsserter shouldNotQuote(String remoteCode, String isoCode) {
        assertFalse(isoCodes(branch(remoteCode)).contains(isoCode),"The branch " + remoteCode + " has " + isoCode);
        return this;
    }

    private QueryResponse branch(String remoteCode) {
        return result.stream()
                .filter(qr -> qr.getBranch().getRemoteCode().equals(remoteCode))
                .findFirst()
                .orElseGet(() -> fail("There is no branch " + remoteCode + ", only " + remoteCodes()));
    }

    private List<String> remoteCodes() {
        return result.stream().map(qr -> qr.getBranch().getRemoteCode()).toList();
    }

    private static List<String> isoCodes(QueryResponse qr) {
        return qr.getDetails().stream().map(QueryResponseDetail::getIsoCode).toList();
    }
}
