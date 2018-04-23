package edu.indiana.pragma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import edu.indiana.pragma.util.Constants;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import net.handle.hdllib.HandleException;
import edu.indiana.pragma.container.PIDProvider;
import edu.indiana.pragma.container.PIDRecord;
import edu.indiana.pragma.container.PropertyDefinition;
import edu.indiana.pragma.container.TypeDefinition;
import edu.indiana.pragma.mongo.PIDRepository;
import edu.indiana.pragma.response.MessageResponse;
import edu.indiana.pragma.response.PIDRecordListResponse;
import edu.indiana.pragma.response.PIDRecordResponse;
import edu.indiana.pragma.response.TypeDefinitionListResponse;
import edu.indiana.pragma.utils.EZIDUtils;
import edu.indiana.pragma.utils.PITUtils;

public class DataIdentityController {

	private PIDRepository pid_repository;
	private String pit_uri = Constants.pitURL;
	private String admin_record = Constants.adminRecord;
	private String admin_id = Constants.adminId;
	private String admin_pkey = Constants.adminPkey;
	private String handle_uri = Constants.handleURI;
	private String handle_resolve_uri = Constants.handleresolveURI;
	private String ezid_server = Constants.ezidServer;
	private String ezid_shoulder = Constants.ezidShoulder;
	private String ezid_username = Constants.ezidUsername;
	private String ezid_password = Constants.ezidPassword;

	private CacheControl control = new CacheControl();

	// Register DO with Data Identity Service for PID - handle and PID metadata
	// attribute
	// set
	@POST
	@Path("/pid/register/handle")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse DOregisterHandle(@FormParam("PIDmetadata") String pidMetadata,
			@QueryParam("PIDmetadataType") String pidMetadataType,
			@QueryParam("DataType") String dataTypePID,
			@QueryParam("DOname") String DOname,
			@QueryParam("RepoID") String repoID) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode pid_metadata = (ObjectNode) mapper.readTree(pidMetadata);
			// Register DO with minimum metadata to PIT
			// Return: String PID
			String pid = PITUtils.registerPID(pit_uri.trim() + "pid/", pid_metadata);

			// Store registered PID record with repoID/DOname/DataType into
			// backend MongoDB database
			PIDRecord pid_record = new PIDRecord(pid, pidMetadataType, DOname, dataTypePID, repoID, PIDProvider.handle);
			pid_repository.addRecord(pid_record);

			// Return message response with registered PID record
			MessageResponse response = new MessageResponse(true, pid);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			MessageResponse response = new MessageResponse(false, null);
			return response;
			//return response;
		}

	}

	// Register DO with Data Identity Service for PID - an existing PID
		// attribute
		// set
		@POST
		@Path("/pid/register/pid")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public MessageResponse DOregisterPID(@QueryParam("PID") String pid,
				@QueryParam("PIDmetadataType") String pidMetadataType,
				@QueryParam("DataType") String dataTypePID,
				@QueryParam("DOname") String DOname,
				@QueryParam("RepoID") String repoID) {
			try {
				// Store registered PID record with repoID/DOname/DataType into
				// backend MongoDB database
				PIDRecord pid_record = new PIDRecord(pid, pidMetadataType, DOname, dataTypePID, repoID, PIDProvider.handle);
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
	
	// Register DO with Data Identity Service for PID - EZID-erc and PID
	// metadata
	// attribute
	// set
	@POST
	@Path("/pid/register/ark")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse DOregisterARK(@QueryParam("PIDmetadata") String pidMetadata,
			@QueryParam("PIDmetadataType") String pidMetadataType,
			@QueryParam("DataType") String dataTypePID,
			@QueryParam("DOname") String DOname,
			@QueryParam("RepoID") String repoID) {
		try {
			// Register DO with minimum metadata to PIT
			// Return: String PID
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode pid_metadata = (ObjectNode) mapper.readTree(pidMetadata);
			@SuppressWarnings("unchecked")
			HashMap<String, String> map_pid_metadata = mapper.convertValue(pid_metadata, HashMap.class);

			String pid = EZIDUtils.registerEZID(ezid_server, ezid_shoulder, ezid_username, ezid_password,
					map_pid_metadata);

			// Store registered PID record with repoID/DOname/DataType into
			// backend MongoDB database
			PIDRecord pid_record = new PIDRecord(pid, pidMetadataType, DOname, dataTypePID, repoID, PIDProvider.ark);
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
	@GET
	@Path("/pidrepo/find/repoID")
	@Produces(MediaType.APPLICATION_JSON)
	public PIDRecordResponse findRepoID(@QueryParam("repoID") String repoID) {
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

	@GET
	@Path("/pidrepo/find/PID")
	@Produces(MediaType.APPLICATION_JSON)
	public PIDRecordResponse findPID(@QueryParam("PID") String PID) {
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

	@GET
	@Path("/pidrepo/list/DO")
	@Produces(MediaType.APPLICATION_JSON)
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

	@GET
	@Path("/pidrepo/list/DObyDTR")
	@Produces(MediaType.APPLICATION_JSON)
	public PIDRecordListResponse DObyDTRlist(@QueryParam("DataType") String dataType) {
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

	@GET
	@Path("/pidrepo/list/DataType")
	@Produces(MediaType.APPLICATION_JSON)
	public TypeDefinitionListResponse DataTypelist() {
		// Connect to mongoDB and list all DataType and names
		// return list of type definitions from PIT service
		try {
			RestTemplate restTemplate = new RestTemplate();
			List<String> dataTypes = pid_repository.listDataType();
			List<TypeDefinition> dataTypes_definition = new ArrayList<TypeDefinition>();
			for (String datatype : dataTypes) {
				final TypeDefinition typeDef = restTemplate.getForObject(pit_uri.trim() + "generic/" + datatype,
						TypeDefinition.class);
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

	@GET
	@Path("/pid/set/landingpage")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse setLandingpage(@QueryParam("PID") String pid,
			@QueryParam("landingpage") String url) throws HandleException {
		MessageResponse response = PITUtils.setResourceLink(pid, "URL", url, admin_record, Integer.parseInt(admin_id),
															admin_pkey);
		return response;
	}

	@GET
	@Path("/pid/set/metadataURL")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse setMetadataURL(@QueryParam("PID") String pid,
			@QueryParam("metadataURL") String url)
					throws HandleException, JsonParseException, JsonMappingException, IOException {
		// Retrieve PID metadata type definition from PIT service
		// Check if PID metadata has Metadata address property
		PIDRecord record = pid_repository.findRecordByPID(pid);
		String pidMetadataType = record.getPIDMetadataType();

		RestTemplate restTemplate = new RestTemplate();
		final TypeDefinition typeDef = restTemplate.getForObject(pit_uri.trim() + "generic/" + pidMetadataType,
				TypeDefinition.class);
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
	@GET
	@Path("/pid/resolvePID/handle")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse resolvePIDHandle(@QueryParam("PID") String pid)
			throws JsonParseException, JsonMappingException, IOException {
		String pid_type = PITUtils.peekPID(pit_uri.trim() + "peek/", pid);
		pid_type = pid_type.replace("\"", "");
		if (pid_type.equalsIgnoreCase("object")) {
			Map<String, String> pid_metadata = PITUtils.resolveObjectPID(handle_resolve_uri.trim(), pid);

			// Get PID metadata type definition from PIT service
			PIDRecord record = pid_repository.findRecordByPID(pid);
			String pidMetadataType = record.getPIDMetadataType();
			RestTemplate restTemplate = new RestTemplate();
			final TypeDefinition typeDef = restTemplate.getForObject(pit_uri.trim() + "generic/" + pidMetadataType,
					TypeDefinition.class);
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

			parsed_pid_metadata.put("pid", pid);

			ObjectMapper mapper = new ObjectMapper();
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

	@GET
	@Path("/pid/resolvePID/ark")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse resolvePIDARK(@QueryParam("PID") String pid)
			throws JsonParseException, JsonMappingException, IOException {
		try {
			Map<String, String> pid_metadata = EZIDUtils.resolveEZID(ezid_server, pid);

			// Get PID metadata type definition from PIT service
			PIDRecord record = pid_repository.findRecordByPID(pid);
			String pidMetadataType = record.getPIDMetadataType();
			RestTemplate restTemplate = new RestTemplate();
			final TypeDefinition typeDef = restTemplate.getForObject(pit_uri.trim() + "generic/" + pidMetadataType,
					TypeDefinition.class);
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
			parsed_pid_metadata.put("_target", pid_metadata.get("_target"));
			parsed_pid_metadata.put("pid", pid);

			ObjectMapper mapper = new ObjectMapper();
			String result = mapper.writeValueAsString(parsed_pid_metadata);
			MessageResponse response = new MessageResponse(true, result);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@GET
	@Path("/PID/resolveRepoID")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse resolveRepoIDHandle(@QueryParam("repoID") String repoID)
			throws Exception {
		PIDRecord record = pid_repository.findRecordByrepoID(repoID);
		String pid = record.getPID();
		String pidMetadataType = record.getPIDMetadataType();
		PIDProvider pidProvider = record.getPidProvider();
		Map<String, String> pid_metadata = new HashMap<String, String>();
		if (pidProvider.equals(PIDProvider.handle))
			pid_metadata = PITUtils.resolveObjectPID(handle_resolve_uri.trim(), pid);
		else if (pidProvider.equals(PIDProvider.ark))
			pid_metadata = EZIDUtils.resolveEZID(ezid_server, pid);

		RestTemplate restTemplate = new RestTemplate();
		final TypeDefinition typeDef = restTemplate.getForObject(pit_uri.trim() + "generic/" + pidMetadataType,
				TypeDefinition.class);
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

		if (pidProvider.equals(PIDProvider.ark))
			parsed_pid_metadata.put("_target", pid_metadata.get("_target"));

		parsed_pid_metadata.put("pid", pid);

		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(parsed_pid_metadata);
		MessageResponse response = new MessageResponse(true, result);
		return response;
	}
}
