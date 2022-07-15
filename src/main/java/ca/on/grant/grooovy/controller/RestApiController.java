package ca.on.grant.grooovy.controller;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.service.RegistrationResult;
import ca.on.grant.grooovy.db.service.UserService;
import ca.on.grant.grooovy.response.AuthenticationResponse;
import ca.on.grant.grooovy.response.RegistrationResponse;
import ca.on.grant.grooovy.util.JWTUtil;

@RestController
@RequestMapping("/api")
public class RestApiController {
	@Autowired
	private UserService userService;
	@Autowired
	private JWTUtil jwtUtil;
	private static final Logger LOG = LoggerFactory.getLogger(RestApiController.class);

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestParam("username") final String username,
			@RequestParam("password") final String password) {
		LOG.info("authenticate");
		final User user = userService.validateLogIn(username, password);
		final ResponseEntity<AuthenticationResponse> response;
		if (user == null) {
			LOG.info("Not validated");
			response = new ResponseEntity<>(new AuthenticationResponse("Unauthorized"), HttpStatus.UNAUTHORIZED);
		} else {
			response = ResponseEntity.ok(new AuthenticationResponse(jwtUtil.getToken(user.getUsername())));
		}
		return response;
	}

	@PostMapping("/register")
	public ResponseEntity<RegistrationResponse> register(@RequestParam("username") final String username,
			@RequestParam("email") final String email, @RequestParam("password") final String password,
			@RequestParam("confirmpassword") final String confirmpassword) {
		LOG.info("Register");
		final ResponseEntity<RegistrationResponse> response;
		final RegistrationResult result = userService.registerUser(username, email, password, confirmpassword);
		if (result.getErrors().isEmpty()) {
			response = ResponseEntity.ok(new RegistrationResponse(jwtUtil.getToken(result.getUser().getUsername()), null));
		} else {
			final Locale locale = LocaleContextHolder.getLocale();
			final Map<String, String> convertedErrors = result.getErrors().entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey,
							e -> e.getValue().getKey()));
			response = ResponseEntity.badRequest().body(new RegistrationResponse(null, convertedErrors));
		}
		return response;
	}
}
