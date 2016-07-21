package dataIdentity.server.pragma.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtils {
	public static JsonNode readUrl2JsonNode(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(buffer.toString());
			return actualObj;
		} finally {
			if (reader != null)
				reader.close();
		}
	}
}
