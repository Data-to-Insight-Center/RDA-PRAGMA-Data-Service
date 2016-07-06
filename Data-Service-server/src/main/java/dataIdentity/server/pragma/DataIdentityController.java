package dataIdentity.server.pragma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import net.handle.hdllib.HandleException;
import dataIdentity.server.pragma.container.PIDRecord;
import dataIdentity.server.pragma.container.PropertyDefinition;
import dataIdentity.server.pragma.container.TypeDefinition;
import dataIdentity.server.pragma.mongo.PIDRepository;
import dataIdentity.server.pragma.response.MessageResponse;
import dataIdentity.server.pragma.response.PIDRecordListResponse;
import dataIdentity.server.pragma.response.PIDRecordResponse;
import dataIdentity.server.pragma.response.TypeDefinitionListResponse;
import dataIdentity.server.pragma.utils.PITUtils;

@RestController
public class DataIdentityController {
	@Autowired
	private PIDRepository pid_repository;

	@Value("${pit.uri}")
	private String pit_uri;

	@Value("${handle.server.admin.record}")
	private String admin_record;

	@Value("${handle.server.admin.id}")
	private String admin_id;

	@Value("${handle.server.admin.pkey}")
	private String admin_pkey;

	@Value("${handle.server.uri}")
	private String handle_uri;

	@Value("${handle.resolve.uri}")
	private String handle_resolve_uri;

	// Register DO with Data Identity Service for PID and PID metadata attribute
	// set
	@RequestMapping(value = "/pid/register", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOregister(@RequestParam(value = "PIDmetadata", required = true) String pidMetadata,
			@RequestParam(value = "PIDmetadataType") String pidMetadataType,
			@RequestParam(value = "DataType", required = true) String dataTypePID,
			@RequestParam(value = "DOname", required = true) String DOname,
			@RequestParam(value = "RepoID", required = true) String repoID) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode pid_metadata = (ObjectNode) mapper.readTree(pidMetadata);
			// Register DO with minimum metadata to PIT
			// Return: String PID
			String pid = PITUtils.registerPID(pit_uri.trim() + "pid/", pid_metadata);

			// Store registered PID record with repoID/DOname/DataType into
			// backend MongoDB database
			PIDRecord pid_record = new PIDRecord(pid, pidMetadataType, DOname, dataTypePID, repoID);
			pid_repository.addRecord(pid_record);

			// Return message response with registered PID record
			MessageResponse response = new MessageResponse(true, pid);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}

	}

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
	public TypeDefinitionListResponse DataTypelist() {
		// Connect to mongoDB and list all DataType and names
		// return list of type definitions from PIT service
		try {
			ObjectMapper mapper = new ObjectMapper();
			List<String> dataTypes = pid_repository.listDataType();
			List<TypeDefinition> dataTypes_definition = new ArrayList<TypeDefinition>();
			for (String datatype : dataTypes) {
				String datatypeDefinition = PITUtils.resolveDTRPID(pit_uri.trim() + "generic/", datatype);
				TypeDefinition typeDef = mapper.readValue(datatypeDefinition, TypeDefinition.class);
				dataTypes_definition.add(typeDef);
			}
			// Construct return message
			TypeDefinitionListResponse response = new TypeDefinitionListResponse(true, dataTypes_definition);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			TypeDefinitionListResponse response = new TypeDefinitionListResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/pid/set/landingpage")
	@ResponseBody
	public MessageResponse setLandingpage(@RequestParam(value = "PID", required = true) String pid,
			@RequestParam(value = "landingpage", required = true) String url) throws HandleException {
		MessageResponse response = PITUtils.setResourceLink(pid, "URL", url, admin_record, Integer.parseInt(admin_id),
				admin_pkey);
		return response;
	}

	@RequestMapping("/pid/set/metadataURL")
	@ResponseBody
	public MessageResponse setMetadataURL(@RequestParam(value = "PID", required = true) String pid,
			@RequestParam(value = "metadataURL", required = true) String url)
					throws HandleException, JsonParseException, JsonMappingException, IOException {
		// Retrieve PID metadata type definition from PIT service
		// Check if PID metadata has Metadata address property
		PIDRecord record = pid_repository.findRecordByPID(pid);
		String pidMetadataType = record.getPIDMetadataType();
		String datatypeDefinition = PITUtils.resolveDTRPID(pit_uri.trim() + "generic/", pidMetadataType);
		ObjectMapper mapper = new ObjectMapper();
		TypeDefinition typeDef = mapper.readValue(datatypeDefinition, TypeDefinition.class);
		Map<String, PropertyDefinition> propertiesDef = typeDef.getProperties();

		boolean exist = false;
		String propertyPID = "";
		for (Entry<String, PropertyDefinition> property : propertiesDef.entrySet()) {
			if (property.getValue().getName().equalsIgnoreCase("Metadata address")) {
				exist = true;
				propertyPID = property.getKey();
			}
		}

		if (exist) {
			MessageResponse response = PITUtils.setResourceLink(pid, propertyPID, url, admin_record,
					Integer.parseInt(admin_id), admin_pkey);
			return response;
		}
		{
			MessageResponse response = new MessageResponse(false, "No metadata address property identified");
			return response;
		}
	}

	// Get parsed PID metadata directly
	// For actor 3 middleware service
	@RequestMapping("/pid/resolvePID")
	@ResponseBody
	public MessageResponse resolvePID(@RequestParam(value = "PID", required = true) String pid)
			throws JsonParseException, JsonMappingException, IOException {
		String pid_type = PITUtils.peekPID(pit_uri.trim() + "peek/", pid);
		pid_type = pid_type.replace("\"","");
		if (pid_type.equalsIgnoreCase("object")) {
			Map<String, String> pid_metadata = PITUtils.resolveObjectPID(pit_uri.trim() + "generic/", pid);

			// Get PID metadata type definition from PIT service
			PIDRecord record = pid_repository.findRecordByPID(pid);
			String pidMetadataType = record.getPIDMetadataType();
			String datatypeDefinition = PITUtils.resolveDTRPID(pit_uri.trim() + "generic/", pidMetadataType);
			ObjectMapper mapper = new ObjectMapper();
			TypeDefinition typeDef = mapper.readValue(datatypeDefinition, TypeDefinition.class);
			Map<String, PropertyDefinition> propertiesDef = typeDef.getProperties();

			// Use PID metadata type definition to resolve PID metadata to human
			// readable format
			Map<String, String> parsed_pid_metadata = new HashMap<String, String>();
			for (String property : pid_metadata.keySet()) {
				if (propertiesDef.containsKey(property)) {
					PropertyDefinition propertyDef = propertiesDef.get(property);
					parsed_pid_metadata.put(propertyDef.getName(), pid_metadata.get(property));
				}
			}

			String result = mapper.writeValueAsString(parsed_pid_metadata);
			MessageResponse response = new MessageResponse(true, result);
			return response;

		} else if (pid_type.equalsIgnoreCase("PROPERTY") || pid_type.equalsIgnoreCase("TYPE")
				|| pid_type.equalsIgnoreCase("PROFILE")) {
			String datatypeDefinition = PITUtils.resolveDTRPID(pit_uri.trim() + "generic/", pid);
			MessageResponse response = new MessageResponse(true, datatypeDefinition);
			return response;
		} else {
			MessageResponse response = new MessageResponse(false, "Identifier not known");
			return response;
		}
	}

	@RequestMapping("/PID/resolveRepoID")
	@ResponseBody
	public MessageResponse resolveRepoID(@RequestParam(value = "repoID", required = true) String repoID)
			throws JsonParseException, JsonMappingException, IOException {
		PIDRecord record = pid_repository.findRecordByrepoID(repoID);
		String pid = record.getPID();
		String pidMetadataType = record.getPIDMetadataType();

		Map<String, String> pid_metadata = PITUtils.resolveObjectPID(pit_uri.trim() + "generic/", pid);
		String datatypeDefinition = PITUtils.resolveDTRPID(pit_uri.trim() + "generic/", pidMetadataType);
		ObjectMapper mapper = new ObjectMapper();
		TypeDefinition typeDef = mapper.readValue(datatypeDefinition, TypeDefinition.class);
		Map<String, PropertyDefinition> propertiesDef = typeDef.getProperties();

		// Use PID metadata type definition to resolve PID metadata to human
		// readable format
		Map<String, String> parsed_pid_metadata = new HashMap<String, String>();
		for (String property : pid_metadata.keySet()) {
			if (propertiesDef.containsKey(property)) {
				PropertyDefinition propertyDef = propertiesDef.get(property);
				parsed_pid_metadata.put(propertyDef.getName(), pid_metadata.get(property));
			}
		}

		String result = mapper.writeValueAsString(parsed_pid_metadata);
		MessageResponse response = new MessageResponse(true, result);
		return response;
	}
}
