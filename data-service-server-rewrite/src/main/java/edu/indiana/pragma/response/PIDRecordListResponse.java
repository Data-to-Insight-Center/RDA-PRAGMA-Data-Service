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
 */
package edu.indiana.pragma.response;

import java.util.List;

import edu.indiana.pragma.container.PIDRecord;

public class PIDRecordListResponse {
	private boolean success;
	private List<PIDRecord> records;

	public PIDRecordListResponse() {

	}

	public PIDRecordListResponse(boolean success, List<PIDRecord> records) {
		this.success = success;
		this.records = records;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<PIDRecord> getRecords() {
		return records;
	}

	public void setRecords(List<PIDRecord> records) {
		this.records = records;
	}
}
