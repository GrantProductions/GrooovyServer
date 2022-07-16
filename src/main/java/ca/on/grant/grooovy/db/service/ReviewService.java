package ca.on.grant.grooovy.db.service;

import java.util.List;

import ca.on.grant.grooovy.db.entity.Review;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.response.GenericResponse;

public interface ReviewService {
	List<Review> getReviewsByUrl(String url);
	GenericResponse addReview(User user, String url, String numOfStars, String text, String isPrivate, String[] ids);
}
