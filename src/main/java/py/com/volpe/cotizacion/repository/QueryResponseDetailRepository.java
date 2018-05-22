package py.com.volpe.cotizacion.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.Date;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
public interface QueryResponseDetailRepository extends JpaRepository<QueryResponseDetail, Long> {

    @Query("SELECT " +
            " new py.com.volpe.cotizacion.repository.QueryResponseDetailRepository$ByIsoCodeResult(" +
            "   qrd.id, " +
            "   p, " +
            "   br," +
            "   qr.id," +
            "   qr.date," +
            "   qrd.id," +
            "   qrd.salePrice," +
            "   qrd.purchasePrice" +
            " ) " +
            "FROM QueryResponseDetail qrd " +
            "JOIN qrd.queryResponse qr " +
            "JOIN qr.branch br " +
            "JOIN br.place p " +
            "WHERE " +
            "     qrd.isoCode = ?1 " +
            " AND qr.id IN (SELECT MAX(qr2.id) FROM QueryResponse qr2 WHERE qr2.branch = br) " +
            "ORDER BY " +
            "   qr.date DESC")
    List<ByIsoCodeResult> getMaxByPlaceInISO(String isoCode);

    @Query("SELECT qrd.isoCode " +
            "FROM QueryResponseDetail qrd " +
            "GROUP BY qrd.isoCode " +
            "ORDER BY count(qrd.isoCode) DESC, qrd.isoCode")
    List<String> getAvailableISO();

    @Value
    class ByIsoCodeResult {
        private long id;

        @JsonIgnoreProperties("branches")
        private Place place;

        @JsonIgnoreProperties("place")
        private PlaceBranch branch;

        private long queryId;
        private Date queryDate;

        private long queryDetailId;
        private long salePrice;
        private long purchasePrice;

    }

}
