package pragma.rocks.dataLit.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;

public class DataDiffUtils {
	public static JsonNode dataDiff(JsonNode source, JsonNode target) {
		JsonNode diff = JsonDiff.asJson(source, target);
		return diff;
	}

	// public static void main(String[] args) throws JsonProcessingException,
	// IOException {
	// String jsonString = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
	// ObjectMapper mapper = new ObjectMapper();
	// JsonNode node = mapper.readTree(jsonString);
	//
	// String json2String = "{\"k1\":\"v1\",\"k2\":\"v3\",\"k3\":\"v4\"}";
	// JsonNode node2 = mapper.readTree(json2String);
	//
	// JsonNode diff = dataDiff(node, node2);
	// System.out.println(diff.toString());
	//
	// }
}
