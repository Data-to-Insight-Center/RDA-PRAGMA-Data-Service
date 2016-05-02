package pragma.rocks.dataIdentity.response;

import pragma.rocks.dataIdentity.container.Provenance;

public class ProvenanceResponse {
	private boolean success;
	private Provenance provenance;

	public ProvenanceResponse() {

	}

	public ProvenanceResponse(boolean success, Provenance provenance) {
		this.success = success;
		this.provenance = provenance;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Provenance getProvenance() {
		return provenance;
	}

	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}
}
