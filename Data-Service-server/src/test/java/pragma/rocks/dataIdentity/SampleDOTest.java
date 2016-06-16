package pragma.rocks.dataIdentity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pragma.rocks.dataIdentity.response.MessageResponse;
import pragma.rocks.dataIdentity.utils.ZipUtils;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class SampleDOTest extends ApplicationTests {

	private final static Logger logger = Logger.getLogger(SampleDOTest.class);

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() throws FileNotFoundException, IOException {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void DOSampleProcess() throws Exception {
		// Set up basic logging mechanism
		ClassLoader classLoader = getClass().getClassLoader();
		File log4j_file = new File(classLoader.getResource("log4j.properties").getFile());
		Properties logProperties = new Properties();
		logProperties.load(new FileInputStream(log4j_file));
		PropertyConfigurator.configure(logProperties);

		logger.info("Run Sample DO upload and register process...");
		logger.info("Step 1 - Upload DO with metadata object to backend repository...");

		// Step 1: Upload DO with metadata object to backend database
		// Construct Metadata
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode node = nodeFactory.objectNode();

		node.put("OccurrenceSetID", "2578");
		node.put("displayName", "57");
		node.put("lastModified", "2016-01-16T01:52:13");

		// Construct data as multipart file
		File file = new File(classLoader.getResource("occur_2578.zip").getFile());
		MockMultipartFile data = new MockMultipartFile("data", "occur_2578.zip", "application/zip",
				new FileInputStream(file));

		logger.info("Upload DO Name:" + "LM Occurrence Bironella gracilis");
		logger.info("Upload DO data type:" + "20.5000.239/9e873b2a5690da5b0455");
		logger.info("Upload DO downloading URL:");
		logger.info("Upload metadata object:" + node.toString());

		String id = DOUpload("20.5000.239/9e873b2a5690da5b0455", "LM Occurrence Bironella gracilis", "",
				node.toString(), data);

		logger.info("Object StagingDB ID is:" + id);

		/*
		 * Step 2 can be ignored for middleware service actor // Step 2: Find DO
		 * in StagingDB and perform add or edit operation logger.info(
		 * "Step 2 - Check uploaded DO and metadata object..."); // Find DO
		 * metadata object MvcResult find_metadata_result =
		 * mockMvc.perform(get("/DO/find/metadata").param("ID", id))
		 * .andExpect(status().isOk()).andExpect(content().contentType(MediaType
		 * .APPLICATION_JSON_UTF8))
		 * .andExpect(jsonPath("$.success").value(true)).andReturn(); String
		 * find_metadata_response =
		 * find_metadata_result.getResponse().getContentAsString();
		 * MessageResponse find_metadata_json = new
		 * ObjectMapper().readValue(find_metadata_response,
		 * MessageResponse.class);
		 * System.out.println(find_metadata_json.getMessage()); logger.info(
		 * "DO and metadata object response:" +
		 * find_metadata_json.getMessage());
		 * 
		 * // Find DO data MvcResult find_data_result =
		 * mockMvc.perform(get("/DO/find/data").param("ID",
		 * id)).andExpect(status().isOk()) .andReturn(); byte[] find_data_out =
		 * find_data_result.getResponse().getContentAsByteArray();
		 * FileUtils.writeByteArrayToFile(new File("test.zip"), find_data_out);
		 */

		// Add: add DO to permanent Repo and register with a PID
		// Edit: Further edit the DO with updated information
		// For demo we run ADD operation
		logger.info("Step 3 - Register DO with handle record and PID metadata profile...");
		String PID = DOAdd(id, "", "");

		logger.info("Registered PID Handle record:" + PID);
	}

	@Test
	public void AgSampleProcess() throws Exception {
		// Set up basic logging mechanism
		ClassLoader classLoader = getClass().getClassLoader();
		File log4j_file = new File(classLoader.getResource("log4j.properties").getFile());
		Properties logProperties = new Properties();
		logProperties.load(new FileInputStream(log4j_file));
		PropertyConfigurator.configure(logProperties);

		logger.info("Run Sample DO upload and register process...");
		logger.info("Step 1 - Upload DO with metadata object to backend repository...");

		// Step 1: Upload DO with metadata object to backend database
		// Construct Metadata
		String input_path = "simulator.properties.txt";
		String output_path = "ward_10_numSeedHH_100_AgYear_2006_allocation_0__soilType_WI_VRZM080_searchScope_5_sharingPercent_0.1_stats.txt";
		File input_file = new File(classLoader.getResource(input_path).getFile());
		File output_file = new File(classLoader.getResource(output_path).getFile());

		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode node = nodeFactory.objectNode();

		Map<String, String> input_fields = AgFiles2Map(input_file, "=");
		Map<String, String> output_fields = AgFiles2Map(output_file, ":");

		// get input metadata
		for (Map.Entry<String, String> entry : input_fields.entrySet()) {
			if (entry.getKey().startsWith("model.parameter"))
				node.put(entry.getKey().replace(".", "_"), entry.getValue());
		}

		// get output metadata
		String ward_num = output_path.split("_")[1].trim();
		node.put("ward", ward_num);
		for (Map.Entry<String, String> entry : output_fields.entrySet()) {
			node.put(entry.getKey(), entry.getValue());
		}

		// Construct data as multipart file
		List<File> files = new ArrayList<File>();
		files.add(input_file);
		files.add(output_file);

		ZipUtils.zipFiles(output_path.replace(".txt", ".zip"), files);
		File file = new File(output_path.replace(".txt", ".zip"));
		MockMultipartFile data = new MockMultipartFile("data", output_path.replace(".txt", ".zip"), "application/zip",
				new FileInputStream(file));

		String DOname = output_path.replace(".txt", "");
		if (DOname.contains("_stats")) {
			DOname = DOname.replace("_stats", "");
		}

		String id = DOUpload("20.5000.239/cd83686e94b6328b28da", DOname, "", node.toString(), data);

		logger.info("Object StagingDB ID is:" + id);

		// Add: add DO to permanent Repo and register with a PID
		// Edit: Further edit the DO with updated information
		// For demo we run ADD operation
		logger.info("Step 3 - Register DO with handle record and PID metadata profile...");
		String PID = DOAdd(id, "", "");

		logger.info("Registered PID Handle record:" + PID);
	}

	public Map<String, String> AgFiles2Map(File file, String split) throws Exception {
		FileInputStream fis = new FileInputStream(file);

		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		Map<String, String> output = new HashMap<String, String>();

		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("##")) {
				String[] tokens = line.split(split);
				output.put(tokens[0].trim(), tokens[1].trim());
			}
		}

		br.close();

		return output;
	}

	public String DOUpload(String DataType, String DOname, String downloadingURL, String metadata,
			MockMultipartFile data) throws Exception {
		MvcResult upload_result = mockMvc
				.perform(fileUpload("/DO/upload").file(data).param("DataType", DataType).param("DOname", DOname)
						.param("downloadingURL", downloadingURL).content(metadata))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();

		String upload_response = upload_result.getResponse().getContentAsString();
		MessageResponse upload_json = new ObjectMapper().readValue(upload_response, MessageResponse.class);
		String id = upload_json.getMessage();
		return id;
	}

	public String DOAdd(String id, String predecessorPID, String successorPID) throws Exception {
		MvcResult register_result = mockMvc
				.perform(post("/DO/add").param("ID", id).param("predecessorPID", predecessorPID).param("successorPID",
						successorPID))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();

		String register_response = register_result.getResponse().getContentAsString();
		MessageResponse register_json = new ObjectMapper().readValue(register_response, MessageResponse.class);
		return register_json.getMessage();
	}
}
