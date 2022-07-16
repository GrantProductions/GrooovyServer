package ca.on.grant.grooovy.db.service;

import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.on.grant.grooovy.db.entity.Review;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.repository.ReviewRepository;
import ca.on.grant.grooovy.response.GenericResponse;

@Service
public class ReviewServiceImpl implements ReviewService{
	@Autowired
	private ReviewRepository reviewRepository;
	
	public List<Review> getReviewsByUrl(String url){
		return reviewRepository.findByUrl(url);
	}

	@Override
	public GenericResponse addReview(User user, String url, String numOfStars, String text) {
		if(numOfStars == null) {
			return new GenericResponse(false, "Please specify a rating", null);
		}
		numOfStars = numOfStars.trim();
		if(numOfStars.length() == 0) {
			return new GenericResponse(false, "Please specify a rating", null);
		}else if(!isValidNum(numOfStars)) {
			return new GenericResponse(false, "Invalid rating", null);
		}else {
			int parsedNum = Integer.parseInt(numOfStars);
			if(parsedNum < 1 || parsedNum > 5) {
				return new GenericResponse(false, "Invalid rating", null);
			}
		}
		if(url == null) {
			return new GenericResponse(false, "Please specify a URL", null);
		}
		url = url.trim();
		if(url.length() == 0) {
			return new GenericResponse(false, "Please specify a URL", null);
		}else if(!new UrlValidator().isValid(url)) {
			return new GenericResponse(false, "Invalid URL", null);
		}
		
		if(text == null) {
			text = "";
		}else {
			text = text.trim();
		}
		
		return null;
	}
	
	private boolean isValidNum(String num) {
		try {
			Integer.parseInt(num);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
}
