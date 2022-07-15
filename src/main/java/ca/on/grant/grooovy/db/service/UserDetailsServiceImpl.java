package ca.on.grant.grooovy.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final User user = userRepository.findByUsernameIgnoreCase(username);
		if (user == null)
			throw new UsernameNotFoundException("No user found with username: " + username);
		return user;
	}
}
