package rdapit.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.glassfish.jersey.server.ResourceConfig;

import rdapit.common.InvalidConfigException;
import rdapit.pidsystem.HandleSystemRESTAdapter;
import rdapit.pidsystem.IIdentifierSystem;
import rdapit.pitservice.TypingService;
import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.TypeRegistry;

public class PITApplication extends ResourceConfig {

	public static final String PROPERTIES_FILE_PATH = "/usr/local/rda/pitapi.properties"; 
	
	public PITApplication() throws IOException, InvalidConfigException {
		super();
		packages("rdapit.rest");

		if (ApplicationContext.instance == null) {
			Properties properties = new Properties();
			File propFile = new File(PROPERTIES_FILE_PATH);
			if (!propFile.exists())
				throw new IOException("Property file pitapi.properties must be available at path " + PROPERTIES_FILE_PATH);
			InputStream propIS = new FileInputStream(propFile);
			properties.load(propIS);
			IIdentifierSystem identifierSystem = HandleSystemRESTAdapter.configFromProperties(properties);
			ITypeRegistry typeRegistry = TypeRegistry.configFromProperties(properties);
			TypingService typingService = new TypingService(identifierSystem, typeRegistry);
			ApplicationContext appContext = new ApplicationContext(typingService);
		}
	}

}
