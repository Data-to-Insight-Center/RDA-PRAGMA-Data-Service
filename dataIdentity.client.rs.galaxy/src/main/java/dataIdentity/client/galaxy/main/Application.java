package dataIdentity.client.galaxy.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

import dataIdentity.client.galaxy.container.PropertyDefinition;
import dataIdentity.client.galaxy.container.TypeDefinition;
import dataIdentity.client.galaxy.response.MessageResponse;
import dataIdentity.client.galaxy.utils.JsonUtils;
import dataIdentity.client.galaxy.utils.MD5Utils;
import dataIdentity.client.galaxy.utils.PITUtils;
import dataIdentity.client.galaxy.utils.PropertyReader;
import dataIdentity.client.galaxy.utils.ZipUtils;

public class Application {

	@SuppressWarnings({ "resource", "deprecation" })
	public static void main(String args[]) throws Exception {
		// Get args info
		String username = "";
		String provider = "";
		String analyseType = "";
		String workflow = "";
		String config_path = "";
		List<String> input_files = new ArrayList<String>();
		List<String> output_files = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-user")) {
				username = args[i + 1];
				i++;
			} else if (args[i].equalsIgnoreCase("-provider")) {
				provider = args[i + 1];
				i++;
			} else if (args[i].equalsIgnoreCase("-analyseType")) {
				analyseType = args[i + 1];
				i++;
			} else if (args[i].equalsIgnoreCase("-config")) {
				config_path = args[i + 1];
				i++;
			} else if (args[i].equalsIgnoreCase("-workflow")) {
				workflow = args[i + 1];
				i++;
			} else if (args[i].equalsIgnoreCase("-i")) {
				int j = i + 1;
				while (!args[j].equalsIgnoreCase("-o")) {
					input_files.add(args[j]);
					j++;
				}
				i = j - 1;
			} else if (args[i].equalsIgnoreCase("-o")) {
				int j = i + 1;
				while (j < args.length) {
					output_files.add(args[j]);
					j++;
				}
				i = j - 1;
			}
		}

		PropertyReader preader = PropertyReader.getInstance(config_path);

		// Get Service uri
		String repo_uri = preader.getProperty("pragma.data.repository.uri");
		String identity_uri = preader.getProperty("pragma.data.identity.uri");
		String pit_uri = preader.getProperty("pragma.pit.ext.uri");
		String gui_uri = preader.getProperty("gui.uri");

		// Get data type and PIT type PID
		String data_type = preader.getProperty("data.type.pid");
		String pid_type = preader.getProperty("pit.type.pid");

		// Create Web Client for POST, GET;
		Client client = Client.create();

		// Get PIT and data type definition from PIT service
		WebResource webResource = client.resource(pit_uri + "type/" + pid_type);

		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		String typeDefString = response.getEntity(String.class).trim();
		System.out.println(typeDefString);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		TypeDefinition typeDef = new TypeDefinition("", "", "");
		typeDef = mapper.readValue(typeDefString, TypeDefinition.class);
		Map<String, PropertyDefinition> properties = typeDef.getProperties();

		// Upload DO to PRAGMA Data Repository with Zip and metadata
		// Return: StagingDB id and repoID
		List<File> file_list = new ArrayList<File>();

		for (String input_file : input_files) {
			File file = new File(input_file);
			file_list.add(file);
		}

		for (String output_file : output_files) {
			File file = new File(output_file);
			file_list.add(file);
		}

		// Set DO harvest date to UTC as MongoDB
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		ft.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		String DOdate = ft.format(dNow);
		String DOname = username + "_" + analyseType + "_" + DOdate;

		ZipUtils.zipFiles("./" + DOname + ".zip", file_list);

		// Path path = Paths.get("./" + DOname + ".zip");

		WebResource uploadDOResource = client.resource(repo_uri + "DO/upload");
		// Add data to request param
		File fileToUpload = new File("./" + DOname + ".zip");

		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("data", fileToUpload,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		fileDataBodyPart.setContentDisposition(
				FormDataContentDisposition.name("data").fileName(fileToUpload.getName()).build());

		// Add metadata to request param

		JsonNode metadata = JsonUtils.readUrl2JsonNode(workflow);
		ObjectNode jNode = mapper.createObjectNode();
		jNode.put("metadata", metadata);
		jNode.put("DOname", DOname);
		jNode.put("DataType", data_type);
		jNode.put("DownloadingURL", "");

		// Setup Multipart Upload Content
		final MultiPart uploadDOMultiPart = new FormDataMultiPart()
				.field("metadata", jNode.toString(), MediaType.APPLICATION_JSON_TYPE).bodyPart(fileDataBodyPart);
		uploadDOMultiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

		// POST request final
		ClientResponse uploadDOResponse = uploadDOResource.type("multipart/form-data").post(ClientResponse.class,
				uploadDOMultiPart);
		String stagingID = dataIdentity.client.galaxy.utils.JsonUtils
				.getStringFromInputStream(uploadDOResponse.getEntityInputStream());

		// Add DO from StagingDB to permanent repository

		WebResource addDOResource = client.resource(repo_uri + "DO/add");

		final MultiPart addMultiPart = new FormDataMultiPart().field("ID", stagingID);
		addMultiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
		ClientResponse addDOResponse = addDOResource.type("multipart/form-data").post(ClientResponse.class,
				addMultiPart);
		String repoID = dataIdentity.client.galaxy.utils.JsonUtils
				.getStringFromInputStream(addDOResponse.getEntityInputStream());

		// Get DO metadata from repo by RepoID
		WebResource getDOResource = client.resource(repo_uri + "repo/find/DO?ID=" + repoID);

		ClientResponse getDOResponse = getDOResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

		if (getDOResponse.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + getDOResponse.getStatus());
		}

		String getDOResponseString = getDOResponse.getEntity(String.class).trim();
		MessageResponse getDO_response_message = mapper.readValue(getDOResponseString, MessageResponse.class);
		String DO_doc = getDO_response_message.getMessage();
		ObjectNode DO_doc_json = (ObjectNode) mapper.readTree(DO_doc);
		String upload_date = DO_doc_json.getPath("uploadDate").getPath("$date").getTextValue();
		String checksum = MD5Utils.getMD5(new File(output_files.get(0)));

		// Register DO with PID and metadata attribute set
		// Fulfill PID metadata attribute set based on PID metadata type
		// definition

		ObjectNode pid_metadata = mapper.createObjectNode();
		pid_metadata.put("URL", gui_uri + "?ID=" + repoID);
		for (Map.Entry<String, PropertyDefinition> property : properties.entrySet()) {
			if (property.getValue().getName().equalsIgnoreCase("Landing page address")) {
				pid_metadata.put(property.getKey(), gui_uri + "?ID=" + repoID);
			}
			if (property.getValue().getName().equalsIgnoreCase("Metadata address")) {
				pid_metadata.put(property.getKey(), repo_uri + "repo/find/metadata?ID=" + repoID);
			}
			if (property.getValue().getName().equalsIgnoreCase("Checksum")) {
				pid_metadata.put(property.getKey(), checksum);
			}
			if (property.getValue().getName().equalsIgnoreCase("Creation date")) {
				pid_metadata.put(property.getKey(), upload_date);
			}
			if (property.getValue().getName().equalsIgnoreCase("Predecessor identifier")) {
				pid_metadata.put(property.getKey(), "");
			}
			if (property.getValue().getName().equalsIgnoreCase("Successor identifier")) {
				pid_metadata.put(property.getKey(), "");
			}
		}

		// Register DO with minimum metadata to PIT
		// Return: String PID
		String pid = PITUtils.registerPID(pit_uri.trim() + "pid/", pid_metadata);

		System.out.println("PID:" + pid);

	}
}
