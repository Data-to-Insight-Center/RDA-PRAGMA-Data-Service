/*
 *
 * Copyright 2018 The Trustees of Indiana University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @creator quzhou@umail.iu.edu
 * @rewritten by kunarath@iu.edu
 */
package edu.indiana.pragma.container;

import org.bson.Document;

public class PIDRecord extends Document {
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
