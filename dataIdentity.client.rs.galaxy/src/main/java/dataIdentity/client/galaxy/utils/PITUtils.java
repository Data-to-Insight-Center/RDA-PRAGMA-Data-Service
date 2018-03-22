package dataIdentity.client.galaxy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class PITUtils {
	public static String registerPID(String pit_uri, ObjectNode informationType) {
		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost(pit_uri);
		StringEntity input;
		try {
			String information_type = informationType.toString();
			input = new StringEntity(information_type);
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line;
			StringBuffer response_content = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response_content.append(line);
			}

			rd.close();

			return response_content.toString();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

	}

}
