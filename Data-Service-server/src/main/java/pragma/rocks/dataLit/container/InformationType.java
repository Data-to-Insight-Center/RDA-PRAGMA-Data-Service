package pragma.rocks.dataLit.container;

/*
 * Information Type Definition for PID metadata
 */
public class InformationType {
	private String dbID;
	private String revID;
	private String title;
	private String creator;
	private String landingpageAddr;
	private String publicationDate;
	private String creationDate;
	private String checksum;
	private String dataIdentifier;
	private String parentID;
	private String childID;
	private String predecessorID;
	private String successorID;
	private String license;

	public InformationType() {
	}

	public String getdbID() {
		return dbID;
	}

	public void setdbID(String dbID) {
		this.dbID = dbID;
	}

	public String getRevID() {
		return revID;
	}

	public void setRevID(String revID) {
		this.revID = revID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
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

	public String getDataIdentifier() {
		return dataIdentifier;
	}

	public void setDataIdentifier(String dataIdentifier) {
		this.dataIdentifier = dataIdentifier;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public String getChildID() {
		return childID;
	}

	public void setChildID(String childID) {
		this.childID = childID;
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

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}
}
