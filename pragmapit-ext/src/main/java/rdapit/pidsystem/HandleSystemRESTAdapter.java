package rdapit.pidsystem;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.internal.util.Base64;

import rdapit.common.InvalidConfigException;
import rdapit.pitservice.PIDInformation;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Util;

/**
 * Concrete adapter of an identifier system that connects to the Handle System
 * through its native REST interface available from HS v8 on.
 * 
 */
public class HandleSystemRESTAdapter implements IIdentifierSystem {

	public static final boolean UNSAFE_SSL = true;

	private final static class TrustAllX509TrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

	}

	protected URI baseURI;
	protected String authInfo;
	protected Client client;
	protected String generatorPrefix;
	protected String admKeyPath;
	protected String admKeyPassword;
	protected String admHandle;

	private ObjectMapper objectMapper = new ObjectMapper();

	protected WebTarget rootTarget;
	protected WebTarget handlesTarget;
	protected WebTarget individualHandleTarget;

	public HandleSystemRESTAdapter(String baseURI, String userName, String userPassword, String generatorPrefix,
			boolean unsafe_ssl, String admKeyPath, String admKeyPassword, String admHandle) {
		super();
		this.generatorPrefix = generatorPrefix;
		this.admHandle = admHandle;
		this.admKeyPassword = admKeyPassword;
		this.admKeyPath = admKeyPath;
		this.baseURI = UriBuilder.fromUri(baseURI).path("api").build();
		try {
			this.authInfo = Base64.encodeAsString(
					URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(userPassword, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Error while encoding the user name in UTF-8", e);
		}

		if (UNSAFE_SSL) {
			/* TODO: REMOVE THIS IN PRODUCTION VERSION! */
			try {
				SSLContext sslContext;
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] { new TrustAllX509TrustManager() },
						new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String string, SSLSession ssls) {
						return true;
					}
				});
				this.client = ClientBuilder.newBuilder().sslContext(sslContext).build();
			} catch (NoSuchAlgorithmException | KeyManagementException e) {
				throw new IllegalStateException("Could not initialize unsafe SSL constructs", e);
			}
		} else {
			this.client = ClientBuilder.newBuilder().build();
		}
		this.rootTarget = client.target(baseURI).path("api");
		this.handlesTarget = rootTarget.path("handles");
		this.individualHandleTarget = handlesTarget.path("{handle}");
	}

	public HandleSystemRESTAdapter(String baseURI, String userName, String userPassword, String generatorPrefix,
			String admKeyPath, String admKeyPassword, String admHandle) {
		this(baseURI, userName, userPassword, generatorPrefix, false, admKeyPath, admKeyPassword, admHandle);
	}

	/**
	 * Factory method. Generates a new instance from a properties instance.
	 * 
	 * @param properties
	 * @return a new HandleSystemRESTAdapter instance
	 * @throws InvalidConfigException
	 */
	public static HandleSystemRESTAdapter configFromProperties(Properties properties) throws InvalidConfigException {
		if (!properties.containsKey("pidsystem.handle.baseURI"))
			throw new InvalidConfigException("Property pidsystem.handle.baseURI missing - check configuration!");
		String baseURI = properties.getProperty("pidsystem.handle.baseURI").trim();
		if (!properties.containsKey("pidsystem.handle.userName"))
			throw new InvalidConfigException("Property pidsystem.handle.userName missing - check configuration!");
		String userName = properties.getProperty("pidsystem.handle.userName").trim();
		if (!properties.containsKey("pidsystem.handle.userPassword"))
			throw new InvalidConfigException("Property pidsystem.handle.userPassword missing - check configuration!");
		String userPassword = properties.getProperty("pidsystem.handle.userPassword").trim();
		if (!properties.containsKey("pidsystem.handle.generatorPrefix"))
			throw new InvalidConfigException(
					"Property pidsystem.handle.generatorPrefix missing - check configuration!");
		String generatorPrefix = properties.getProperty("pidsystem.handle.generatorPrefix").trim();

		if (!properties.containsKey("pidsystem.handle.admKeyPath")) {
			throw new InvalidConfigException("Property pidsystem.handle.admKeyPath missing - check configuration!");
		}
		String admKeyPath = properties.getProperty("pidsystem.handle.admKeyPath").trim();

		if (!properties.containsKey("pidsystem.handle.admKeyPassword")) {
			throw new InvalidConfigException("Property pidsystem.handle.admKeyPassword missing - check configuration!");
		}
		String admKeyPassword = properties.getProperty("pidsystem.handle.admKeyPassword").trim();

		if (!properties.containsKey("pidsystem.handle.admHandle")) {
			throw new InvalidConfigException("Property pidsystem.handle.admHandle missing - check configuration!");
		}
		String admHandle = properties.getProperty("pidsystem.handle.admHandle").trim();

		boolean unsafeSSL = false;
		if (properties.containsKey("pidsystem.handle.unsafeSSL"))
			unsafeSSL = Boolean.parseBoolean(properties.getProperty("pidsystem.handle.unsafeSSL").trim());
		return new HandleSystemRESTAdapter(baseURI, userName, userPassword, generatorPrefix, unsafeSSL, admKeyPath,
				admKeyPassword, admHandle);
	}

	@Override
	public boolean isIdentifierRegistered(String pid) {
		Response response = individualHandleTarget.resolveTemplate("handle", pid).request(MediaType.APPLICATION_JSON)
				.head();
		return response.getStatus() == 200;
	}

	@Override
	public String queryProperty(String pid, PropertyDefinition propertyDefinition) throws IOException {
		String pidResponse = individualHandleTarget.resolveTemplate("handle", pid)
				.queryParam("type", propertyDefinition.getIdentifier()).request(MediaType.APPLICATION_JSON)
				.get(String.class);
		// extract the Handle value data entry from the json response
		JsonNode rootNode = objectMapper.readTree(pidResponse);
		JsonNode values = rootNode.get("values");
		if (!values.isArray())
			throw new IllegalStateException("Invalid response format: values must be an array");
		if (values.size() == 0)
			return null;
		if (values.size() > 1) {
			// More than one property stored at this record
			throw new IllegalStateException(
					"PID records with more than one property of same type are not supported yet");
		}
		String value = values.get(0).get("data").get("value").asText();
		return value;

	}

	protected String generatePIDName() {
		String uuid = UUID.randomUUID().toString();
		return this.generatorPrefix + "/" + uuid;
	}

	@Override
	public String registerPID(Map<String, String> handleValues) throws Exception {
		HandleResolver resolver = new HandleResolver();

		File privKeyFile = new File(this.admKeyPath);
		PrivateKey hdl_adm_priv = net.handle.hdllib.Util.getPrivateKeyFromFileWithPassphrase(privKeyFile,
				this.admKeyPassword);
		byte adm_handle[] = Util.encodeString(this.admHandle);
		AuthenticationInfo auth = new net.handle.hdllib.PublicKeyAuthenticationInfo(adm_handle, 300, hdl_adm_priv);

		// Create one sample Handle identifier
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		String handle_identifier = this.generatorPrefix + "/" + randomUUIDString;
		HandleValue[] handle_values = new HandleValue[handleValues.size()];

		int count = 0;
		Iterator it = handleValues.entrySet().iterator();
		while (it.hasNext()) {
			count++;
			Map.Entry pair = (Map.Entry) it.next();
			HandleValue handle_value = new HandleValue(count, Util.encodeString(pair.getKey().toString()),
					Util.encodeString(pair.getValue().toString()));
			handle_values[count - 1] = handle_value;
			it.remove(); // avoids a ConcurrentModificationException
		}

		CreateHandleRequest assign_request = new CreateHandleRequest(Util.encodeString(handle_identifier),
				handle_values, auth);

		// Return PID create/assign response - one Handle identifier
		AbstractResponse response_assign = resolver.processRequestGlobally(assign_request);
		if (response_assign.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the handle
			// identifier
			return handle_identifier;

		} else if (response_assign.responseCode == AbstractMessage.RC_ERROR) {
			byte values[] = ((ErrorResponse) response_assign).message;
			for (int i = 0; i < values.length; i++) {
				return String.valueOf(values[i]);
			}
		}
		
		return handle_identifier;
	}

	@Override
	public PIDInformation queryByType(String pid, TypeDefinition typeDefinition) throws IOException {
		PIDInformation allProps = queryAllProperties(pid);
		// only return properties listed in the type def
		PIDInformation pidInfo = new PIDInformation();
		Set<String> typeProps = typeDefinition.getAllProperties();
		for (String propID : allProps.getPropertyIdentifiers()) {
			if (typeProps.contains(propID)) {
				pidInfo.addProperty(propID, "", allProps.getPropertyValue(propID));
			}
		}
		return pidInfo;
	}

	@Override
	public boolean deletePID(String pid) {
		Response response = individualHandleTarget.resolveTemplate("handle", pid).request()
				.header("Authorization", "Basic " + authInfo).delete();
		return response.getStatus() == 200;
	}

	@Override
	public PIDInformation queryAllProperties(String pid) throws IOException {
		Response resp = individualHandleTarget.resolveTemplate("handle", pid).request(MediaType.APPLICATION_JSON).get();
		if (resp.getStatus() != 200)
			return null;
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(resp.readEntity(String.class));
		PIDInformation result = new PIDInformation();
		for (JsonNode valueNode : root.get("values")) {
			if (!(valueNode.get("data").get("format").asText().equals("string")
					|| valueNode.get("data").get("format").asText().equals("base64")
					|| valueNode.get("data").get("format").asText().equals("hex")))
				continue;
			// index is ignored..
			result.addProperty(valueNode.get("type").asText(), "", valueNode.get("data").get("value").asText());
		}
		return result;
	}

	public String getGeneratorPrefix() {
		return generatorPrefix;
	}

}
