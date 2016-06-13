package pragma.rocks.dataIdentity.container;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PIDCollection")
public class PIDRecord {
	@Id
	private String id;

	private String PID;
	private String repoID;

	public PIDRecord() {

	}

	public PIDRecord(String PID, String repoID) {
		super();
		this.PID = PID;
		this.repoID = repoID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getRepoID() {
		return repoID;
	}

	public void setRepoID(String repoID) {
		this.repoID = repoID;
	}
}
