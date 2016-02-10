package pragma.rocks.dataLit;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pragma.rocks.dataLit.container.InformationType;
import pragma.rocks.dataLit.container.PublishType;
import pragma.rocks.dataLit.response.InformationTypeResponse;
import pragma.rocks.dataLit.response.PublishListResponse;
import pragma.rocks.dataLit.utils.PITUtils;

/**
 * Handles requests for Published Data Object Query/Update
 * 
 */
@RestController
public class PublishObjectController {
	@Value("${couchdb.uri}")
	private String couchdb_uri;

	@Value("${couchdb.database.object}")
	private String couchdb_db_object;

	@Value("${couchdb.database.publish}")
	private String couchdb_db_publish;

	@Value("${handle.server.uri}")
	private String handle_uri;

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

	@RequestMapping("/publish/find")
	@ResponseBody
	public InformationTypeResponse Publishfind(@RequestParam(value = "objectID", required = true) String objectID,
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
							if (entry.getKey().equalsIgnoreCase(pit_title)) {
								informationtype.setTitle(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_creator)) {
								informationtype.setCreator(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_landingpageAddr)) {
								informationtype.setLandingpageAddr(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_publicationdate)) {
								informationtype.setPublicationDate(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_creationdate)) {
								informationtype.setCreationDate(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_checksum)) {
								informationtype.setChecksum(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_dataID)) {
								informationtype.setDataIdentifier(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_parentID)) {
								informationtype.setParentID(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_childID)) {
								informationtype.setChildID(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_predecessorID)) {
								informationtype.setPredecessorID(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_successorID)) {
								informationtype.setSuccessorID(entry.getValue());
							}

							if (entry.getKey().equalsIgnoreCase(pit_license)) {
								informationtype.setLicense(entry.getValue());
							}
						}
						InformationTypeResponse response = new InformationTypeResponse(true, pid, informationtype);
						return response;
					}
				}
			}
			// Convert Json Node to message response type
			InformationTypeResponse response = new InformationTypeResponse(false, null, null);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			InformationTypeResponse response = new InformationTypeResponse(false, null, null);
			return response;
		}

	}

	@RequestMapping("/publish/list")
	@ResponseBody
	public PublishListResponse Publishlist() {
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
			List<PublishType> publishlist = new ArrayList<PublishType>();
			for (JsonNode doc : bulkLoaded) {
				PublishType publishtype = new PublishType();
				publishtype.setId(doc.findPath("_id").toString().replace("\"", ""));
				publishtype.setRevision(doc.findPath("_rev").toString().replace("\"", ""));
				publishtype.setObjectID(doc.findPath("objectID").toString().replace("\"", ""));
				publishtype.setObjectRevID(doc.findPath("objectRevID").toString().replace("\"", ""));
				publishtype.setPID(doc.findPath("pid").toString().replace("\"", ""));
				publishtype.setTitle(doc.findPath("title").toString().replace("\"", ""));

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

}
