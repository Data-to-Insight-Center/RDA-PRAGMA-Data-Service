package pragma.rocks.dataIdentity.mongo;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@Repository
public class StagingDBRepository {
	@Autowired
	private GridFsTemplate stagingDBTemplate;

	public String addDO(InputStream inputStream, String file_name, String content_type, DBObject metadata) {
		return stagingDBTemplate.store(inputStream, file_name, content_type, metadata).getId().toString();
	}

	public List<GridFSDBFile> listAll() {
		return stagingDBTemplate.find(null);
	}

	public GridFSDBFile findDOByID(String id) {
		return stagingDBTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
	}

	public boolean deleteDOByID(String id) {
		if (stagingDBTemplate.findOne(Query.query(Criteria.where("_id").is(id))).equals(null))
			return false;
		else {
			stagingDBTemplate.delete(Query.query(Criteria.where("_id").is(id)));
			return true;
		}
	}

	public boolean existDOByID(String id) {
		boolean result = stagingDBTemplate.findOne(Query.query(Criteria.where("_id").is(id))).equals(null);
		return !result;
	}
}
