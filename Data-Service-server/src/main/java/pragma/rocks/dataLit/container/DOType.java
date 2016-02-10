package pragma.rocks.dataLit.container;

public class DOType {
	private String objectID;
	private String objectRevID;
	private String DOname;

	public DOType(String objectID, String objectRevID, String DOname) {
		this.objectID = objectID;
		this.objectRevID = objectRevID;
		this.DOname = DOname;
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

	public String getDOname() {
		return DOname;
	}

	public void setDOname(String dOname) {
		DOname = dOname;
	}

}
