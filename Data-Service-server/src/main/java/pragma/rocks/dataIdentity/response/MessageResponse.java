package pragma.rocks.dataIdentity.response;

/**
 * 
 * Normal message rest response
 *
 */
public class MessageResponse {
	private boolean success;
	private String message;

	public MessageResponse() {

	}

	public MessageResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
