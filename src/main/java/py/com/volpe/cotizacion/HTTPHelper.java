package py.com.volpe.cotizacion;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

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

	public String doPost(String uri, ImmutableMap<String, String> data) {
		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);


			StringJoiner sj = new StringJoiner("&");
			for (Map.Entry<String, String> entry : data.entrySet())
				sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
						+ URLEncoder.encode(entry.getValue(), "UTF-8"));
			byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			con.setFixedLengthStreamingMode(length);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			con.connect();
			try (OutputStream os = con.getOutputStream()) {
				os.write(out);
			}

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
