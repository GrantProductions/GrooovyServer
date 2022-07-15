package ca.on.grant.grooovy.db.service;

import ca.on.grant.grooovy.db.entity.User;

public interface UserService {
	RegistrationResult registerUser(final String username, final String email, final String password,
			final String confirmpassword);
	User validateLogIn(final String username, final String password);
}
