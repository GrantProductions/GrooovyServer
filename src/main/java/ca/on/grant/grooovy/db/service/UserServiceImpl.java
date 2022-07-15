package ca.on.grant.grooovy.db.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ca.on.grant.grooovy.config.UserConfig;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.repository.UserRepository;

import static ca.on.grant.grooovy.config.MessageCodes.*;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserConfig userConfig;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	private final EmailValidator emailValidator = EmailValidator.getInstance();

	@Override
	public RegistrationResult registerUser(final String username, final String email, final String password,
			final String confirmpassword) {
		final Map<String, MessageVO> errors = new HashMap<>();
		if (username == null) {
			errors.put(userConfig.getUsernameErrorAttr(), new MessageVO(ENTER_USERNAME));
		} else if (username.length() < userConfig.getMinUsernameLen()) {
			errors.put(userConfig.getUsernameErrorAttr(),
					new MessageVO(USERNAME_MINLENGTH_REQUIRED, userConfig.getMinUsernameLen()));
		} else if (username.length() > userConfig.getMaxUsernameLen()) {
			errors.put(userConfig.getUsernameErrorAttr(),
					new MessageVO(USERNAME_MAXLENGTH_EXCEEDED, userConfig.getMinUsernameLen()));
		} else if (userRepository.findByUsernameIgnoreCase(username) != null) {
			errors.put(userConfig.getUsernameErrorAttr(), new MessageVO(USERNAME_ALREADYEXISTS));
		}
		if (email == null) {
			errors.put(userConfig.getEmailErrorAttr(), new MessageVO(ENTER_EMAIL));
		} else if (email.length() > userConfig.getMaxEmailLen()) {
			errors.put(userConfig.getEmailErrorAttr(),
					new MessageVO("email.maxlength.exceeded", userConfig.getMaxEmailLen()));
		} else if (!emailValidator.isValid(email)) {
			errors.put(userConfig.getEmailErrorAttr(), new MessageVO(EMAIL_INVALID));
		} else if (userRepository.findByEmailIgnoreCase(email) != null) {
			errors.put(userConfig.getEmailErrorAttr(), new MessageVO(EMAIL_ALREADYEXISTS));
		}
		if (password == null) {
			errors.put(userConfig.getPasswordErrorAttr(), new MessageVO(ENTER_PASSWORD));
		} else if (password.length() < userConfig.getMinPasswordLen()) {
			errors.put(userConfig.getPasswordErrorAttr(),
					new MessageVO(PASSWORD_MINLENGTH_REQUIRED, userConfig.getMinPasswordLen()));
		} else if (password.length() > userConfig.getMaxPasswordLen()) {
			errors.put(userConfig.getPasswordErrorAttr(),
					new MessageVO(PASSWORD_MAXLENGTH_EXCEEDED, userConfig.getMaxPasswordLen()));
		} else if (!password.equals(confirmpassword)) {
			errors.put(userConfig.getPasswordErrorAttr(), new MessageVO(PASSWORD_CONFIRM_NOTMATCHED));
		}
		if (errors.isEmpty()) {
			final User user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(passwordEncoder.encode(password));
			return new RegistrationResult(userRepository.save(user), errors);
		} else {
			return new RegistrationResult(null, errors);
		}
	}

	@Override
	public User validateLogIn(String username, String password) {
		if (username == null || password == null)
			return null;
		final User user = userRepository.findByUsernameIgnoreCase(username);
		if (user == null)
			return null;
		if (passwordEncoder.matches(password, user.getPassword()))
			return user;
		return null;
	}
}
