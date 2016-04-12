package dataLit.client.operation;

import java.util.Map;
import dataLit.client.utils.PropertyReader;
import pragma.rocks.dataLit.container.InformationType;
import pragma.rocks.dataLit.response.InformationTypeResponse;
import pragma.rocks.dataLit.utils.PITUtils;

public class DOFind {
	public static InformationTypeResponse findRegisteredDO(String service_uri, String pid, String properties_path) {

		InformationType informationtype = new InformationType();
		Map<String, String> metadata = PITUtils.resolvePID(service_uri, pid);
		for (Map.Entry<String, String> entry : metadata.entrySet()) {
			if (entry.getKey()
					.equalsIgnoreCase(PropertyReader.getInstance(properties_path).getProperty("pit.record.title"))) {
				informationtype.setTitle(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(
					PropertyReader.getInstance(properties_path).getProperty("pit.record.landingpageAddr"))) {
				informationtype.setLandingpageAddr(entry.getValue());
			}

			if (entry.getKey()
					.equalsIgnoreCase(PropertyReader.getInstance(properties_path).getProperty("pit.record.datatype"))) {
				informationtype.setDatatype(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(
					PropertyReader.getInstance(properties_path).getProperty("pit.record.creationDate"))) {
				informationtype.setCreationDate(entry.getValue());
			}

			if (entry.getKey()
					.equalsIgnoreCase(PropertyReader.getInstance(properties_path).getProperty("pit.record.checksum"))) {
				informationtype.setChecksum(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(
					PropertyReader.getInstance(properties_path).getProperty("pit.record.predecessorID"))) {
				informationtype.setPredecessorID(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(
					PropertyReader.getInstance(properties_path).getProperty("pit.record.successorID"))) {
				informationtype.setSuccessorID(entry.getValue());
			}
		}
		InformationTypeResponse response = new InformationTypeResponse(true, pid, informationtype);
		return response;
	}
}
