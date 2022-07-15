package ca.on.grant.grooovy.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.on.grant.grooovy.db.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByUsernameIgnoreCase(String username);
	User findByEmailIgnoreCase(String email);
}
