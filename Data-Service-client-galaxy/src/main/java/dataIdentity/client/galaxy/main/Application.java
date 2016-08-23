package dataIdentity.client.galaxy.main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dataIdentity.client.galaxy.container.FileMessageResource;
import dataIdentity.client.galaxy.container.PropertyDefinition;
import dataIdentity.client.galaxy.container.TypeDefinition;
import dataIdentity.client.galaxy.response.MessageResponse;
import dataIdentity.client.galaxy.utils.JsonUtils;
import dataIdentity.client.galaxy.utils.MD5Utils;
import dataIdentity.client.galaxy.utils.PropertyReader;
import dataIdentity.client.galaxy.utils.ZipUtils;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	@SuppressWarnings("deprecation")
	public void run(String... args) throws Exception {
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

		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();

		// Get PIT and data type definition from PIT service
		final TypeDefinition typeDef = restTemplate.getForObject(pit_uri + "type/" + pid_type, TypeDefinition.class);
		Map<String, PropertyDefinition> properties = typeDef.getProperties();

		// Upload DO to PRAGMA Data Repository with Zip and metadata
		// Return: StagingDB id and repoID
		final MultiValueMap<String, Object> uploaddata = new LinkedMultiValueMap<String, Object>();
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

		Path path = Paths.get("./" + DOname + ".zip");

		// Add data to request param
		byte[] fileBytes = Files.readAllBytes(path);
		uploaddata.add("data", new FileMessageResource(fileBytes, DOname + ".zip"));

		// Add metadata to request param
		JsonNode metadata = JsonUtils.readUrl2JsonNode(workflow);
		ObjectNode jNode = mapper.createObjectNode();
		jNode.put("metadata", metadata);
		jNode.put("DOname", DOname);
		jNode.put("DataType", data_type);
		jNode.put("DownloadingURL", "");

		uploaddata.add("metadata", jNode.toString());

		final HttpEntity<MultiValueMap<String, Object>> uploadrequestEntity = new HttpEntity<MultiValueMap<String, Object>>(
				uploaddata);

		final ResponseEntity<MessageResponse> uploadresponse = restTemplate.exchange(repo_uri + "DO/upload",
				HttpMethod.POST, uploadrequestEntity, MessageResponse.class);
		String stagingID = uploadresponse.getBody().getMessage();

		// Add DO from StagingDB to permanent repository
		final MultiValueMap<String, Object> adddata = new LinkedMultiValueMap<String, Object>();
		adddata.add("ID", stagingID);
		final HttpEntity<MultiValueMap<String, Object>> addrequestEntity = new HttpEntity<MultiValueMap<String, Object>>(
				adddata);
		final ResponseEntity<MessageResponse> addresponse = restTemplate.exchange(repo_uri + "DO/add", HttpMethod.POST,
				addrequestEntity, MessageResponse.class);
		String repoID = addresponse.getBody().getMessage();

		// Get DO metadata from repo by RepoID
		final MessageResponse retrieveresponse = restTemplate.getForObject(repo_uri + "repo/find/DO?ID=" + repoID,
				MessageResponse.class);
		String DO_doc = retrieveresponse.getMessage();
		ObjectNode DO_doc_json = (ObjectNode) mapper.readTree(DO_doc);
		String upload_date = DO_doc_json.getPath("uploadDate").getPath("$date").getTextValue();
		String checksum = MD5Utils.getMD5(new File(output_files.get(0)));

		// Register DO with PID and metadata attribute set
		// Fulfill PID metadata attribute set based on PID metadata type
		// definition

		if (provider.equalsIgnoreCase("handle")) {
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

			final MultiValueMap<String, Object> piddata = new LinkedMultiValueMap<String, Object>();
			piddata.add("PIDmetadata", pid_metadata.toString());
			piddata.add("PIDmetadataType", pid_type);
			piddata.add("DataType", data_type);
			piddata.add("DOname", DOname);
			piddata.add("RepoID", repoID);

			final HttpEntity<MultiValueMap<String, Object>> pidrequestEntity = new HttpEntity<MultiValueMap<String, Object>>(
					piddata);
			final ResponseEntity<MessageResponse> pidresponse = restTemplate.exchange(
					identity_uri + "pid/register/handle", HttpMethod.POST, pidrequestEntity, MessageResponse.class);

			log.info("PID:" + pidresponse.getBody().getMessage());
		} else if (provider.equalsIgnoreCase("ark")) {
			ObjectNode pid_metadata = mapper.createObjectNode();
			pid_metadata.put("_target", gui_uri + "?ID=" + repoID);
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
					pid_metadata.put(property.getKey(), "NULL");
				}
				if (property.getValue().getName().equalsIgnoreCase("Successor identifier")) {
					pid_metadata.put(property.getKey(), "NULL");
				}
			}

			final MultiValueMap<String, Object> piddata = new LinkedMultiValueMap<String, Object>();
			piddata.add("PIDmetadata", pid_metadata.toString());
			piddata.add("PIDmetadataType", pid_type);
			piddata.add("DataType", data_type);
			piddata.add("DOname", DOname);
			piddata.add("RepoID", repoID);

			final HttpEntity<MultiValueMap<String, Object>> pidrequestEntity = new HttpEntity<MultiValueMap<String, Object>>(
					piddata);
			final ResponseEntity<MessageResponse> pidresponse = restTemplate.exchange(identity_uri + "pid/register/ark",
					HttpMethod.POST, pidrequestEntity, MessageResponse.class);

			log.info("PID:" + pidresponse.getBody().getMessage());
		}
	}
}
