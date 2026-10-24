package py.com.volpe.cotizacion.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The rate of one currency in a {@link QueryResponse}, as returned by a gatherer.
 *
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
@NoArgsConstructor
public class QueryResponseDetail {

    private long purchasePrice;
    private long salePrice;
    /**
     * >1 if it is increasing. Informative only, it is not stored (it can be derived from the history).
     */
    private long purchaseTrend;
    /**
     * >1 if it is increasing. Informative only, it is not stored (it can be derived from the history).
     */
    private long saleTrend;
    private String isoCode;

    public QueryResponseDetail(long purchasePrice, long salePrice, String isoCode) {
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.isoCode = isoCode;
    }

    public QueryResponseDetail dup() {
        QueryResponseDetail toRet = new QueryResponseDetail(this.purchasePrice, this.salePrice, this.isoCode);
        toRet.setPurchaseTrend(this.purchaseTrend);
        toRet.setSaleTrend(this.saleTrend);
        return toRet;
    }
}
