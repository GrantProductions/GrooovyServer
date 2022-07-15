package ca.on.grant.grooovy.db.service;

import java.util.Map;

import ca.on.grant.grooovy.db.entity.User;

public class RegistrationResult {
	private final User user;
	private final Map<String, MessageVO> errors;

	public RegistrationResult(User user, Map<String, MessageVO> errors) {
		super();
		this.user = user;
		this.errors = errors;
	}

	public User getUser() {
		return user;
	}

	public Map<String, MessageVO> getErrors() {
		return errors;
	}
}
