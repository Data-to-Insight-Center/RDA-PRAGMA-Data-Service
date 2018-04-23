package edu.indiana.pragma.container;

public class PIDRecord {
	private String id;

	private String PID;
	private String PIDMetadataType;
	private String DOname;
	private String DataType;
	private String repoID;
	private PIDProvider pidProvider;

	public PIDRecord() {


	}

	public PIDRecord(String PID, String PIDMetadataType, String DOname, String DataType, String repoID,
			PIDProvider pidProvider) {
		super();
		this.PID = PID;
		this.PIDMetadataType = PIDMetadataType;
		this.DOname = DOname;
		this.DataType = DataType;
		this.repoID = repoID;
		this.pidProvider = pidProvider;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		this.PID = pID;
	}

	public String getPIDMetadataType() {
		return PIDMetadataType;
	}

	public void setPIDMetadataType(String pIDMetadataType) {
		PIDMetadataType = pIDMetadataType;
	}

	public String getDOname() {
		return DOname;
	}

	public void setDOname(String dOname) {
		this.DOname = dOname;
	}

	public String getDataType() {
		return DataType;
	}

	public void setDataType(String dataType) {
		this.DataType = dataType;
	}

	public String getRepoID() {
		return repoID;
	}

	public void setRepoID(String repoID) {
		this.repoID = repoID;
	}

	public PIDProvider getPidProvider() {
		return pidProvider;
	}

	public void setPidProvider(PIDProvider pidProvider) {
		this.pidProvider = pidProvider;
	}

}
