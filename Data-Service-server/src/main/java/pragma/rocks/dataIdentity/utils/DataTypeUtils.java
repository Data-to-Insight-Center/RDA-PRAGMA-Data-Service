package pragma.rocks.dataIdentity.utils;

import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public class DataTypeUtils {

	public static boolean checkNested(JsonNode doc) {
		// Check if Json node has nested properties
		if (doc.has("properties"))
			return true;
		else
			return false;
	}

	public static void getDoc(String dataTypePID, ObjectNode result, String resolve_host) throws Exception {

		JsonNode actualObj = JsonUtils.readUrl2JsonNode(resolve_host + dataTypePID);

		if (!actualObj.has("properties")) {
		} else {
			JsonNode properties = actualObj.findPath("properties");

			List<JsonNode> names = properties.findValues("name");
			List<JsonNode> identifiers = properties.findValues("identifier");

			for (int i = 0; i < names.size(); i++) {
				JsonNode subObj = JsonUtils
						.readUrl2JsonNode(resolve_host + identifiers.get(i).toString().replace("\"", ""));
				if (!checkNested(subObj)) {
					result.put(names.get(i).toString().replace("\"", ""), "");
				} else {
					ObjectNode childObj = result.putObject(names.get(i).toString().replace("\"", ""));
					getDoc(identifiers.get(i).toString().replace("\"", ""), childObj, resolve_host);
				}
			}

		}
	}
}
