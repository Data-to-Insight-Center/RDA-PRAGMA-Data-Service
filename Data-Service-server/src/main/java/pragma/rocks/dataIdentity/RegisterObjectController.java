package pragma.rocks.dataIdentity;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.GenericResponse;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ModifyValueRequest;
import net.handle.hdllib.ResolutionRequest;
import net.handle.hdllib.ResolutionResponse;
import net.handle.hdllib.SecretKeyAuthenticationInfo;
import net.handle.hdllib.Util;
import pragma.rocks.dataIdentity.container.InformationType;
import pragma.rocks.dataIdentity.container.PublishType;
import pragma.rocks.dataIdentity.response.InformationTypeResponse;
import pragma.rocks.dataIdentity.response.MessageResponse;
import pragma.rocks.dataIdentity.response.PublishListResponse;
import pragma.rocks.dataIdentity.utils.PITUtils;

/**
 * Handles requests for Published Data Object Query/Update
 * 
 */
@RestController
public class RegisterObjectController {
	@Value("${couchdb.uri}")
	private String couchdb_uri;

	@Value("${couchdb.database.object}")
	private String couchdb_db_object;

	@Value("${couchdb.database.publish}")
	private String couchdb_db_publish;

	@Value("${handle.server.uri}")
	private String handle_uri;

	@Value("${pit.record.landingpageAddr}")
	private String pit_landingpageAddr;

	@Value("${pit.record.creationDate}")
	private String pit_creationdate;

	@Value("${pit.record.metadataURL}")
	private String pit_metadataURL;

	@Value("${pit.record.checksum}")
	private String pit_checksum;

	@Value("${pit.record.predecessorID}")
	private String pit_predecessorID;

	@Value("${pit.record.successorID}")
	private String pit_successorID;

	/*@Value("${pit.record.license}")
	private String pit_license;*/

	@Value("${handle.server.admin.record}")
	private String admin_record;

	@Value("${handle.server.admin.id}")
	private String admin_id;

	@Value("${handle.server.admin.pkey}")
	private String admin_pkey;

	@RequestMapping("/register/find")
	@ResponseBody
	public InformationTypeResponse findRegister(@RequestParam(value = "objectID", required = true) String objectID,
			@RequestParam(value = "objectRevID", required = true) String objectRevID) {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_publish, false);

			ViewQuery q = new ViewQuery().allDocs().includeDocs(true);
			List<JsonNode> bulkLoaded = db.queryView(q, JsonNode.class);

			for (JsonNode doc : bulkLoaded) {
				if (doc.findPath("objectID").toString().replaceAll("\"", "").equalsIgnoreCase(objectID)) {
					if (doc.findPath("objectRevID").toString().replace("\"", "").equalsIgnoreCase(objectRevID)) {
						String pid = doc.findPath("pid").toString().replace("\"", "");

						InformationType informationtype = new InformationType();
						Map<String, String> metadata = PITUtils.resolvePID(handle_uri, pid);
						for (Map.Entry<String, String> entry : metadata.entrySet()) {
							if (entry.getKey().equalsIgnoreCase("URL")) {
								informationtype.setLandingpageAddr(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_metadataURL)) {
								informationtype.setMetadataURL(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_creationdate)) {
								informationtype.setCreationDate(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_checksum)) {
								informationtype.setChecksum(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_predecessorID)) {
								informationtype.setPredecessorID(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_successorID)) {
								informationtype.setSuccessorID(entry.getValue());
							}
						}
						InformationTypeResponse response = new InformationTypeResponse(true, pid, objectID, objectRevID,
								informationtype);
						return response;
					}
				}
			}
			// Convert Json Node to message response type
			InformationTypeResponse response = new InformationTypeResponse(false, null, null, null, null);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			InformationTypeResponse response = new InformationTypeResponse(false, null, null, null, null);
			return response;
		}

	}

	@RequestMapping("/register/list")
	@ResponseBody
	public PublishListResponse listRegister() {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_publish, false);
			ViewQuery q = new ViewQuery().allDocs().includeDocs(true);
			List<JsonNode> bulkLoaded = db.queryView(q, JsonNode.class);
			ArrayList<PublishType> publishlist = new ArrayList<PublishType>();
			for (JsonNode doc : bulkLoaded) {
				PublishType publishtype = new PublishType();
				publishtype.setId(doc.findPath("_id").toString().replace("\"", ""));
				publishtype.setRevision(doc.findPath("_rev").toString().replace("\"", ""));
				publishtype.setObjectID(doc.findPath("objectID").toString().replace("\"", ""));
				publishtype.setObjectRevID(doc.findPath("objectRevID").toString().replace("\"", ""));
				publishtype.setPID(doc.findPath("pid").toString().replace("\"", ""));

				publishlist.add(publishtype);
			}
			// Construct response
			PublishListResponse response = new PublishListResponse(true, publishlist);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			PublishListResponse response = new PublishListResponse(false, null);
			return response;
		}

	}

	@RequestMapping("/DO/landingpage/set")
	@ResponseBody
	public MessageResponse setLandingpage(@RequestParam(value = "pid", required = true) String pid,
			@RequestParam(value = "landingpageAddr", required = true) String url) throws HandleException {
		// Get the UTF8 encoding of the desired handle.
		byte someHandle[] = Util.encodeString(pid);
		// Create a resolution request.
		// (without specifying any types, indexes, or authentication info)

		ResolutionRequest request = new ResolutionRequest(someHandle, null, null, null);
		HandleResolver resolver = new HandleResolver();
		// Create a resolver that will send the request and return the response.
		AbstractResponse response = resolver.processRequest(request);
		// Check the response to see if the operation was successful.
		if (response.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the response
			// and get the handle values.
			HandleValue values[] = ((ResolutionResponse) response).getHandleValues();
			for (int i = 0; i < values.length; i++) {
				System.out.println(String.valueOf(values[i]));
			}
		}

		AuthenticationInfo auth = new SecretKeyAuthenticationInfo(Util.encodeString(admin_record),
				Integer.parseInt(admin_id), Util.encodeString(admin_pkey));
		HandleValue new_value = new HandleValue(1, Util.encodeString("URL"), Util.encodeString(url));
		ModifyValueRequest modify = new ModifyValueRequest(someHandle, new_value, auth);

		AbstractResponse response_modify = resolver.processRequestGlobally(modify);

		if (response_modify.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the response
			// and get the handle values.
			byte values[] = ((GenericResponse) response_modify).getEncodedMessage();
			String message = "";
			for (int i = 0; i < values.length; i++) {
				message += String.valueOf(values[i]);
			}
			MessageResponse result = new MessageResponse(true, message);
			return result;
		} else if (response_modify.responseCode == AbstractMessage.RC_ERROR) {
			byte values[] = ((ErrorResponse) response_modify).message;
			String message = "";
			for (int i = 0; i < values.length; i++) {
				message += String.valueOf(values[i]);
			}
			MessageResponse result = new MessageResponse(false, message);
			return result;
		} else {
			MessageResponse result = new MessageResponse(false, null);
			return result;
		}
	}

	@RequestMapping("/DO/metadata/set")
	@ResponseBody
	public MessageResponse setMetadata(@RequestParam(value = "pid", required = true) String pid,
			@RequestBody String metadata) {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;

		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_publish, false);

			ViewQuery q = new ViewQuery().allDocs().includeDocs(true);
			List<JsonNode> bulkLoaded = db.queryView(q, JsonNode.class);

			for (JsonNode doc : bulkLoaded) {
				if (doc.findPath("pid").toString().replaceAll("\"", "").equalsIgnoreCase(pid)) {
					CouchDbConnector object_db = dbInstance.createConnector(couchdb_db_object, false);

					String objectID = doc.findPath("objectID").toString().replaceAll("\"", "");
					JsonNode object_doc = object_db.get(JsonNode.class, objectID);

					JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
					ObjectNode new_doc = new ObjectNode(nodeFactory);
					ObjectMapper mapper = new ObjectMapper();

					new_doc = (ObjectNode) mapper.readTree(metadata);
					new_doc.put("DOname", object_doc.findPath("DOname").toString().replace("\"", ""));
					new_doc.put("DataType", object_doc.findPath("DataType").toString().replace("\"", ""));
					new_doc.put("_id", object_doc.findPath("_id").toString().replace("\"", ""));
					new_doc.put("_rev", object_doc.findPath("_rev").toString().replace("\"", ""));

					object_db.update(new_doc);

					MessageResponse response = new MessageResponse(true,
							new_doc.findPath("_id").toString().replace("\"", "") + ","
									+ new_doc.findPath("_rev").toString().replace("\"", ""));
					return response;
				}
			}

			MessageResponse response = new MessageResponse(false, "Data object not found.");
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, "An unexpected error occurs.");
			return response;
		}
	}
	
	@RequestMapping("/DO/metadataURL/set")
	@ResponseBody
	public MessageResponse setMetadataURL(@RequestParam(value = "pid", required = true) String pid,
			@RequestParam(value = "metadataURL", required = true) String url) throws HandleException {
		// Get the UTF8 encoding of the desired handle.
		byte someHandle[] = Util.encodeString(pid);
		// Create a resolution request.
		// (without specifying any types, indexes, or authentication info)

		ResolutionRequest request = new ResolutionRequest(someHandle, null, null, null);
		HandleResolver resolver = new HandleResolver();
		// Create a resolver that will send the request and return the response.
		AbstractResponse response = resolver.processRequest(request);
		// Check the response to see if the operation was successful.
		if (response.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the response
			// and get the handle values.
			HandleValue values[] = ((ResolutionResponse) response).getHandleValues();
			for (int i = 0; i < values.length; i++) {
				System.out.println(String.valueOf(values[i]));
			}
		}

		AuthenticationInfo auth = new SecretKeyAuthenticationInfo(Util.encodeString(admin_record),
				Integer.parseInt(admin_id), Util.encodeString(admin_pkey));
		HandleValue new_value = new HandleValue(1, Util.encodeString(pit_metadataURL), Util.encodeString(url));
		ModifyValueRequest modify = new ModifyValueRequest(someHandle, new_value, auth);

		AbstractResponse response_modify = resolver.processRequestGlobally(modify);

		if (response_modify.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the response
			// and get the handle values.
			byte values[] = ((GenericResponse) response_modify).getEncodedMessage();
			String message = "";
			for (int i = 0; i < values.length; i++) {
				message += String.valueOf(values[i]);
			}
			MessageResponse result = new MessageResponse(true, message);
			return result;
		} else if (response_modify.responseCode == AbstractMessage.RC_ERROR) {
			byte values[] = ((ErrorResponse) response_modify).message;
			String message = "";
			for (int i = 0; i < values.length; i++) {
				message += String.valueOf(values[i]);
			}
			MessageResponse result = new MessageResponse(false, message);
			return result;
		} else {
			MessageResponse result = new MessageResponse(false, null);
			return result;
		}
	}
}
