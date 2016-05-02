package pragma.rocks.dataIdentity.response;

import java.util.List;

import pragma.rocks.dataIdentity.container.DOType;

public class DOListResponse {
	private boolean success;
	private List<DOType> DOList;

	public DOListResponse() {

	}

	public DOListResponse(boolean success, List<DOType> DOList) {
		this.success = success;
		this.DOList = DOList;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<DOType> getDOList() {
		return DOList;
	}

	public void setDOList(List<DOType> dOList) {
		DOList = dOList;
	}
}
