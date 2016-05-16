package pragma.rocks.dataIdentity;

import java.net.MalformedURLException;
import java.util.List;

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

import pragma.rocks.dataIdentity.container.DataTypeRecord;
import pragma.rocks.dataIdentity.response.DataObjectIDResponse;
import pragma.rocks.dataIdentity.response.DataTypeListResponse;

/**
 * Handles requests for Data Object upload/query/publish
 * 
 */
@RestController
public class DataTypeController {
	@Value("${couchdb.uri}")
	private String couchdb_uri;

	@Value("${dtr.uri}")
	private String dtr_uri;

	@Value("${couchdb.database.datatype}")
	private String couchdb_db_datatype;

	@RequestMapping("/datatype/list")
	@ResponseBody
	public DataTypeListResponse DataTypeList() {
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_datatype, false);

			ViewQuery q = new ViewQuery().allDocs().includeDocs(true);
			List<DataTypeRecord> bulkLoaded = db.queryView(q, DataTypeRecord.class);

			// Convert Json Node to message response type
			DataTypeListResponse response = new DataTypeListResponse(true, bulkLoaded);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			DataTypeListResponse response = new DataTypeListResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/datatype/upload")
	@ResponseBody
	public DataObjectIDResponse DataTypeUpload(@RequestParam("datatypeName") String datatypeName,
			@RequestParam("datatypePID") String datatypePID) {
		HttpClient authenticatedHttpClient;
		try {
			authenticatedHttpClient = new StdHttpClient.Builder().url(couchdb_uri).build();
			CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
			// if the second parameter is true, the database will be created if
			// it doesn't exists
			CouchDbConnector db = dbInstance.createConnector(couchdb_db_datatype, true);

			// Upload datatype record as DatatypeRecord class
			DataTypeRecord datatype = new DataTypeRecord(datatypeName, datatypePID);
			db.create(datatype);

			String id = datatype.getId();
			String revID = datatype.getRevision();

			// Convert Json Node to message response type
			DataObjectIDResponse response = new DataObjectIDResponse(true, id, revID);
			return response;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			DataObjectIDResponse response = new DataObjectIDResponse(false, null, null);
			return response;
		}
	}
}
