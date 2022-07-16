package ca.on.grant.grooovy.db.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.on.grant.grooovy.db.entity.Review;
import ca.on.grant.grooovy.db.entity.Tag;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.repository.ReviewRepository;
import ca.on.grant.grooovy.db.repository.TagRepository;
import ca.on.grant.grooovy.response.GenericResponse;
import ca.on.grant.grooovy.util.ReviewVO;
import ca.on.grant.grooovy.util.TagVO;
import ca.on.grant.grooovy.util.UserVO;

@Service
public class ReviewServiceImpl implements ReviewService {
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private TagRepository tagRepository;

	public static UserVO userToUserVO(User u) {
		UserVO response = new UserVO(u.getId(), u.getUsername());
		return response;
	}

	public static TagVO tagToTagVO(Tag t) {
		TagVO response = new TagVO(t.getId(), t.getName(), t.getColor(), t.getOwner() != null);
		return response;
	}

	@Transactional
	public List<ReviewVO> getReviewsByUrl(String url, User user, String sortOption) {
		List<Review> reviews;
		if(sortOption == null) {
			reviews = reviewRepository.findByUrlOrderByCreatedDateTimeDesc(url);
		}else {
			sortOption = sortOption.trim().toLowerCase();
			if(sortOption.equals("recent")) {
				reviews = reviewRepository.findByUrlOrderByCreatedDateTimeDesc(url);
			}else if(sortOption.equals("older")) {
				reviews = reviewRepository.findByUrlOrderByCreatedDateTimeAsc(url);
			}else if(sortOption.equals("highest rating")) {
				reviews = reviewRepository.findByUrlOrderByStarsDesc(url);
			}else if(sortOption.equals("lowest rating")){
				reviews = reviewRepository.findByUrlOrderByStarsAsc(url);
			}else {
				reviews = reviewRepository.findByUrlOrderByCreatedDateTimeDesc(url);
			}
		}
		List<ReviewVO> convertedReviews = new ArrayList<>(reviews.size());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, u").withLocale(new Locale("en-US"));
		final ZoneId zone = ZoneId.systemDefault();
		final long userId = user.getId();
		for (Review r : reviews) {
			if(!r.isPrivate() || r.isPrivate() && r.getAuthor().getId() == userId) {
				Set<TagVO> tags = new HashSet<>();
				final LocalDateTime createdDateTime = r.getCreatedDateTime();
				long epochTimestamp = createdDateTime.atZone(zone).toEpochSecond();
				ReviewVO response = new ReviewVO(r.getId(), r.isPrivate(), epochTimestamp,
						formatter.format(createdDateTime), r.getStars(), r.getText(), userToUserVO(r.getAuthor()), tags);
				for (Tag t : r.getTags()) {
					tags.add(tagToTagVO(t));
				}
				convertedReviews.add(response);
			}
		}
		return convertedReviews;
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

		if (ids != null) {
			final long userId = user.getId();
			for (String id : ids) {
				if (id != null) {
					final String trimmed = id.trim();
					if (trimmed.length() > 0 && isValidLong(trimmed)) {
						final long parsedId = Long.parseLong(trimmed);
						Tag t = tagRepository.findById(parsedId);
						if (t != null) {
							final User owner = t.getOwner();
							if (owner == null || owner != null && owner.getId() == userId) {
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
