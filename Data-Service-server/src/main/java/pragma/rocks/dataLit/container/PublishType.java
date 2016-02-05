package pragma.rocks.dataLit.container;

public class PublishType {
	private String PID;
	private String objectID;
	private String objectRevID;

	public PublishType(String PID, String objectID, String objectRevID) {
		this.PID = PID;
		this.objectID = objectID;
		this.objectRevID = objectRevID;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getObjectRevID() {
		return objectRevID;
	}

	public void setObjectRevID(String objectRevID) {
		this.objectRevID = objectRevID;
	}

}
