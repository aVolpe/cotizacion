package py.com.volpe.cotizacion.repository;

import lombok.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
			"   p.id, " +
			"   p.name, " +
			"   br.id, " +
			"   br.name, " +
			"   br.latitude, " +
			"   br.longitude," +
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
			" AND qr.date IN (SELECT MAX(qr2.date) FROM QueryResponse qr2 WHERE qr2.branch = br) " +
			"ORDER BY " +
			"   qr.date DESC," +
			"   p.name," +
			"   br.name")
	List<ByIsoCodeResult> getMaxByPlaceInISO(String isoCode);

	@Query("SELECT qrd.isoCode " +
			"FROM QueryResponseDetail qrd " +
			"GROUP BY qrd.isoCode " +
			"ORDER BY count(qrd.isoCode) DESC, qrd.isoCode")
	List<String> getAvailableISO();

	@Value
	class ByIsoCodeResult {
		long placeId;
		String placeName;

		long branchId;
		String branchName;

		Double branchLatitude;
		Double branchLongitude;

		long queryId;
		Date queryDate;

		long queryDetailId;
		long salePrice;
		long purchasePrice;

	}

}
