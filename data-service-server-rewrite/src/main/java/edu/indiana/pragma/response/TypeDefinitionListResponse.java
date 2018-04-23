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
