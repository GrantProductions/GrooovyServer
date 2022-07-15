package ca.on.grant.grooovy.config;

import org.springframework.stereotype.Component;

@Component
public class UserConfig {
	private int minUsernameLen = 4;
	private int maxUsernameLen = 64;
	private int maxEmailLen = 90;
	private int minPasswordLen = 6;
	private int maxPasswordLen = 40;
	private String usernameErrorAttr = "usernameError";
	private String emailErrorAttr = "emailError";
	private String passwordErrorAttr = "passwordError";
	private String usernameAttr = "username";
	private String emailAttr = "email";

	public int getMinUsernameLen() {
		return minUsernameLen;
	}

	public int getMaxUsernameLen() {
		return maxUsernameLen;
	}

	public int getMaxEmailLen() {
		return maxEmailLen;
	}

	public int getMinPasswordLen() {
		return minPasswordLen;
	}

	public int getMaxPasswordLen() {
		return maxPasswordLen;
	}

	public String getUsernameErrorAttr() {
		return usernameErrorAttr;
	}

	public String getEmailErrorAttr() {
		return emailErrorAttr;
	}

	public String getPasswordErrorAttr() {
		return passwordErrorAttr;
	}

	public String getUsernameAttr() {
		return usernameAttr;
	}

	public String getEmailAttr() {
		return emailAttr;
	}
}
