package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.Pair;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Arturo Volpe
 * @since 10/5/18
 */
@RunWith(MockitoJUnitRunner.class)
public class FeCambiosTest {

	@Mock
	private HTTPHelper wsHelper;

	@InjectMocks
	private FeCambios gatherer;

	@Test
	public void doQuery() throws Exception {


		String data = IOUtils.toString(getClass().getResourceAsStream("fecambios_exchange_data.html"), "UTF-8");
		Mockito.when(wsHelper.doPost(Mockito.any(), Mockito.any())).thenReturn(data);

		Place p = new Place();
		PlaceBranch pb = new PlaceBranch();
		pb.setRemoteCode("8");
		List<QueryResponse> result = gatherer.doQuery(p, Collections.singletonList(pb));

		assertEquals(1, result.size());

		assertNotNull(result.get(0).getDetails());
		assertNotNull(result.get(0).getDate());
		assertEquals(10, result.get(0).getDetails().size());


		QueryResponseDetail qrdAsuncion = result.get(0).getDetails()
				.stream()
				.peek(System.out::println)
				.filter(d -> d.getIsoCode().equals("USD"))
				.findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

		assertEquals(5830, qrdAsuncion.getPurchasePrice());
		assertEquals(5890, qrdAsuncion.getSalePrice());
	}

	@Test
	public void build() throws Exception {

		String data = IOUtils.toString(getClass().getResourceAsStream("fecambios_branches.html"), "UTF-8");

		Mockito.when(wsHelper.doGet(Mockito.any())).thenReturn(data);

		Place p = gatherer.build();


		assertNotNull(p);
		assertEquals(gatherer.getCode(), p.getCode());

		assertEquals(5, p.getBranches().size());


	}

	@Test
	public void extractLatLng() throws Exception {

		Pair<Double, Double> data = gatherer.extractLatLng("http://maps.google.es/maps?q=-25.281095,-57.636751&amp;output=embed");

		assertEquals(-25.281095, data.getFirst(), 0);
		assertEquals(-57.636751, data.getSecond(), 0);

		data = gatherer.extractLatLng("http://maps.google.es/maps?q=-25.281095,-57.636751");

		assertEquals(-25.281095, data.getFirst(), 0);
		assertEquals(-57.636751, data.getSecond(), 0);
	}
}