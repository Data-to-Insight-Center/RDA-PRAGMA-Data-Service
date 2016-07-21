package dataIdentity.server.pragma.mongo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import dataIdentity.server.pragma.container.PIDRecord;

@Repository
public class PIDRepository {

	@Autowired
	private MongoTemplate PIDTemplate;

	public void addRecord(PIDRecord record) {
		PIDTemplate.insert(record);
	}

	public List<PIDRecord> listAll() {
		return PIDTemplate.findAll(PIDRecord.class);
	}

	@SuppressWarnings("unchecked")
	public List<String> listDataType() {
		return PIDTemplate.getCollection("PIDCollection").distinct("DataType");
	}

	public List<PIDRecord> listAllByDTR(String dataType) {
		List<PIDRecord> records = PIDTemplate.findAll(PIDRecord.class);
		List<PIDRecord> filtered_records = new ArrayList<PIDRecord>();
		for (PIDRecord record : records) {
			if (record.getDataType().equalsIgnoreCase(dataType))
				filtered_records.add(record);
		}
		return filtered_records;
	}

	public PIDRecord findRecordByPID(String pid) {
		return PIDTemplate.find(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class).get(0);
	}

	public PIDRecord findRecordByrepoID(String repoID) {
		return PIDTemplate.find(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class).get(0);
	}

	public boolean deleteRecordByPID(String pid) {
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
	}
}
