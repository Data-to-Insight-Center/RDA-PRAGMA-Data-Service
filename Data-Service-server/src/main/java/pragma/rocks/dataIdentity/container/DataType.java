package pragma.rocks.dataIdentity.container;

import org.springframework.data.annotation.Id;

public class DataType {
	@Id
	private String id;

	private String datatypeName;
	private String datatypePID;

	public DataType() {

	}

	public DataType(String datatypeName, String datatypePID) {
		super();
		this.datatypeName = datatypeName;
		this.datatypePID = datatypePID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDatatypeName() {
		return datatypeName;
	}

	public void setDatatypeName(String datatypeName) {
		this.datatypeName = datatypeName;
	}

	public String getDatatypePID() {
		return datatypePID;
	}

	public void setDatatypePID(String datatypePID) {
		this.datatypePID = datatypePID;
	}

}
