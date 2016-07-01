package rdapit.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

import rdapit.common.InvalidConfigException;
import rdapit.pidsystem.HandleSystemRESTAdapter;
import rdapit.pitservice.EntityClass;
import rdapit.pitservice.PIDInformation;
import rdapit.pitservice.TypingService;
import rdapit.rest.ApplicationContext;
import rdapit.rest.PITApplication;
import rdapit.typeregistry.TypeRegistry;

public class RESTServiceTest extends JerseyTest {
	
	private TypeRegistry typeRegistry;
	private HandleSystemRESTAdapter identifierSystem;
	
	private static Properties configProperties = new Properties();

	@Test
	public void testResolve() {
		String prefix = this.identifierSystem.getGeneratorPrefix();
		/* Prepare targets */
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget pidResolveTarget = rootTarget.path("pid").path("{id}");
		WebTarget pidResolveTarget2 = rootTarget.path("pid").path("{prefix}").path("{suffix}");
		WebTarget propertyResolveTarget = rootTarget.path("property").path("{id}");
		WebTarget peekTarget = rootTarget.path("peek").path("{id}");
		/* Simple tests */
		Response resp = rootTarget.path("ping").request().get();
		assertEquals(200, resp.getStatus());
		// Some postings indicate that Tomcat may have a problem with encoded
		// slashes.
		// There's a solution however: in setenv include
		// CATALINA_OPTS="-Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"
		assertEquals(200, pidResolveTarget.resolveTemplate("id", prefix+"/pitapi_test1").request().head().getStatus());
		assertEquals(200, pidResolveTarget.resolveTemplate("id", prefix+"/pitapi_test1").request().get().getStatus());
		assertEquals(404, pidResolveTarget.resolveTemplate("id", prefix+"/invalid_or_unknown_identifier").request().head().getStatus());
		assertEquals(404, pidResolveTarget.resolveTemplate("id", prefix+"/invalid_or_unknown_identifier").request().get().getStatus());
		/* Query full record */
		resp = pidResolveTarget.resolveTemplate("id", prefix+"/pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		PIDInformation pidrec = resp.readEntity(PIDInformation.class);
		assertEquals("http://www.example.com", pidrec.getPropertyValue("URL"));
		// same call but on the other target that does not encode the slash between prefix and suffix
		resp = pidResolveTarget2.resolveTemplate("prefix", prefix).resolveTemplate("suffix", "pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		pidrec = resp.readEntity(PIDInformation.class);
		assertEquals("http://www.example.com", pidrec.getPropertyValue("URL"));
		/* Query single property */
		resp = pidResolveTarget.resolveTemplate("id", prefix+"/pitapi_test1").queryParam("filter_by_property", "11314.2/2f305c8320611911a9926bb58dfad8c9").request().get();
		assertEquals(200, resp.getStatus());
		/* Query property by type */
		resp = pidResolveTarget.resolveTemplate("id", prefix+"/pitapi_test1").queryParam("type", prefix+"/test_type").request().get();
		/* Query prop definition */
		assertEquals(200, propertyResolveTarget.resolveTemplate("id", "11314.2/56bb4d16b75ae50015b3ed634bbb519f").request().get().getStatus());
		/* Peek tests */
		resp = peekTarget.resolveTemplate("id", "11314.2/2f305c8320611911a9926bb58dfad8c9").request().get();
		assertEquals(200, resp.getStatus());
		assertEquals(EntityClass.PROPERTY, resp.readEntity(EntityClass.class));
		resp = peekTarget.resolveTemplate("id", prefix+"/pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		assertEquals(EntityClass.OBJECT, resp.readEntity(EntityClass.class));
	}

	@Test
	public void testCreateDeletePID() {
		/* Prepare targets */
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget pidResolveTarget = rootTarget.path("pid").path("{id}");
		/* Create PID */
		HashMap<String, String> properties = new HashMap<>();
		properties.put("URL", "http://www.eudat.eu");
		Response response = rootTarget.path("pid").request().post(Entity.json(properties));
		assertEquals(201, response.getStatus());
		String pid = response.readEntity(String.class);
		System.out.println("PID created: " + pid);
		try {
			/* Read properties */
			response = pidResolveTarget.resolveTemplate("id", pid).request().get();
			assertEquals(response.getStatus(), 200);
			PIDInformation pidinfo = response.readEntity(PIDInformation.class);
			assertEquals(pidinfo.getPropertyValue("URL"), properties.get("URL"));
		} finally {
			/* Delete PID */
			response = pidResolveTarget.resolveTemplate("id", pid).request().delete();
			assertEquals(200, response.getStatus());
		}
	}
	
	@BeforeClass
	public static void readTestingConfig() throws Exception {
		RESTServiceTest.configProperties = System.getProperties();
		if (RESTServiceTest.configProperties.containsKey("pitapi.testingconfig")) {
			String fn = RESTServiceTest.configProperties.getProperty("pitapi.testingconfig");
			File propfile = new File(fn);
			if (!propfile.exists()) {
				throw new Exception("Testing config file given in 'pitapi.testingconfig' not found! (file name: "+propfile.getCanonicalPath()+")");
			}
			RESTServiceTest.configProperties.load(new FileInputStream(propfile));
		}
	}
	
	@Override
	protected Application configure() {
		try {
			identifierSystem = HandleSystemRESTAdapter.configFromProperties(RESTServiceTest.configProperties);
			typeRegistry = TypeRegistry.configFromProperties(RESTServiceTest.configProperties); //("http://38.100.130.13:8002/registrar", "11314.2");
			new ApplicationContext(new TypingService(identifierSystem, typeRegistry));
			return new PITApplication();
		} catch (InvalidConfigException exc) {
			throw new IllegalStateException("Could not initialize application due to testing configuration errors: "+exc.getMessage());
		} catch (Exception exc) {
			throw new IllegalStateException("Could not initialize application: ", exc);
		}
	}

}
