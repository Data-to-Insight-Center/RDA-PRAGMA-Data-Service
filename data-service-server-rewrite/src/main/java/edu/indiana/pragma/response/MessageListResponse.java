package edu.indiana.pragma.response;

import java.util.List;

public class MessageListResponse {
	private boolean success;
	private List<String> messages;

	public MessageListResponse() {

	}

	public MessageListResponse(boolean success, List<String> messages) {
		this.success = success;
		this.messages = messages;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
