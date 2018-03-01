package dataIdentity.client.galaxy.utils;

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
import dataIdentity.client.galaxy.utils.PITUtils;
import dataIdentity.client.galaxy.utils.PropertyReader;
import dataIdentity.client.galaxy.utils.ZipUtils;

@SpringBootApplication
public class TestUtils {
	public static void main (String[] args) throws Exception {
		// Get args info
		String config_path = "";

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
		
			ObjectNode pid_metadata = mapper.createObjectNode();
			pid_metadata.put("URL", gui_uri + "?ID=test1");
			for (Map.Entry<String, PropertyDefinition> property : properties.entrySet()) {
				if (property.getValue().getName().equalsIgnoreCase("Landing page address")) {
					pid_metadata.put(property.getKey(), gui_uri + "?ID=test1");
				}
				if (property.getValue().getName().equalsIgnoreCase("Metadata address")) {
					pid_metadata.put(property.getKey(), repo_uri + "repo/find/metadata?ID=test1");
				}
				if (property.getValue().getName().equalsIgnoreCase("Checksum")) {
					pid_metadata.put(property.getKey(), "test111");
				}
				if (property.getValue().getName().equalsIgnoreCase("Creation date")) {
					pid_metadata.put(property.getKey(), "20180228");
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
