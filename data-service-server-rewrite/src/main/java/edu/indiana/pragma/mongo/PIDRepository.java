package edu.indiana.pragma.mongo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.util.JSON;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.sun.jersey.api.client.ClientResponse;
import org.bson.Document;
import org.bson.conversions.Bson;
import edu.indiana.pragma.util.MongoDB;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import edu.indiana.pragma.container.PIDRecord;
import edu.indiana.pragma.util.Constants;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;


public class PIDRepository {

	private MongoCollection<Document> pidCollection = null;
	private CacheControl control = new CacheControl();
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	WebResource pidResource;

	public PIDRepository() {
		MongoDatabase db = MongoDB.getServicesDB();
		pidCollection = db.getCollection(MongoDB.researchObjects);
		control.setNoCache(true);
		pidResource = Client.create().resource(Constants.pitURL);
	}
	public void addRecord(PIDRecord record) {
		BasicDBObject document = new BasicDBObject();
		document.put(record);
		pidCollection.insert(document);
	}

	public List<PIDRecord> listAll() {

		List<PIDRecord> pidRecords = new ArrayList<PIDRecord>();
		MongoCursor record = pidCollection.find().iterator();
		while (record.hasNext()) {
			pidRecords.add((PIDRecord) record.next());
		}
		return pidRecords;
	}

	public List<String> listDataType() {
		List<String> dataTypes = new ArrayList<String>();
		MongoCursor<String> types = pidCollection.distinct("DataType", String.class).iterator();
		while(types.hasNext()) {
			dataTypes.add(types.next());
		}
		return dataTypes;
	}

	public List<PIDRecord> listAllByDTR(String dataType) {
		List<PIDRecord> pidRecords = new ArrayList<PIDRecord>();
		MongoCursor records = pidCollection.find().iterator();
		while (records.hasNext()) {
			pidRecords.add((PIDRecord) records.next());
		}
		List<PIDRecord> filtered_records = new ArrayList<PIDRecord>();
		for (PIDRecord record : pidRecords) {
			if (record.getDataType().equalsIgnoreCase(dataType))
				filtered_records.add(record);
		}
		return filtered_records;
	}

	public PIDRecord findRecordByPID(String pid) {



		FindIterable<Document> iter = pidCollection.find(new Document("PID",pid));
		return iter.toString();
	}

	public PIDRecord findRecordByrepoID(String repoID) {
		return PIDTemplate.find(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class).get(0);
	}

	public boolean deleteRecordByPID(String pid) {


		Bson condition = new Document("$eq", pid);
		Bson filter = new Document("PID", condition);
		pidCollection.deleteOne(filter);

		if (!PIDTemplate.exists(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class))
			return false;
		else {
			PIDTemplate.remove(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class);
			return true;
		}
	}

	public boolean deleteRecordByrepoID(String repoID) {
		if (!PIDTemplate.exists(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class))
			return false;
		else {
			PIDTemplate.remove(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class);
			return true;
		}
	}

	public boolean existRecordByPID(String pid) {
		return (PIDTemplate.exists(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class));
	}

	public boolean existRecordByrepoID(String repoID) {
		return (PIDTemplate.exists(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class));

		DBObject query = new BasicDBObject("repoID", new BasicDBObject(
				"$exists", true).append("$eq", repoID));

		DBCursor repo_id = pidCollection.find(query);
	}
}
