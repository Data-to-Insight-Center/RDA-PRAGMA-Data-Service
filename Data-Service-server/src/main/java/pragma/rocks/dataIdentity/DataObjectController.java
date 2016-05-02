package pragma.rocks.dataIdentity;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.Options;
import org.ektorp.ViewQuery;
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

import pragma.rocks.dataIdentity.container.DOType;
import pragma.rocks.dataIdentity.container.InformationType;
import pragma.rocks.dataIdentity.container.PublishType;
import pragma.rocks.dataIdentity.response.DOListResponse;
import pragma.rocks.dataIdentity.response.MessageListResponse;
import pragma.rocks.dataIdentity.response.MessageResponse;
import pragma.rocks.dataIdentity.response.PublishBoolean;
import pragma.rocks.dataIdentity.response.PublishBooleanResponse;
import pragma.rocks.dataIdentity.utils.PITUtils;

/**
 * Handles requests for Data Object upload/query/publish
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

	@Value("${pit.record.license}")
	private String pit_license;

	@RequestMapping(value = "/DO/upload", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOupload(@RequestParam(value = "DataType", required = true) String datatype,
			@RequestParam(value = "DOname", required = true) String DOname, @RequestBody String metadata) {
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode doc = new ObjectNode(nodeFactory);
		ObjectMapper mapper = new ObjectMapper();

		try {
			doc = (ObjectNode) mapper.readTree(metadata);
			doc.put("DOname", DOname);
			doc.put("DataType", datatype);

			// Connect to couch DB and create document with document ID as
			// return
			HttpClient authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();

			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it
			// doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_object, true);

			db.create(doc);
			String id = doc.findPath("_id").toString().replace("\"", "");
			String revid = doc.findPath("_rev").toString().replace("\"", "");

			MessageResponse response = new MessageResponse(true, id + "," + revid);
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

	@RequestMapping("/DO/find/version")
	@ResponseBody
	public MessageListResponse DOfindversion(@RequestParam(value = "ID", required = true) String ID) {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_object, false);

			Options options = new Options().includeRevisions();
			JsonNode doc = db.get(JsonNode.class, ID, options);

			JsonNode revisions = doc.findPath("_revisions");
			JsonNode ids = revisions.findPath("ids");
			String rev_info = ids.toString().replace("\"", "");
			rev_info = rev_info.replace("[", "");
			rev_info = rev_info.replace("]", "");

			String[] revs = rev_info.split(",");

			List<String> revs_list = new ArrayList<String>();
			for (int i = 0; i < revs.length; i++) {
				int rev_num = i + 1;
				revs_list.add(rev_num + "-" + revs[0]);
			}

			// Convert Json Node to message response type
			MessageListResponse response = new MessageListResponse(true, revs_list);
			return response;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			MessageListResponse response = new MessageListResponse(false, null);
			return response;
		}

	}

	@RequestMapping("/DO/find/registerVersion")
	@ResponseBody
	public PublishBooleanResponse DOfindversionpublish(@RequestParam(value = "ID", required = true) String ID) {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_object, false);

			Options options = new Options().includeRevisions();
			JsonNode doc = db.get(JsonNode.class, ID, options);

			JsonNode revisions = doc.findPath("_revisions");
			JsonNode ids = revisions.findPath("ids");
			String rev_info = ids.toString().replace("\"", "");
			rev_info = rev_info.replace("[", "");
			rev_info = rev_info.replace("]", "");

			String[] revs = rev_info.split(",");

			CouchDbInstance publish_dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector publish_db = publish_dbInstance.createConnector(couchdb_db_publish, false);

			List<PublishBoolean> publishbooleanlist = new ArrayList<PublishBoolean>();
			for (int i = 0; i < revs.length; i++) {
				PublishBoolean publishboolean = new PublishBoolean();

				int rev_num = revs.length - i;
				String rev_id = rev_num + "-" + revs[i];

				ViewQuery q = new ViewQuery().allDocs().includeDocs(true);
				List<JsonNode> bulkLoaded = publish_db.queryView(q, JsonNode.class);

				for (JsonNode publish_doc : bulkLoaded) {
					if (publish_doc.findPath("objectID").toString().replaceAll("\"", "").equalsIgnoreCase(ID)) {
						if (publish_doc.findPath("objectRevID").toString().replace("\"", "").equalsIgnoreCase(rev_id)) {
							String pid = publish_doc.findPath("pid").toString().replace("\"", "");
							publishboolean.setSuccess(true);
							publishboolean.setPid(pid);
							publishboolean.setRev(rev_id);
							publishboolean.setId(ID);
						}
					}
				}

				if (!publishboolean.isSuccess()) {
					publishboolean.setRev(rev_id);
					publishboolean.setId(ID);
				}

				publishbooleanlist.add(publishboolean);
			}

			// Convert Json Node to message response type
			PublishBooleanResponse response = new PublishBooleanResponse(true, publishbooleanlist);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			PublishBooleanResponse response = new PublishBooleanResponse(false, null);
			return response;
		}

	}

	@RequestMapping("/DO/list")
	@ResponseBody
	public DOListResponse DOlist() {
		// Connect to couch DB and create document with document ID as
		// return
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_object, false);

			ViewQuery q = new ViewQuery().allDocs().includeDocs(true);
			List<JsonNode> bulkLoaded = db.queryView(q, JsonNode.class);

			List<DOType> DOList = new ArrayList<DOType>();

			for (JsonNode doc : bulkLoaded) {
				DOType dotype = new DOType(doc.findPath("_id").toString().replace("\"", ""),
						doc.findPath("_rev").toString().replace("\"", ""),
						doc.findPath("DOname").toString().replace("\"", ""));
				DOList.add(dotype);
			}

			// Convert Json Node to message response type
			DOListResponse response = new DOListResponse(true, DOList);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			DOListResponse response = new DOListResponse(false, null);
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

	@RequestMapping(value = "/DO/register", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOregister(@RequestBody InformationType informationtype) {

		// Connect to couch DB and create document with document ID as return
		// response
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector object_db = dbInstance.createConnector(couchdb_db_object, false);
			if (object_db.contains(informationtype.getdbID())) {

				// Construct minimum metadata associated with PID for DO
				// publication
				// Key is using PID instead of plain text name

				JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
				ObjectNode doc = new ObjectNode(nodeFactory);

				// Put core required types
				doc.put("URL", informationtype.getLandingpageAddr());
				doc.put(pit_creationdate, informationtype.getCreationDate());
				doc.put(pit_landingpageAddr, informationtype.getLandingpageAddr());
				doc.put(pit_metadataURL, informationtype.getMetadataURL());

				// Put optional types
				if (informationtype.getChecksum() != "") {
					doc.put(pit_checksum, informationtype.getChecksum());
				}
				if (informationtype.getPredecessorID() != "") {
					doc.put(pit_predecessorID, informationtype.getPredecessorID());
				}
				if (informationtype.getSuccessorID() != "") {
					doc.put(pit_successorID, informationtype.getSuccessorID());
				}

				// Public DO with minimum metadata to PIT in order to generate a
				// PID
				String pid = PITUtils.registerPID("http://pragma8.cs.indiana.edu:8008/rdapit-0.1/pitapi/pid", doc);
				// System.out.println(pid);

				// Put publish record to publish db
				PublishType publish_do = new PublishType(pid, informationtype.getdbID(), informationtype.getRevID());

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
