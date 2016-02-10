package pragma.rocks.dataLit.response;

import pragma.rocks.dataLit.container.InformationType;

public class InformationTypeResponse {
	private boolean success;
	private String pid;
	private InformationType informationtype;

	public InformationTypeResponse() {

	}

	public InformationTypeResponse(boolean success, String pid, InformationType informationtype) {
		this.success = success;
		this.pid = pid;
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

	public InformationType getInformationtype() {
		return informationtype;
	}

	public void setInformationtype(InformationType informationtype) {
		this.informationtype = informationtype;
	}

}
