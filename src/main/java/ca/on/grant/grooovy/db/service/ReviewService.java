package ca.on.grant.grooovy.db.service;

import java.util.List;

import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.response.GenericResponse;
import ca.on.grant.grooovy.util.ReviewVO;

public interface ReviewService {
	List<ReviewVO> getReviewsByUrl(String url, User user, String sortOption);
	GenericResponse addReview(User user, String url, String numOfStars, String text, String isPrivate, String[] ids);
}
