package pragma.rocks.dataIdentity.response;

import pragma.rocks.dataIdentity.container.InformationType;

public class InformationTypeResponse {
	private boolean success;
	private String pid;
	private String id;
	private String revID;
	private InformationType informationtype;

	public InformationTypeResponse() {

	}

	public InformationTypeResponse(boolean success, String pid, String id, String revID,
			InformationType informationtype) {
		this.success = success;
		this.pid = pid;
		this.id = id;
		this.revID = revID;
		this.informationtype = informationtype;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public InformationType getInformationtype() {
		return informationtype;
	}

	public void setInformationtype(InformationType informationtype) {
		this.informationtype = informationtype;
	}

}
