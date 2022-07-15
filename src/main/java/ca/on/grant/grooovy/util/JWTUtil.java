package ca.on.grant.grooovy.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import ca.on.grant.grooovy.config.JWTConfig;

@Component
public class JWTUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JWTUtil.class);
	@Autowired
	private JWTConfig jwtConfig;

	public String getToken(final String username) {
		return JWT.create().withIssuer(jwtConfig.getTokenIssuer()).withSubject(username)
				.withExpiresAt(new Date(new Date().getTime() + jwtConfig.getExpiryTimeSeconds() * 1000))
				.sign(jwtConfig.getAlgorithmHS());
	}
	
	public DecodedJWT decode(final String token) {
		try {
			return jwtConfig.getVerifier().verify(token);
		} catch (JWTVerificationException e) {
			LOG.error("Authentication failed", e);
		}
		return null;
	}
}
