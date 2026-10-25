package py.com.volpe.cotizacion.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The rate of a currency in a place (or branch) while it didn't change.
 * <p>
 * The places are queried every 10 minutes and most of the time the rates are the same, so instead of a row
 * per query there is a row per price: it was first seen at {@link #firstSeen} and confirmed by every query
 * until {@link #lastSeen}. A new row is added only when the purchase or sale price changes.
 * <p>
 * For a chart, the price of a row holds from its {@link #firstSeen} until the {@link #firstSeen} of the next
 * row of the same place/branch/currency.
 */
@Data
@Entity
@NoArgsConstructor
@ToString(exclude = {"place", "branch", "lastExecution"})
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Place place;

    /**
     * Null when the place is queried as a whole (most banks).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private PlaceBranch branch;

    private String isoCode;

    @Column(precision = 14, scale = Quote.SCALE)
    private BigDecimal purchasePrice;
    @Column(precision = 14, scale = Quote.SCALE)
    private BigDecimal salePrice;

    private Date firstSeen;
    private Date lastSeen;

    /**
     * The execution that saw this rate for the last time, the latest exchange rates are the ones of the last
     * execution. Null for the rows migrated from before the executions existed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Execution lastExecution;

    /**
     * True for the newest row of each place/branch/currency: the one a new query is compared with.
     */
    private boolean latest;

    public ExchangeRate(QueryResponse response, QueryResponseDetail detail, Execution execution) {
        this.place = response.getPlace();
        this.branch = response.getBranch();
        this.isoCode = detail.getIsoCode();
        this.purchasePrice = detail.getPurchase().amount();
        this.salePrice = detail.getSale().amount();
        this.firstSeen = response.getDate();
        this.lastSeen = response.getDate();
        this.lastExecution = execution;
        this.latest = true;
    }

    /**
     * The purchase price as a {@link Quote} of {@link #isoCode}, it is not mapped (the columns are the amounts).
     */
    public Quote getPurchase() {
        return Quote.of(isoCode, purchasePrice);
    }

    /**
     * The sale price as a {@link Quote} of {@link #isoCode}, it is not mapped (the columns are the amounts).
     */
    public Quote getSale() {
        return Quote.of(isoCode, salePrice);
    }

    /**
     * Compares the values, not the scale: 3.4 is the same price as 3.40.
     */
    public boolean hasTheSamePrices(QueryResponseDetail detail) {
        return getPurchase().sameAmount(detail.getPurchase()) && getSale().sameAmount(detail.getSale());
    }

    public void seenAgain(Date when, Execution execution) {
        this.lastSeen = when;
        this.lastExecution = execution;
    }
}
