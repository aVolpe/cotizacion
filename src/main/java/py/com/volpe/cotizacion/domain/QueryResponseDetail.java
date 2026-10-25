package py.com.volpe.cotizacion.domain;

import lombok.Data;

/**
 * The rate of one currency in a {@link QueryResponse}, as returned by a gatherer.
 * <p>
 * The purchase and sale prices are {@link Quote}s of the same currency.
 *
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
public class QueryResponseDetail {

    private final Quote purchase;
    private final Quote sale;
    /**
     * >1 if it is increasing. Informative only, it is not stored (it can be derived from the history).
     */
    private long purchaseTrend;
    /**
     * >1 if it is increasing. Informative only, it is not stored (it can be derived from the history).
     */
    private long saleTrend;

    public QueryResponseDetail(Quote purchase, Quote sale) {
        if (!purchase.isoCode().equals(sale.isoCode())) {
            throw new IllegalArgumentException("The purchase (" + purchase + ") and the sale (" + sale
                    + ") must be of the same currency");
        }
        this.purchase = purchase;
        this.sale = sale;
    }

    public String getIsoCode() {
        return purchase.isoCode();
    }

    public QueryResponseDetail dup() {
        QueryResponseDetail toRet = new QueryResponseDetail(this.purchase, this.sale);
        toRet.setPurchaseTrend(this.purchaseTrend);
        toRet.setSaleTrend(this.saleTrend);
        return toRet;
    }
}
