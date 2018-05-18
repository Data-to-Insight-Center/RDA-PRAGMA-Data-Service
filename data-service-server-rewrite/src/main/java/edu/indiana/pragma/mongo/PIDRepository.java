/*
 *
 * Copyright 2018 The Trustees of Indiana University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @creator quzhou@umail.iu.edu
 * @rewritten by kunarath@iu.edu
 */
package edu.indiana.pragma.mongo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import edu.indiana.pragma.utils.MongoDB;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import edu.indiana.pragma.container.PIDRecord;
import edu.indiana.pragma.utils.Constants;

import javax.ws.rs.core.CacheControl;


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
		pidCollection.insertOne(record);
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

		BasicDBObject whereQuery = new BasicDBObject();
		List<PIDRecord> pids = new ArrayList<PIDRecord>();
		whereQuery.put("PID", pid);

		FindIterable<Document> iter_pid = pidCollection.find(whereQuery);
		iter_pid.projection(new Document("_id", 0));
		MongoCursor<Document> cursor = iter_pid.iterator();
		while (cursor.hasNext()) {
			pids.add((PIDRecord) cursor.next());
		}
		return pids.get(0);
	}

	public PIDRecord findRecordByrepoID(String repoID) {
		BasicDBObject whereQuery = new BasicDBObject();
		List<PIDRecord> repo_ids = new ArrayList<PIDRecord>();
		whereQuery.put("repoID", repoID);

		FindIterable<Document> iter_repo_ids = pidCollection.find(whereQuery);
		iter_repo_ids.projection(new Document("_id", 0));
		MongoCursor<Document> cursor = iter_repo_ids.iterator();
		while (cursor.hasNext()) {
			repo_ids.add((PIDRecord) cursor.next());
		}
		return repo_ids.get(0);
	}

}
