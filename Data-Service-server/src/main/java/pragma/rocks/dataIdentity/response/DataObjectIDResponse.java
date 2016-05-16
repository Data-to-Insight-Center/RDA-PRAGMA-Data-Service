package pragma.rocks.dataIdentity.response;

public class DataObjectIDResponse {
	private boolean success;
	private String id;
	private String revID;

	public DataObjectIDResponse() {
	}

	public DataObjectIDResponse(boolean success, String id, String revID) {
		this.success = success;
		this.id = id;
		this.revID = revID;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRevID() {
		return revID;
	}

	public void setRevID(String revID) {
		this.revID = revID;
	}	
}
