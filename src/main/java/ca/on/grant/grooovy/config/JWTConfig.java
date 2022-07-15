package ca.on.grant.grooovy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JWTConfig {
	private final Algorithm algorithmHS;
	private final JWTVerifier verifier;
	private final String tokenIssuer = "Grooovy";
	private final String authorizationParameter = "Authorization";
	private final String subjectParameter = "authorized_subject";
	private final long expiryTimeSeconds = 60 * 1440; // 24 hours

	public JWTConfig(@Value("${jwt.secret}") final String secret) {
		this.algorithmHS = Algorithm.HMAC256(secret);
		this.verifier = JWT.require(algorithmHS).withIssuer(tokenIssuer).build();
	}

	public Algorithm getAlgorithmHS() {
		return algorithmHS;
	}

	public JWTVerifier getVerifier() {
		return verifier;
	}

	public String getTokenIssuer() {
		return tokenIssuer;
	}

	public String getAuthorizationParameter() {
		return authorizationParameter;
	}

	public String getSubjectParameter() {
		return subjectParameter;
	}

	public long getExpiryTimeSeconds() {
		return expiryTimeSeconds;
	}
}
