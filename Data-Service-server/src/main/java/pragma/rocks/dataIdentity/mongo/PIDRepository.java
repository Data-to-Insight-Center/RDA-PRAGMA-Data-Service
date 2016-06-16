package pragma.rocks.dataIdentity.mongo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pragma.rocks.dataIdentity.container.PIDRecord;

@Repository
public class PIDRepository {

	@Autowired
	private MongoTemplate repoPIDTemplate;

	public void addRecord(PIDRecord record) {
		repoPIDTemplate.insert(record);
	}

	public List<PIDRecord> listAll() {
		return repoPIDTemplate.findAll(PIDRecord.class);
	}

	@SuppressWarnings("unchecked")
	public List<String> listDataType() {
		return repoPIDTemplate.getCollection("PIDCollection").distinct("DataType");
	}

	public List<PIDRecord> listAllByDTR(String dataType) {
		List<PIDRecord> records = repoPIDTemplate.findAll(PIDRecord.class);
		List<PIDRecord> filtered_records = new ArrayList<PIDRecord>();
		for (PIDRecord record : records) {
			if (record.getDataType().equalsIgnoreCase(dataType))
				filtered_records.add(record);
		}
		return filtered_records;
	}

	public PIDRecord findRecordByPID(String pid) {
		return repoPIDTemplate.find(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class).get(0);
	}

	public PIDRecord findRecordByrepoID(String repoID) {
		return repoPIDTemplate.find(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class).get(0);
	}

	public boolean deleteRecordByPID(String pid) {
		if (!repoPIDTemplate.exists(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class))
			return false;
		else {
			repoPIDTemplate.remove(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class);
			return true;
		}
	}

	public boolean deleteRecordByrepoID(String repoID) {
		if (!repoPIDTemplate.exists(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class))
			return false;
		else {
			repoPIDTemplate.remove(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class);
			return true;
		}
	}

	public boolean existRecordByPID(String pid) {
		return (repoPIDTemplate.exists(Query.query(Criteria.where("PID").is(pid)), PIDRecord.class));
	}

	public boolean existRecordByrepoID(String repoID) {
		return (repoPIDTemplate.exists(Query.query(Criteria.where("repoID").is(repoID)), PIDRecord.class));
	}
}
