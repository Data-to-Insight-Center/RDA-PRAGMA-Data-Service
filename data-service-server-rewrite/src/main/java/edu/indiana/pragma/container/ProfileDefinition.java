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
package edu.indiana.pragma.container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Encapsulates a type in the type registry, roughly defined as a set of
 * properties.
 * 
 */
public class ProfileDefinition {

	/**
	 * PID of the profile.
	 */
	@JsonProperty("identifier")
	protected String identifier;

	/**
	 * Value (boolean) True means mandatory, False means optional.
	 */
	@JsonProperty("types")
	protected HashMap<String, TypeDefinition> types;

	@JsonProperty("explanationOfUse")
	protected String explanationOfUse;

	@JsonProperty("description")
	protected String description;

	public ProfileDefinition(String identifier, String explanationOfUse, String description) {
		this.identifier = identifier;
		this.explanationOfUse = explanationOfUse;
		this.description = description;
		this.types = new HashMap<String, TypeDefinition>();
	}

	@JsonCreator
	public ProfileDefinition(@JsonProperty("identifier") String identifier,
			@JsonProperty("explanationOfUse") String explanationOfUse, @JsonProperty("description") String description,
			@JsonProperty("types") HashMap<String, TypeDefinition> types) {
		this.identifier = identifier;
		this.explanationOfUse = explanationOfUse;
		this.description = description;
		this.types = types;
	}

	public void addType(String typeIdentifier, TypeDefinition type_def) {
		this.types.put(typeIdentifier, type_def);
	}

	/**
	 * Returns a set of all properties. The caller will not be able to
	 * distinguish between mandatory and optional properties.
	 * 
	 * @return a set of property identifiers (strings)
	 */
	@JsonIgnore
	public Set<String> getAllTypes() {
		return new HashSet<String>(types.keySet());
	}

	public String getIdentifier() {
		return identifier;
	}
}
