package pragma.rocks.dataIdentity.response;

import java.util.List;

import pragma.rocks.dataIdentity.container.PIDRecord;

public class PIDRecordListResponse {
	private boolean success;
	private List<PIDRecord> records;

	public PIDRecordListResponse() {

	}

	public PIDRecordListResponse(boolean success, List<PIDRecord> records) {
		this.success = success;
		this.records = records;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<PIDRecord> getRecords() {
		return records;
	}

	public void setRecords(List<PIDRecord> records) {
		this.records = records;
	}
}
