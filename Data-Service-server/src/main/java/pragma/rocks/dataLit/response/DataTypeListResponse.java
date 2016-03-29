package pragma.rocks.dataLit.response;

import java.util.List;

import pragma.rocks.dataLit.container.DataTypeRecord;

public class DataTypeListResponse {
	private boolean success;
	private List<DataTypeRecord> datatyperecords;

	public DataTypeListResponse() {

	}

	public DataTypeListResponse(boolean success, List<DataTypeRecord> datatyperecords) {
		this.success = success;
		this.datatyperecords = datatyperecords;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<DataTypeRecord> getDatatyperecords() {
		return datatyperecords;
	}

	public void setDatatyperecords(List<DataTypeRecord> datatyperecords) {
		this.datatyperecords = datatyperecords;
	}
}
