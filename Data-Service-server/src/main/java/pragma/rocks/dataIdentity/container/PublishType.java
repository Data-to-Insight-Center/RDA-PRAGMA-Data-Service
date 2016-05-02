package pragma.rocks.dataIdentity.container;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublishType {
	private String _id;
	private String _rev;
	private String PID;
	private String objectID;
	private String objectRevID;

	public PublishType() {

	}

	public PublishType(String PID, String objectID, String objectRevID) {
		this.PID = PID;
		this.objectID = objectID;
		this.objectRevID = objectRevID;
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

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}
}
