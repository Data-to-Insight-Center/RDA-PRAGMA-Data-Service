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

import pragma.rocks.dataIdentity.container.InformationType;
import pragma.rocks.dataIdentity.response.DataObjectIDResponse;
import pragma.rocks.dataIdentity.response.MessageListResponse;
import pragma.rocks.dataIdentity.response.MessageResponse;
import pragma.rocks.dataIdentity.utils.MD5Utils;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class SampleDOTest extends ApplicationTests {
	private String repo_uri = "http://127.0.0.1:5984";
	private String repo_DB = "dataobject";
	private String server_uri = "http://localhost:9002";

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

	@SuppressWarnings("deprecation")
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
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode node = nodeFactory.objectNode();
		ObjectNode child = nodeFactory.objectNode();

		child.put("OccurrenceSetID", "2578");
		child.put("displayName", "57");
		child.put("lastModified", "2016-01-16T01:52:13");
		node.put("metadata", child);

		MvcResult upload_result = mockMvc
				.perform(post("/DO/upload").param("DataType", "20.5000.239/9e873b2a5690da5b0455")
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

		// Get id and revID from response
		DataObjectIDResponse upload_json = new ObjectMapper().readValue(upload_response, DataObjectIDResponse.class);
		String id = upload_json.getId();
		String revID = upload_json.getRevID();

		System.out.println(id + "" + revID);

		// Step 2: Upload DO attachment if there is related DO file
		//ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("occur_2578.zip").getFile());
		MockMultipartFile attachment = new MockMultipartFile("attachment", "occur_2578.zip", "application/zip",
				new FileInputStream(file));

		MvcResult attachment_result = mockMvc
				.perform(fileUpload("/DO/upload/attachment").file(attachment).param("ID", id).param("revID", revID)
						.param("name", "occur_2578.zip").param("contentType", "application/zip"))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();

		logger.info("Step 2 - Upload attachment:" + "occur_2578.zip");

		String attachment_response = attachment_result.getResponse().getContentAsString();

		logger.info("Upload attachment response:" + attachment_response);
		// Get updated revID from response - attachment uploading causes a new
		// revision ID
		DataObjectIDResponse attachment_json = new ObjectMapper().readValue(attachment_response,
				DataObjectIDResponse.class);
		revID = attachment_json.getRevID();
		System.out.println(revID);
		// Step 3: find DO and related attachment

		logger.info("Step 3 - Check uploaded DO and metadata object...");
		// Find DO and metadata object
		MvcResult find_DO_result = mockMvc.perform(get("/DO/find").param("ID", id).param("revID", revID))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();
		String find_DO_response = find_DO_result.getResponse().getContentAsString();
		MessageResponse find_DO_json = new ObjectMapper().readValue(find_DO_response, MessageResponse.class);
		System.out.println(find_DO_json.getMessage());

		logger.info("DO and metadata object response:" + find_DO_response);
		// Find attachment downloading URL
		MvcResult find_attachment_result = mockMvc
				.perform(get("/DO/find/attachment").param("ID", id).param("revID", revID)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();

		String find_attachment_response = find_attachment_result.getResponse().getContentAsString();
		logger.info("DO downloading URL:" + find_attachment_response);

		MessageListResponse find_attachment_json = new ObjectMapper().readValue(find_attachment_response,
				MessageListResponse.class);
		for (String downloadingURL : find_attachment_json.getMessages())
			System.out.println(downloadingURL + "\n");

		// Step 4: Register DO with Handle record and PID metadata profile
		// PID metadata profile is represented as InformationType java
		// pojo(class)

		logger.info("Step 4 - Register DO with handle record and PID metadata profile...");

		InformationType informationType = new InformationType();
		informationType.setLandingpageAddr(server_uri + "/landingpage.html?ID=" + id + "&revID=" + revID);
		informationType.setCreationDate("2016-05-16T12:56:00-04:00");
		informationType.setChecksum(MD5Utils.getMD5(file));
		informationType.setMetadataURL(repo_uri + "/" + repo_DB + "/" + id + "?rev=" + revID);
		informationType.setPredecessorID("");
		informationType.setSuccessorID("");

		String informationType_string = new ObjectMapper().writeValueAsString(informationType);
		System.out.println(informationType_string);

		logger.info("PID metadata profile:" + informationType_string);

		MvcResult register_result = mockMvc
				.perform(post("/DO/register").param("ID", id).param("revID", revID)
						.contentType(MediaType.APPLICATION_JSON).content(informationType_string))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value(true)).andReturn();

		String register_response = register_result.getResponse().getContentAsString();
		MessageResponse register_json = new ObjectMapper().readValue(register_response, MessageResponse.class);
		System.out.println(register_json.getMessage());

		logger.info("Registered PID Handle record:" + register_json.getMessage());

		// Now you get the Handle of the DO. You can open your browser and type
		// in http://hdl.handle.net/<handle> to see what you get here.
	}

}
