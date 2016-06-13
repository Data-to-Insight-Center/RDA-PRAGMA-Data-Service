package pragma.rocks.dataIdentity.mongo;

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
