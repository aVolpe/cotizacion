package py.com.volpe.cotizacion;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Service
public class HTTPHelper {


	public String doGet(String uri) {

		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			con.setRequestProperty("Content-Type", "application/json");


			int status = con.getResponseCode();
			if (status != 200)
				throw new AppException(status, "Invalid status returned querying URL " + uri);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			return content.toString();
		} catch (IOException e) {
			throw new AppException(500, "Invalid response", e);
		}
	}

}
