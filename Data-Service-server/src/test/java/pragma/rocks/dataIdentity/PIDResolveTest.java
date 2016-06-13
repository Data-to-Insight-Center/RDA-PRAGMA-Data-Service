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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;

public class PIDResolveTest extends ApplicationTests {
	private final static Logger logger = Logger.getLogger(SampleDOTest.class);

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() throws FileNotFoundException, IOException {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void massiveResolveTest() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File log4j_file = new File(classLoader.getResource("log4j.properties").getFile());
		Properties logProperties = new Properties();
		logProperties.load(new FileInputStream(log4j_file));
		PropertyConfigurator.configure(logProperties);

		logger.info("Massive PID resolve Test - 150 PIDs run");
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < 150; i++) {
			mockMvc.perform(get("/pidrepo/resolvePID").param("PID", "11723/ac423e9d-1833-4eb9-86d0-55584eb14222"))
					.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
					.andExpect(jsonPath("$.success").value(true));
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		logger.info("Massive resolve test -150 run time:" + totalTime + " ms");

	}

}
