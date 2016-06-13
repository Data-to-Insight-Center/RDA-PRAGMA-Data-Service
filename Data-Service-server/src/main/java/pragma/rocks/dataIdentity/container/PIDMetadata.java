package pragma.rocks.dataIdentity.container;

/*
 * Information Type Definition for PID metadata
 */
public class PIDMetadata {
	private String PID;
	private String metadataURL;
	private String landingpageAddr;
	private String creationDate;
	private String checksum;
	private String predecessorID;
	private String successorID;

	public PIDMetadata() {
	}
	
	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}
	
	public String getLandingpageAddr() {
		return landingpageAddr;
	}

	public void setLandingpageAddr(String landingpageAddr) {
		this.landingpageAddr = landingpageAddr;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getPredecessorID() {
		return predecessorID;
	}

	public void setPredecessorID(String predecessorID) {
		this.predecessorID = predecessorID;
	}

	public String getSuccessorID() {
		return successorID;
	}

	public void setSuccessorID(String successorID) {
		this.successorID = successorID;
	}

	public String getMetadataURL() {
		return metadataURL;
	}

	public void setMetadataURL(String metadataURL) {
		this.metadataURL = metadataURL;
	}
}
