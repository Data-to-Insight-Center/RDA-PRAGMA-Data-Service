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

import edu.indiana.pragma.container.PIDRecord;

public class PIDRecordResponse {
	boolean success;
	PIDRecord record;

	public PIDRecordResponse() {
	}

	public PIDRecordResponse(boolean success, PIDRecord record) {
		this.success = success;
		this.record = record;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public PIDRecord getRecord() {
		return record;
	}

	public void setRecord(PIDRecord record) {
		this.record = record;
	}
}
