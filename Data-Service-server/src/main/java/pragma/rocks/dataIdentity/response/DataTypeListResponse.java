package pragma.rocks.dataIdentity.response;

import java.util.List;

import pragma.rocks.dataIdentity.container.DataType;

public class DataTypeListResponse {
	private boolean success;
	private List<DataType> datatyperecords;

	public DataTypeListResponse() {

	}

	public DataTypeListResponse(boolean success, List<DataType> datatyperecords) {
		this.success = success;
		this.datatyperecords = datatyperecords;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<DataType> getDatatyperecords() {
		return datatyperecords;
	}

	public void setDatatyperecords(List<DataType> datatyperecords) {
		this.datatyperecords = datatyperecords;
	}
}
