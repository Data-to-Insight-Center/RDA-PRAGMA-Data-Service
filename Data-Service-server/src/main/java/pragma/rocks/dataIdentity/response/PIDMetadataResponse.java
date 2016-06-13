package pragma.rocks.dataIdentity.response;

import pragma.rocks.dataIdentity.container.PIDMetadata;

public class PIDMetadataResponse {
	private boolean success;
	private PIDMetadata PIDMetadata;

	public PIDMetadataResponse() {

	}

	public PIDMetadataResponse(boolean success, PIDMetadata pid_metadata) {
		this.success = success;
		this.PIDMetadata = pid_metadata;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public PIDMetadata getPIDMetadata() {
		return PIDMetadata;
	}

	public void setPIDMetadata(PIDMetadata pIDMetadata) {
		PIDMetadata = pIDMetadata;
	}
}
