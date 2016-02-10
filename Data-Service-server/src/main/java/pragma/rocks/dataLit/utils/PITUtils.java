package pragma.rocks.dataLit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static Map<String, String> resolvePID(String pid_uri, String pid) {
		HttpClient client = new DefaultHttpClient();

		HttpGet request = new HttpGet(pid_uri + pid);
		try {
			HttpResponse response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line;
			StringBuffer response_content = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response_content.append(line);
			}

			rd.close();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(response_content.toString());
			JsonNode values = actualObj.findPath("values");

			List<String> metadata_keys = values.findValuesAsText("type");
			List<String> metadata_values = values.findValuesAsText("value");

			Map<String, String> metadata = new HashMap<String, String>();

			for (int i = 0; i < metadata_keys.size(); i++) {
				metadata.put(metadata_keys.get(i), metadata_values.get(i));
			}
			return metadata;
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

	/*
	 * public static void main(String[] args) {
	 * 
	 * Map<String, String> metadata =
	 * resolvePID("http://hdl.handle.net/api/handles/",
	 * "11723/06ce9adb-cc64-40aa-afd5-583cec36a93f"); for (Map.Entry<String,
	 * String> entry : metadata.entrySet()) { String key =
	 * entry.getKey().toString(); String value = entry.getValue();
	 * System.out.println("key, " + key + " value " + value); } }
	 */

}
