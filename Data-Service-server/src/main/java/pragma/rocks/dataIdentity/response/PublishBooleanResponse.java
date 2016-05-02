package pragma.rocks.dataIdentity.response;

import java.util.List;

public class PublishBooleanResponse {
	private boolean success;
	private List<PublishBoolean> publishbooleanlist;

	public PublishBooleanResponse() {

	}

	public PublishBooleanResponse(boolean success, List<PublishBoolean> publishbooleanlist) {
		this.success = success;
		this.publishbooleanlist = publishbooleanlist;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<PublishBoolean> getPublishbooleanlist() {
		return publishbooleanlist;
	}

	public void setPublishbooleanlist(List<PublishBoolean> publishbooleanlist) {
		this.publishbooleanlist = publishbooleanlist;
	}
}
