package ca.on.grant.grooovy.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.on.grant.grooovy.db.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
	List<Review> findByUrl(String url);
}
