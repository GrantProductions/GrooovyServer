package ca.on.grant.grooovy.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.on.grant.grooovy.db.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
