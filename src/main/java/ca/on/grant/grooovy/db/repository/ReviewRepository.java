package ca.on.grant.grooovy.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.on.grant.grooovy.db.entity.Review;
import ca.on.grant.grooovy.db.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long>{
	Review findById(long id);
	
	List<Review> findByUrl(String url);
	List<Review> findByUrlOrderByCreatedDateTimeDesc(String url);//most recent
	List<Review> findByUrlOrderByCreatedDateTimeAsc(String url);//most old
	List<Review> findByUrlOrderByStarsDesc(String url); //most stars
	List<Review> findByUrlOrderByStarsAsc(String url); //least stars
	
	List<Review> findByUrlStartsWithOrderByCreatedDateTimeDesc(String url);//most recent
	List<Review> findByUrlStartsWithOrderByCreatedDateTimeAsc(String url);//most old
	List<Review> findByUrlStartsWithOrderByStarsDesc(String url); //most stars
	List<Review> findByUrlStartsWithOrderByStarsAsc(String url); //least stars
	
	List<Review> findByAuthor(User user);
	List<Review> findByAuthorOrderByCreatedDateTimeDesc(User user);
	List<Review> findByAuthorOrderByCreatedDateTimeAsc(User user);
	List<Review> findByAuthorOrderByStarsDesc(User user);
	List<Review> findByAuthorOrderByStarsAsc(User user);
}
