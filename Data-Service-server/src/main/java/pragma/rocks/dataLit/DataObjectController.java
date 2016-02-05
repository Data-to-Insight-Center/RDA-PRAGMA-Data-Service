package pragma.rocks.dataLit;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pragma.rocks.dataLit.container.InformationType;
import pragma.rocks.dataLit.container.PublishType;
import pragma.rocks.dataLit.response.MessageResponse;
import pragma.rocks.dataLit.utils.DataTypeUtils;
import pragma.rocks.dataLit.utils.PITUtils;

/**
 * Handles requests for the occurrence set upload and query
 * 
 */
@RestController
public class DataObjectController {

	@Value("${couchdb.uri}")
	private String couchdb_uri;

	@Value("${dtr.uri}")
	private String dtr_uri;

	@Value("${couchdb.database.object}")
	private String couchdb_db_object;

	@Value("${couchdb.database.publish}")
	private String couchdb_db_publish;

	@Value("${pit.uri}")
	private String pit_uri;

	@Value("${pit.record.title}")
	private String pit_title;

	@Value("${pit.record.creator}")
	private String pit_creator;

	@Value("${pit.record.landingpageAddr}")
	private String pit_landingpageAddr;

	@Value("${pit.record.publicationDate}")
	private String pit_publicationdate;

	@Value("${pit.record.creationDate}")
	private String pit_creationdate;

	@Value("${pit.record.checksum}")
	private String pit_checksum;

	@Value("${pit.record.dataIdentifier}")
	private String pit_dataID;

	@Value("${pit.record.parentID}")
	private String pit_parentID;

	@Value("${pit.record.childID}")
	private String pit_childID;

	@Value("${pit.record.predecessorID}")
	private String pit_predecessorID;

	@Value("${pit.record.successorID}")
	private String pit_successorID;

	@Value("${pit.record.license}")
	private String pit_license;

	@RequestMapping("/DO/upload")
	@ResponseBody
	public MessageResponse DOupload(@RequestParam(value = "DataType", required = true) String datatype) {
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode doc = new ObjectNode(nodeFactory);

		try {
			DataTypeUtils.getDoc(datatype, doc, dtr_uri);

			// Connect to couch DB and create document with document ID as
			// return
			HttpClient authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();

			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it
			// doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_object, true);

			db.create(doc);
			String id = doc.findPath("_id").toString();

			MessageResponse response = new MessageResponse(true, id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, "");
			return response;
		}
	}

	@RequestMapping("/DO/find")
	@ResponseBody
	public MessageResponse DOfind(@RequestParam(value = "ID", required = true) String ID,
			@RequestParam(value = "revID", required = true) String revID) {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_object, false);

			@SuppressWarnings("deprecation")
			JsonNode doc = db.get(JsonNode.class, ID, revID);

			// Convert Json Node to message response type
			MessageResponse response = new MessageResponse(true, doc.toString());
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}

	}

	@RequestMapping("/DO/delete")
	@ResponseBody
	public MessageResponse DOdelete(@RequestParam(value = "ID", required = true) String ID,
			@RequestParam(value = "revID", required = true) String revID) {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_object, false);
			db.delete(ID, revID);

			// Convert Json Node to message response type
			MessageResponse response = new MessageResponse(true, null);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}

	}

	@RequestMapping(value = "/DO/publish", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOpublish(@RequestBody InformationType informationtype) {

		// Connect to couch DB and create document with document ID as return
		// response

		System.out.println("hello" + informationtype.getID());
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector object_db = dbInstance.createConnector(couchdb_db_object, false);
			if (object_db.contains(informationtype.getID())) {

				// Construct minimum metadata associated with PID for DO
				// publication
				// Key is using PID instead of plain text name

				JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
				ObjectNode doc = new ObjectNode(nodeFactory);

				// Put core required types
				doc.put("URL", informationtype.getLandingpageAddr());
				doc.put(pit_title, informationtype.getTitle());
				doc.put(pit_creator, informationtype.getCreator());
				doc.put(pit_publicationdate, informationtype.getPublicationDate());
				doc.put(pit_creationdate, informationtype.getCreationDate());
				doc.put(pit_checksum, informationtype.getChecksum());
				doc.put(pit_dataID, informationtype.getDataIdentifier());
				doc.put(pit_landingpageAddr, informationtype.getLandingpageAddr());

				// Put optional types
				if (informationtype.getParentID() != "") {
					doc.put(pit_parentID, informationtype.getParentID());
				}
				if (informationtype.getChildID() != "") {
					doc.put(pit_childID, informationtype.getChildID());
				}
				if (informationtype.getPredecessorID() != "") {
					doc.put(pit_predecessorID, informationtype.getPredecessorID());
				}
				if (informationtype.getSuccessorID() != "") {
					doc.put(pit_successorID, informationtype.getSuccessorID());
				}
				if (informationtype.getLicense() != "") {
					doc.put(pit_license, informationtype.getLicense());
				}

				// Public DO with minimum metadata to PIT in order to generate a
				// PID
				String pid = PITUtils.registerPID(pit_uri, doc);

				// Put publish record to publish db
				PublishType publish_do = new PublishType(pid, informationtype.getID(), informationtype.getRevID());

				CouchDbConnector publishdb = dbInstance.createConnector(couchdb_db_publish, true);
				publishdb.create(publish_do);

				// Return message response with registered PID record
				MessageResponse response = new MessageResponse(true, pid);
				return response;
			} else {
				MessageResponse response = new MessageResponse(false, null);
				return response;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}

	}
}
