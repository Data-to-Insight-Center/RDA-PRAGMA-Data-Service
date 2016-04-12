package dataLit.client.operation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import pragma.rocks.dataLit.response.MessageResponse;

public class DOUpdate {
	public MessageResponse updateResourceURL(String resourceURL, String pid, String service_uri) {
		try {

			URL url = new URL(service_uri + "?pid=" + pid + "&url=" + resourceURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			ObjectMapper mapper = new ObjectMapper();
			MessageResponse response = mapper.readValue(conn.getInputStream(), MessageResponse.class);
			conn.disconnect();

			return response;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			MessageResponse respone = new MessageResponse(false, null);
			return respone;

		} catch (IOException e) {
			e.printStackTrace();
			MessageResponse respone = new MessageResponse(false, null);
			return respone;
		}
	}

	public MessageResponse updateMetadata(String metadata, String pid, String service_uri) {
		try {

			URL url = new URL(service_uri + "?pid=" + pid);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String input = ow.writeValueAsString(metadata);

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			ObjectMapper mapper = new ObjectMapper();
			MessageResponse response = mapper.readValue(conn.getInputStream(), MessageResponse.class);
			conn.disconnect();

			return response;

		} catch (MalformedURLException e) {

			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;

		} catch (IOException e) {

			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);

			return response;
		}
	}
}
