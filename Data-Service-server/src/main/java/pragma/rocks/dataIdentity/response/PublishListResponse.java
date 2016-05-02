package pragma.rocks.dataIdentity.response;

import java.util.List;

import pragma.rocks.dataIdentity.container.PublishType;

public class PublishListResponse {
	private boolean success;
	private List<PublishType> PublishList;

	public PublishListResponse(boolean success, List<PublishType> PublishList) {
		this.success = success;
		this.PublishList = PublishList;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<PublishType> getPublishList() {
		return PublishList;
	}

	public void setPublishList(List<PublishType> publishList) {
		PublishList = publishList;
	}
}
