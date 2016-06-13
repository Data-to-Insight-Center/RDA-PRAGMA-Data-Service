package pragma.rocks.dataIdentity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
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

	// @Test
	// public void registerDOList() throws Exception {
	//
	// mockMvc.perform(get("/register/list")).andExpect(status().isOk())
	// .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
	// .andExpect(jsonPath("$.success").value(true));
	//
	// }

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

		MvcResult upload_result = mockMvc
				.perform(fileUpload("/DO/upload").file(data).param("DataType", "20.5000.239/9e873b2a5690da5b0455")
						.param("DOname", "LM Occurrence Bironella gracilis").param("downloadingURL", "")
						.content(node.toString()))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();

		logger.info("Upload DO Name:" + "LM Occurrence Bironella gracilis");
		logger.info("Upload DO data type:" + "20.5000.239/9e873b2a5690da5b0455");
		logger.info("Upload DO downloading URL:");
		logger.info("Upload metadata object:" + node.toString());

		String upload_response = upload_result.getResponse().getContentAsString();

		logger.info("Upload service response:" + upload_response);

		// Get id in stagingDB from response
		MessageResponse upload_json = new ObjectMapper().readValue(upload_response, MessageResponse.class);
		String id = upload_json.getMessage();
		System.out.println("Object StagingDB ID is:" + id);

		// Step 2: Find DO in StagingDB and perform add or edit operation
		logger.info("Step 2 - Check uploaded DO and metadata object...");
		// Find DO metadata object
		MvcResult find_metadata_result = mockMvc.perform(get("/DO/find/metadata").param("ID", id))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();
		String find_metadata_response = find_metadata_result.getResponse().getContentAsString();
		MessageResponse find_metadata_json = new ObjectMapper().readValue(find_metadata_response,
				MessageResponse.class);
		System.out.println(find_metadata_json.getMessage());
		logger.info("DO and metadata object response:" + find_metadata_json.getMessage());

		// Find DO data
		MvcResult find_data_result = mockMvc.perform(get("/DO/find/data").param("ID", id)).andExpect(status().isOk())
				.andReturn();
		byte[] find_data_out = find_data_result.getResponse().getContentAsByteArray();
		FileUtils.writeByteArrayToFile(new File("test.zip"), find_data_out);

		// Add: add DO to permanent Repo and register with a PID
		// Edit: Further edit the DO with updated information
		// For demo we run ADD operation
		logger.info("Step 3 - Register DO with handle record and PID metadata profile...");

		MvcResult register_result = mockMvc
				.perform(post("/DO/add").param("ID", id).param("predecessorPID", "").param("successorPID", ""))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();

		String register_response = register_result.getResponse().getContentAsString();
		MessageResponse register_json = new ObjectMapper().readValue(register_response, MessageResponse.class);
		// System.out.println(register_json.getMessage());

		logger.info("Registered PID Handle record:" + register_json.getMessage());
	}
}
