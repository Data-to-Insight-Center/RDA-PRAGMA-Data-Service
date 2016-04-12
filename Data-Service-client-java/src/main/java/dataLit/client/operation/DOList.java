package dataLit.client.operation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import pragma.rocks.dataLit.response.PublishListResponse;

public class DOList {

	public static PublishListResponse listRegisteredDO(String service_uri) {

		try {

			URL url = new URL(service_uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			ObjectMapper mapper = new ObjectMapper();
			PublishListResponse respone = mapper.readValue(conn.getInputStream(), PublishListResponse.class);
			conn.disconnect();
			return respone;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			PublishListResponse respone = new PublishListResponse(false, null);
			return respone;

		} catch (IOException e) {
			e.printStackTrace();
			PublishListResponse respone = new PublishListResponse(false, null);
			return respone;
		}

	}

}
