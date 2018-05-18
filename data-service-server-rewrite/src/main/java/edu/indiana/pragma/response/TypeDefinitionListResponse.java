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

import edu.indiana.pragma.container.TypeDefinition;

public class TypeDefinitionListResponse {
	private boolean success;
	private List<TypeDefinition> typeDefinitions;

	public TypeDefinitionListResponse() {

	}

	public TypeDefinitionListResponse(boolean success, List<TypeDefinition> typeDefinitions) {
		this.success = success;
		this.typeDefinitions = typeDefinitions;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<TypeDefinition> getTypeDefinitions() {
		return typeDefinitions;
	}

	public void setTypeDefinitions(List<TypeDefinition> typeDefinitions) {
		this.typeDefinitions = typeDefinitions;
	}
}
