package ca.on.grant.grooovy.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.repository.UserRepository;
import ca.on.grant.grooovy.util.JWTUtil;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
	private static final Logger LOG = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;

	public JWTAuthorizationFilter(final JWTUtil jwtUtil, final UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		LOG.debug("JWTAuthorizationFilter");
		UsernamePasswordAuthenticationToken authentication = getUserFromAuthorization(request);
		LOG.debug("AuthenticationToken: [{}]", authentication);
		if (authentication != null) {
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} else {
			SecurityContextHolder.clearContext();
		}
		filterChain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getUserFromAuthorization(final HttpServletRequest request) {
		final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		LOG.info("Authorization [{}]", authorization);
		if (authorization != null && authorization.startsWith("Bearer ")) {
			final User user = userRepository
					.findByUsernameIgnoreCase(this.jwtUtil.decode(authorization.substring(7)).getSubject());
			return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
		}
		return null;
	}
}
