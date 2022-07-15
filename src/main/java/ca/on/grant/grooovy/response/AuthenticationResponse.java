package ca.on.grant.grooovy.response;

public class AuthenticationResponse {
	private final String token;

	public AuthenticationResponse(final String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
