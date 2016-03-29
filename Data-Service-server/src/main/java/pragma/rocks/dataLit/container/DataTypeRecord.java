package pragma.rocks.dataLit.container;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataTypeRecord {
	private String _id;
	private String _rev;
	private String datatypeName;
	private String datatypePID;
	private String datatypeClass;

	public DataTypeRecord() {

	}

	public DataTypeRecord(String datatypeName, String datatypePID, String datatypeClass) {
		this.datatypeName = datatypeName;
		this.datatypePID = datatypePID;
		this.datatypeClass = datatypeClass;
	}
	
	@JsonProperty("_id")
	public String getId() {
		return _id;
	}

	@JsonProperty("_id")
	public void setId(String s) {
		_id = s;
	}

	@JsonProperty("_rev")
	public String getRevision() {
		return _rev;
	}

	@JsonProperty("_rev")
	public void setRevision(String s) {
		_rev = s;
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

	public String getDatatypeClass() {
		return datatypeClass;
	}

	public void setDatatypeClass(String datatypeClass) {
		this.datatypeClass = datatypeClass;
	}
}
