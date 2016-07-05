package dataIdentity.server.pragma.response;

import dataIdentity.server.pragma.container.PIDRecord;

public class PIDRecordResponse {
	boolean success;
	PIDRecord record;

	public PIDRecordResponse() {
	}

	public PIDRecordResponse(boolean success, PIDRecord record) {
		this.success = success;
		this.record = record;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public PIDRecord getRecord() {
		return record;
	}

	public void setRecord(PIDRecord record) {
		this.record = record;
	}
}
