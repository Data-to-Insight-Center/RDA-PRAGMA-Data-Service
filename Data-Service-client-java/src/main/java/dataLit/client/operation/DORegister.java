package dataLit.client.operation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import pragma.rocks.dataLit.container.InformationType;
import pragma.rocks.dataLit.response.MessageResponse;

public class DORegister {
	public static MessageResponse registerDO(String service_uri, InformationType information_type) {
		try {

			URL url = new URL(service_uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String input = ow.writeValueAsString(information_type);

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
