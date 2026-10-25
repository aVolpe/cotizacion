package py.com.volpe.cotizacion.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import py.com.volpe.cotizacion.domain.ExchangeRate;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    /**
     * The rates of the given currency seen by the last execution.
     */
    @Query("SELECT " +
            " new py.com.volpe.cotizacion.repository.ExchangeRateRepository$ByIsoCodeResult(" +
            "   r.id, " +
            "   p, " +
            "   br," +
            "   e.id," +
            "   r.lastSeen," +
            "   r.id," +
            "   r.salePrice," +
            "   r.purchasePrice" +
            " ) " +
            "FROM ExchangeRate r " +
            "LEFT JOIN r.branch br " +
            "JOIN r.place p " +
            "JOIN r.lastExecution e " +
            "WHERE " +
            "      r.isoCode = ?1 " +
            "  AND r.latest = true " +
            "  AND e.id = (SELECT MAX(e2.id) FROM Execution e2) " +
            "ORDER BY " +
            "   r.lastSeen DESC")
    List<ByIsoCodeResult> getMaxByPlaceInISO(String isoCode);

    @Query("SELECT r.isoCode " +
            "FROM ExchangeRate r " +
            "GROUP BY r.isoCode " +
            "ORDER BY count(r.isoCode) DESC, r.isoCode")
    List<String> getAvailableISO();

    /**
     * The newest rate of every currency of a place queried as a whole.
     */
    @Query("SELECT r FROM ExchangeRate r WHERE r.latest = true AND r.place = ?1 AND r.branch IS NULL")
    List<ExchangeRate> findLatest(Place place);

    /**
     * The newest rate of every currency of a branch.
     */
    @Query("SELECT r FROM ExchangeRate r WHERE r.latest = true AND r.place = ?1 AND r.branch = ?2")
    List<ExchangeRate> findLatest(Place place, PlaceBranch branch);

    @Value
    class ByIsoCodeResult {
        /**
         * The id of the exchange rate (unique in the response).
         */
        private long id;

        @JsonIgnoreProperties("branches")
        private Place place;

        @JsonIgnoreProperties("place")
        private PlaceBranch branch;

        /**
         * The execution that saw this rate.
         */
        private long queryId;
        /**
         * When the rate was seen for the last time.
         */
        private Date queryDate;

        /**
         * Same as {@link #id}, kept for compatibility.
         */
        private long queryDetailId;
        private BigDecimal salePrice;
        private BigDecimal purchasePrice;

    }

}
