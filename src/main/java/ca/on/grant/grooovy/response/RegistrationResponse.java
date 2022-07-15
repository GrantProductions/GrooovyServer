package ca.on.grant.grooovy.response;

import java.util.Map;

public class RegistrationResponse {
	private final String token;
	private final Map<String, String> errors;

	public RegistrationResponse(final String token, final Map<String, String> errors) {
		super();
		this.token = token;
		this.errors = errors;
	}

	public String getToken() {
		return token;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
