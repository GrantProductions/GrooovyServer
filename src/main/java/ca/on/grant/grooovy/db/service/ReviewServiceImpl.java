package ca.on.grant.grooovy.db.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.on.grant.grooovy.db.entity.Review;
import ca.on.grant.grooovy.db.entity.Tag;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.entity.Vote;
import ca.on.grant.grooovy.db.entity.Vote.Type;
import ca.on.grant.grooovy.db.repository.ReviewRepository;
import ca.on.grant.grooovy.db.repository.TagRepository;
import ca.on.grant.grooovy.db.repository.VoteRepository;
import ca.on.grant.grooovy.response.GenericResponse;
import ca.on.grant.grooovy.util.ReviewVO;
import ca.on.grant.grooovy.util.TagVO;
import ca.on.grant.grooovy.util.UserVO;

@Service
public class ReviewServiceImpl implements ReviewService {
	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private VoteRepository voteRepository;

	public static UserVO userToUserVO(User u) {
		UserVO response = new UserVO(u.getId(), u.getUsername());
		return response;
	}

	public static TagVO tagToTagVO(Tag t) {
		TagVO response = new TagVO(t.getId(), t.getName(), t.getColor(), t.getOwner() != null);
		return response;
	}

	@Transactional
	public List<ReviewVO> getReviewsByUrl(String url, User user, String sortOption, String startsWith) {
		List<Review> reviews;
		boolean parsedStartsWith = false;
		if (startsWith != null) {
			startsWith = startsWith.trim();
			if (isBoolean(startsWith)) {
				parsedStartsWith = Boolean.parseBoolean(startsWith);
			}
		}
		LOG.info("parsedStartsWith [{}]", parsedStartsWith);
		if (sortOption == null) {
			if (parsedStartsWith) {
				reviews = reviewRepository.findByUrlStartsWithOrderByCreatedDateTimeDesc(url);
			} else {
				reviews = reviewRepository.findByUrlOrderByCreatedDateTimeDesc(url);
			}
		} else {
			sortOption = sortOption.trim().toLowerCase();
			if (sortOption.equals("recent")) {
				if (parsedStartsWith) {
					reviews = reviewRepository.findByUrlStartsWithOrderByCreatedDateTimeDesc(url);
				} else {
					reviews = reviewRepository.findByUrlOrderByCreatedDateTimeDesc(url);
				}
			} else if (sortOption.equals("older")) {
				if (parsedStartsWith) {
					reviews = reviewRepository.findByUrlStartsWithOrderByCreatedDateTimeAsc(url);
				} else {
					reviews = reviewRepository.findByUrlOrderByCreatedDateTimeAsc(url);
				}
			} else if (sortOption.equals("highest rating")) {
				if (parsedStartsWith) {
					reviews = reviewRepository.findByUrlStartsWithOrderByStarsDesc(url);
				} else {
					reviews = reviewRepository.findByUrlOrderByStarsDesc(url);
				}
			} else if (sortOption.equals("lowest rating")) {
				if (parsedStartsWith) {
					reviews = reviewRepository.findByUrlStartsWithOrderByStarsAsc(url);
				} else {
					reviews = reviewRepository.findByUrlOrderByStarsAsc(url);
				}
			} else {
				if (parsedStartsWith) {
					reviews = reviewRepository.findByUrlStartsWithOrderByCreatedDateTimeDesc(url);
				} else {
					reviews = reviewRepository.findByUrlOrderByCreatedDateTimeDesc(url);
				}
			}
		}
		List<ReviewVO> convertedReviews = new ArrayList<>(reviews.size());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, u").withLocale(new Locale("en-US"));
		final ZoneId zone = ZoneId.systemDefault();
		final long userId = user.getId();
		for (Review r : reviews) {
			if (!r.isPrivate() || r.isPrivate() && r.getAuthor().getId() == userId) {
				Set<TagVO> tags = new HashSet<>();
				final LocalDateTime createdDateTime = r.getCreatedDateTime();
				long epochTimestamp = createdDateTime.atZone(zone).toEpochSecond();

				final Set<Vote> votes = r.getVotes();
				Type userChoice = null;
				long totalScore = 0;
				for (Vote vote : votes) {
					if(vote.getDeletedDateTime() == null) {
						final Type voteType = vote.getType();
						if (vote.getUser().getId() == userId) {
							userChoice = voteType;
						}
						if (voteType.equals(Type.UP)) {
							totalScore++;
						} else {
							totalScore--;
						}
					}
				}
				ReviewVO response = new ReviewVO(r.getId(), r.isPrivate(), epochTimestamp,
						formatter.format(createdDateTime), r.getStars(), r.getText(), r.getUrl(),
						userToUserVO(r.getAuthor()), tags, totalScore, userChoice);
				for (Tag t : r.getTags()) {
					if (t.getOwner() == null || t.getOwner().getId() == userId) {
						tags.add(tagToTagVO(t));
					}
				}
				convertedReviews.add(response);
			}
		}
		return convertedReviews;
	}

	@Transactional
	public List<ReviewVO> getReviewsByUser(User user, String sortOption, String tagFilterId) {
		List<Review> reviews;
		final long userId = user.getId();

		Long parsedTagFilterId = null;
		Tag tagFilter = null;
		if (tagFilterId != null) {
			tagFilterId = tagFilterId.trim();
			if (isValidLong(tagFilterId)) {
				parsedTagFilterId = Long.parseLong(tagFilterId);
				tagFilter = tagRepository.findById(userId);
				User tagOwner = tagFilter.getOwner();
				if (tagOwner != null && tagOwner.getId() != userId) {
					parsedTagFilterId = null;
					tagOwner = null;
				}
			}
		}

		if (sortOption == null) {
			reviews = reviewRepository.findByAuthorOrderByCreatedDateTimeDesc(user);
		} else {
			sortOption = sortOption.trim().toLowerCase();
			if (sortOption.equals("recent")) {
				reviews = reviewRepository.findByAuthorOrderByCreatedDateTimeDesc(user);
			} else if (sortOption.equals("older")) {
				reviews = reviewRepository.findByAuthorOrderByCreatedDateTimeAsc(user);
			} else if (sortOption.equals("highest rating")) {
				reviews = reviewRepository.findByAuthorOrderByStarsDesc(user);
			} else if (sortOption.equals("lowest rating")) {
				reviews = reviewRepository.findByAuthorOrderByStarsAsc(user);
			} else {
				reviews = reviewRepository.findByAuthorOrderByCreatedDateTimeDesc(user);
			}
		}
		final int reviewsSize = reviews.size();
		List<ReviewVO> convertedReviews = new ArrayList<>(reviewsSize);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, u").withLocale(new Locale("en-US"));
		final ZoneId zone = ZoneId.systemDefault();
		for (Review r : reviews) {
			Set<TagVO> tags = new HashSet<>();
			final LocalDateTime createdDateTime = r.getCreatedDateTime();
			long epochTimestamp = createdDateTime.atZone(zone).toEpochSecond();

			final Set<Vote> votes = r.getVotes();
			Type userChoice = null;
			long totalScore = 0;
			for (Vote vote : votes) {
				if(vote.getDeletedDateTime() == null) {
					final Type voteType = vote.getType();
					if (vote.getUser().getId() == userId) {
						userChoice = voteType;
					}
					if (voteType.equals(Type.UP)) {
						totalScore++;
					} else {
						totalScore--;
					}
				}
			}
			ReviewVO response = new ReviewVO(r.getId(), r.isPrivate(), epochTimestamp,
					formatter.format(createdDateTime), r.getStars(), r.getText(), r.getUrl(),
					userToUserVO(r.getAuthor()), tags, totalScore, userChoice);
			boolean shouldAddReview = parsedTagFilterId == null;
			for (Tag t : r.getTags()) {
				final User owner = t.getOwner();
				if (owner == null || owner.getId() == userId) {
					tags.add(tagToTagVO(t));
					if (parsedTagFilterId != null && t.getId() == parsedTagFilterId) {
						shouldAddReview = true;
					}
				}
			}
			if (shouldAddReview) {
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

	@Transactional
	public GenericResponse voteReview(String postId, String action, User user) {
		if (postId == null) {
			return new GenericResponse(false, "Please specify a post id", null);
		}
		postId = postId.trim();

		if (postId.length() == 0) {
			return new GenericResponse(false, "Please specify a post id", null);
		}

		if (action == null) {
			return new GenericResponse(false, "Please specify an action", null);
		}
		action = action.trim().toUpperCase();
		final Type parsedAction;

		if (action.length() == 0) {
			return new GenericResponse(false, "Please specify an action", null);
		} else {
			if (action.equals("UP")) {
				parsedAction = Vote.Type.UP;
			} else if (action.equals("DOWN")) {
				parsedAction = Vote.Type.DOWN;
			} else {
				return new GenericResponse(false, "Invalid action", null);
			}
		}

		Long parsedPostId;

		if (!isValidLong(postId)) {
			return new GenericResponse(false, "Please specify a post id", null);
		}
		parsedPostId = Long.parseLong(postId);
		Review review = reviewRepository.findById((long) parsedPostId);

		final long userId = user.getId();
		LocalDateTime now = LocalDateTime.now();
		Type oldVoteType = null;
		if (review != null) {
			boolean userHasVoted = false;
			Set<Vote> votes = review.getVotes();
			for (Vote v : votes) {
				if (v.getUser().getId() == userId) {
					if (v.getDeletedDateTime() == null) {
						v.setDeletedDateTime(now);
						userHasVoted = true;
						oldVoteType = v.getType();
						break;
					}
				}
			}

			if (!userHasVoted) {
				Vote newVote = new Vote(parsedPostId, parsedAction, user, review, now, null);
				voteRepository.save(newVote);
			}else {
				if(oldVoteType != parsedAction) {
					Vote newVote = new Vote(parsedPostId, parsedAction, user, review, now, null);
					voteRepository.save(newVote);
				}
			}
			return new GenericResponse(true, null, null);
		} else {
			return new GenericResponse(false, "No review with id found", null);
		}
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
