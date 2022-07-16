package ca.on.grant.grooovy.db.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.on.grant.grooovy.db.entity.Review;
import ca.on.grant.grooovy.db.entity.Tag;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.repository.ReviewRepository;
import ca.on.grant.grooovy.db.repository.TagRepository;
import ca.on.grant.grooovy.response.GenericResponse;

@Service
public class ReviewServiceImpl implements ReviewService {
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private TagRepository tagRepository;

	public List<Review> getReviewsByUrl(String url) {
		return reviewRepository.findByUrl(url);
	}

	@Override
	public GenericResponse addReview(User user, String url, String numOfStars, String text, String isPrivate,
			String[] ids) {
		if (numOfStars == null) {
			return new GenericResponse(false, "Please specify a rating", null);
		}
		numOfStars = numOfStars.trim();
		if (numOfStars.length() == 0) {
			return new GenericResponse(false, "Please specify a rating", null);
		} else if (!isValidNum(numOfStars)) {
			return new GenericResponse(false, "Invalid rating", null);
		}

		int parsedNumOfStars = Integer.parseInt(numOfStars);
		if (parsedNumOfStars < 1 || parsedNumOfStars > 5) {
			return new GenericResponse(false, "Invalid rating", null);
		}
		if (url == null) {
			return new GenericResponse(false, "Please specify a URL", null);
		}
		url = url.trim();
		if (url.length() == 0) {
			return new GenericResponse(false, "Please specify a URL", null);
		} else if (!new UrlValidator().isValid(url)) {
			return new GenericResponse(false, "Invalid URL", null);
		}

		if (text == null) {
			text = "";
		} else {
			text = text.trim();
		}

		if (isPrivate == null) {
			return new GenericResponse(false, "Please specify a privacy option", null);
		}
		isPrivate = isPrivate.trim();
		if (isPrivate.length() == 0) {
			return new GenericResponse(false, "Please specify a privacy option", null);
		} else if (!isBoolean(isPrivate)) {
			return new GenericResponse(false, "Invalid privacy option", null);
		}

		final boolean parsedIsPrivate = Boolean.parseBoolean(isPrivate);
		LocalDateTime now = LocalDateTime.now();
		Set<Tag> tags = new HashSet<>();
		
		if(ids != null) {
			final long userId = user.getId();
			for(String id : ids) {
				if(id != null) {
					final String trimmed = id.trim();
					if(trimmed.length() > 0 && isValidLong(trimmed)) {
						final long parsedId = Long.parseLong(trimmed);
						Tag t = tagRepository.findById(parsedId);
						if(t != null) {
							final User owner = t.getOwner();
							if(owner == null || owner != null && owner.getId() == userId) {
								tags.add(t);
							}
						}
					}
				}
			}
		}
		Review newReview = new Review(parsedIsPrivate, now, parsedNumOfStars, text, url, user, tags);
		reviewRepository.save(newReview);
		return new GenericResponse(true, null, null);
	}

	private boolean isValidLong(String num) {
		try {
			Long.parseLong(num);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isValidNum(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isBoolean(String bool) {
		try {
			Boolean.parseBoolean(bool);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
