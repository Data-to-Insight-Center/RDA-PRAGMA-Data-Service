package pragma.rocks.dataIdentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import net.handle.hdllib.HandleException;
import pragma.rocks.dataIdentity.container.PIDMetadata;
import pragma.rocks.dataIdentity.container.PIDRecord;
import pragma.rocks.dataIdentity.mongo.PIDRepository;
import pragma.rocks.dataIdentity.response.MessageListResponse;
import pragma.rocks.dataIdentity.response.MessageResponse;
import pragma.rocks.dataIdentity.response.PIDMetadataResponse;
import pragma.rocks.dataIdentity.response.PIDRecordListResponse;
import pragma.rocks.dataIdentity.response.PIDRecordResponse;
import pragma.rocks.dataIdentity.utils.JsonUtils;
import pragma.rocks.dataIdentity.utils.PITUtils;

@RestController
public class PIDRepoController {
	@Autowired
	private PIDRepository pid_repository;

	@Value("${handle.server.admin.record}")
	private String admin_record;

	@Value("${handle.server.admin.id}")
	private String admin_id;

	@Value("${handle.server.admin.pkey}")
	private String admin_pkey;

	@Value("${handle.server.uri}")
	private String handle_uri;

	@Value("${dtr.uri}")
	private String dtr_uri;

	@Value("${pit.record.metadataURL}")
	private String pit_metadataURL;

	@Value("${pit.record.creationDate}")
	private String pit_creationdate;

	@Value("${pit.record.checksum}")
	private String pit_checksum;

	@Value("${pit.record.predecessorID}")
	private String pit_predecessorID;

	@Value("${pit.record.successorID}")
	private String pit_successorID;

	// PID records in PIDRepo can only be read and listed;
	// Do not support update and delete
	// Allow update resource link for PID record itself

	@RequestMapping("/pidrepo/find/repoID")
	@ResponseBody
	public PIDRecordResponse findRepoID(@RequestParam(value = "repoID", required = true) String repoID) {
		// Connect to MongoDB and return repoID as reponse
		// return string repoID
		try {
			PIDRecord record = pid_repository.findRecordByrepoID(repoID);
			// Construct return message
			PIDRecordResponse response = new PIDRecordResponse(true, record);
			return response;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			PIDRecordResponse response = new PIDRecordResponse(false, null);
			return response;
		}

	}

	@RequestMapping("/pidrepo/find/PID")
	@ResponseBody
	public PIDRecordResponse findPID(@RequestParam(value = "PID", required = true) String PID) {
		// Connect to MongoDB and return PID as reponse
		// return string PID
		try {
			PIDRecord record = pid_repository.findRecordByPID(PID);
			// Construct return message
			PIDRecordResponse response = new PIDRecordResponse(true, record);
			return response;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			PIDRecordResponse response = new PIDRecordResponse(false, null);
			return response;
		}

	}

	@RequestMapping("/pidrepo/list/DO")
	@ResponseBody
	public PIDRecordListResponse DOlist() {
		// Connect to mongoDB and list all PID records
		// return list of PID records
		try {
			List<PIDRecord> records = pid_repository.listAll();

			// Construct return message
			PIDRecordListResponse response = new PIDRecordListResponse(true, records);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			PIDRecordListResponse response = new PIDRecordListResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/pidrepo/list/DObyDTR")
	@ResponseBody
	public PIDRecordListResponse DObyDTRlist(@RequestParam(value = "DataType", required = true) String dataType) {
		// Connect to mongoDB and list all PID records by data type PID
		// return list of PID records
		try {
			List<PIDRecord> records = pid_repository.listAllByDTR(dataType);

			// Construct return message
			PIDRecordListResponse response = new PIDRecordListResponse(true, records);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			PIDRecordListResponse response = new PIDRecordListResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/pidrepo/list/DataType")
	@ResponseBody
	public MessageListResponse DataTypelist() {
		// Connect to mongoDB and list all DataType and names
		// return list of PID records
		try {
			List<String> dataTypes = pid_repository.listDataType();
			List<String> dataTypes_definition = new ArrayList<String>();
			for (String datatype : dataTypes) {
				String url = dtr_uri + datatype;
				JsonNode datatype_definition = JsonUtils.readUrl2JsonNode(url);
				dataTypes_definition.add(datatype_definition.toString());
			}
			// Construct return message
			MessageListResponse response = new MessageListResponse(true, dataTypes_definition);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageListResponse response = new MessageListResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/pidrepo/set/landingpage")
	@ResponseBody
	public MessageResponse setLandingpage(@RequestParam(value = "PID", required = true) String pid,
			@RequestParam(value = "landingpage", required = true) String url) throws HandleException {
		MessageResponse response = PITUtils.setResourceLink(pid, "URL", url, admin_record, Integer.parseInt(admin_id),
				admin_pkey);
		return response;
	}

	@RequestMapping("/pidrepo/set/metadataURL")
	@ResponseBody
	public MessageResponse setMetadataURL(@RequestParam(value = "PID", required = true) String pid,
			@RequestParam(value = "metadataURL", required = true) String url) throws HandleException {
		MessageResponse response = PITUtils.setResourceLink(pid, pit_metadataURL, url, admin_record,
				Integer.parseInt(admin_id), admin_pkey);
		return response;
	}

	// Get parsed PID metadata directly
	// For actor 3 middleware service
	@RequestMapping("/pidrepo/resolvePID")
	@ResponseBody
	public PIDMetadataResponse resolvePID(@RequestParam(value = "PID", required = true) String pid) {
		Map<String, String> pid_response = PITUtils.resolvePID(handle_uri, pid);

		PIDMetadata pid_metadata = new PIDMetadata();
		pid_metadata.setPID(pid);
		for (Map.Entry<String, String> entry : pid_response.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("URL")) {
				pid_metadata.setLandingpageAddr(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_metadataURL)) {
				pid_metadata.setMetadataURL(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_creationdate)) {
				pid_metadata.setCreationDate(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_checksum)) {
				pid_metadata.setChecksum(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_predecessorID)) {
				pid_metadata.setPredecessorID(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_successorID)) {
				pid_metadata.setSuccessorID(entry.getValue());
			}
		}

		PIDMetadataResponse response = new PIDMetadataResponse(true, pid_metadata);
		return response;
	}

	@RequestMapping("/pidrepo/resolveRepoID")
	@ResponseBody
	public PIDMetadataResponse resolveRepoID(@RequestParam(value = "repoID", required = true) String repoID) {
		PIDRecord record = pid_repository.findRecordByrepoID(repoID);
		String pid = record.getPID();
		Map<String, String> pid_response = PITUtils.resolvePID(handle_uri, pid);

		PIDMetadata pid_metadata = new PIDMetadata();
		pid_metadata.setPID(pid);
		for (Map.Entry<String, String> entry : pid_response.entrySet()) {
			if (entry.getKey().equalsIgnoreCase("URL")) {
				pid_metadata.setLandingpageAddr(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_metadataURL)) {
				pid_metadata.setMetadataURL(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_creationdate)) {
				pid_metadata.setCreationDate(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_checksum)) {
				pid_metadata.setChecksum(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_predecessorID)) {
				pid_metadata.setPredecessorID(entry.getValue());
			}

			if (entry.getKey().equalsIgnoreCase(pit_successorID)) {
				pid_metadata.setSuccessorID(entry.getValue());
			}
		}

		PIDMetadataResponse response = new PIDMetadataResponse(true, pid_metadata);
		return response;
	}

	@RequestMapping("/pidrepo/resolveDTR")
	@ResponseBody
	public MessageResponse resolveDTR(@RequestParam(value = "DataType", required = true) String dataType) {
		// Connect to DTR API and get Data Type definition doc
		// return json doc as Data Type definition
		try {
			String url = dtr_uri + dataType;
			JsonNode datatype_definition = JsonUtils.readUrl2JsonNode(url);

			// Construct return message
			MessageResponse response = new MessageResponse(true, datatype_definition.toString());
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

}
