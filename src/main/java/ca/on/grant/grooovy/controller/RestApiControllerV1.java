package ca.on.grant.grooovy.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.on.grant.grooovy.db.entity.Review;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.service.RegistrationResult;
import ca.on.grant.grooovy.db.service.ReviewService;
import ca.on.grant.grooovy.db.service.TagService;
import ca.on.grant.grooovy.db.service.UserService;
import ca.on.grant.grooovy.response.AuthenticationResponse;
import ca.on.grant.grooovy.response.GenericResponse;
import ca.on.grant.grooovy.response.RegistrationResponse;
import ca.on.grant.grooovy.response.ReviewListResponse;
import ca.on.grant.grooovy.util.JWTUtil;
import ca.on.grant.grooovy.util.ReviewVO;

@RestController
@RequestMapping("/api/v1")
public class RestApiControllerV1 {
	@Autowired
	private UserService userService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private TagService tagService;
	@Autowired
	private JWTUtil jwtUtil;
	private static final Logger LOG = LoggerFactory.getLogger(RestApiControllerV1.class);

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestParam("username") final String username,
			@RequestParam("password") final String password) {
		LOG.info("authenticate");
		final User user = userService.validateLogIn(username, password);
		final ResponseEntity<AuthenticationResponse> response;
		if (user == null) {
			LOG.info("Not validated");
			response = new ResponseEntity<>(new AuthenticationResponse("Unauthorized"), HttpStatus.UNAUTHORIZED);
		} else {
			response = ResponseEntity.ok(new AuthenticationResponse(jwtUtil.getToken(user.getUsername())));
		}
		return response;
	}

	@PostMapping("/register")
	public ResponseEntity<RegistrationResponse> register(@RequestParam("username") final String username,
			@RequestParam("email") final String email, @RequestParam("password") final String password,
			@RequestParam("confirmpassword") final String confirmpassword) {
		LOG.info("Register");
		final ResponseEntity<RegistrationResponse> response;
		final RegistrationResult result = userService.registerUser(username, email, password, confirmpassword);
		if (result.getErrors().isEmpty()) {
			LOG.info("Successfully registered user with username [{}] email [{}] password [{}] confirmpassword [{}]",
					username, email, password, confirmpassword);
			response = ResponseEntity
					.ok(new RegistrationResponse(jwtUtil.getToken(result.getUser().getUsername()), null));
		} else {
			final Locale locale = LocaleContextHolder.getLocale();
			final Map<String, String> convertedErrors = result.getErrors().entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey,
							e -> messageSource.getMessage(e.getValue().getKey(), e.getValue().getArguments(), locale)));
			LOG.info(
					"Failed to register user with username [{}] email [{}] password [{}] confirmpassword [{}]. Reasons:",
					username, email, password, confirmpassword);
			for (Map.Entry<String, String> error : convertedErrors.entrySet()) {
				LOG.info(error.getKey() + ": " + error.getValue());
			}
			response = ResponseEntity.badRequest().body(new RegistrationResponse(null, convertedErrors));
		}
		return response;
	}

	@GetMapping("/reviews")
	public ResponseEntity<GenericResponse> getReviewList(@RequestParam("url") final String url,
			@RequestParam(value = "sortOption", required = false) final String sortOption,
			@RequestParam(value = "startsWith", required = false) final String startsWith,
			Authentication authentication) {
		LOG.info("/reviews: url [{}] sortOptions [{}] startsWith [{}]", url, sortOption, startsWith);
		User user = (User) authentication.getPrincipal();
		if(url == null || url.trim().length() == 0) {
			return ResponseEntity.badRequest().body(new GenericResponse(false, "Could not retrieve URL", null));
		}
		
		List<ReviewVO> reviews = reviewService.getReviewsByUrl(url, user, sortOption, startsWith);
		final int numOfReviews = reviews.size();
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 0);
		count.put(2, 0);
		count.put(3, 0);
		count.put(4, 0);
		count.put(5, 0);
		int sum = 0;
		for (ReviewVO r : reviews) {
			final int stars = r.getStars();
			count.put(stars, count.get(stars) + 1);
			sum += stars;
		}

		final double averageRating = numOfReviews == 0 ? 0 : sum / (double) numOfReviews;

		return ResponseEntity.ok(new GenericResponse(true, null, new ReviewListResponse(averageRating, numOfReviews, reviews, count.get(5),
				count.get(4), count.get(3), count.get(2), count.get(1))));
	}
	
	@PostMapping("/reviews/me")
	public ResponseEntity<GenericResponse> getUserReviews(
			@RequestParam(value = "sortOption", required = false) final String sortOption,
			@RequestParam(value = "tagFilter", required = false) final String tagFilterId,
			Authentication authentication){
		try {
			LOG.info("/reviews/me: sortOption [{}], tagFilter [{}]", sortOption, tagFilterId);	
			User user = (User) authentication.getPrincipal();
			List<ReviewVO> userReviews = reviewService.getReviewsByUser(user, sortOption, tagFilterId);
			return ResponseEntity.ok(new GenericResponse(true, null, userReviews));
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new GenericResponse(false, "An error occurred", null));
		}
	}
	
	@PostMapping("/reviews/new")
	public ResponseEntity<GenericResponse> addReview(@RequestParam("url") final String url,
			@RequestParam("stars") final String numOfStars, @RequestParam("text") final String text,
			@RequestParam(value = "tagIDs[]", required = false) final String[] tagIDs,
			@RequestParam("isPrivate") final String isPrivate, Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		LOG.info("/reviews/new: url [{}] numOfStars [{}] text [{}] tagIDs [{}] isPrivate [{}]", url, numOfStars, text,
				tagIDs != null ? Arrays.toString(tagIDs) : "none provided", isPrivate);
		final GenericResponse response = reviewService.addReview(user, url, numOfStars, text, isPrivate, tagIDs);
		if (response.isSuccess()) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/reviews/vote")
	public ResponseEntity<GenericResponse> voteReview(@RequestParam("postId") final String postId,
			@RequestParam("action") final String action, Authentication authentication){
		User user = (User) authentication.getPrincipal();
		final GenericResponse response = reviewService.voteReview(postId, action, user);
		if(response.isSuccess()) {
			return ResponseEntity.ok(response);
		}else {
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	
	@GetMapping("/gettags")
	public ResponseEntity<GenericResponse> getTags(@RequestParam("query") final String query,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		if (query == null || query.trim().length() == 0) {
			return ResponseEntity.badRequest().body(new GenericResponse(false, "Please specify a query", null));
		}
		GenericResponse result = tagService.getTags(user, query);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/newtag")
	public ResponseEntity<GenericResponse> createTag(@RequestParam(value = "name", required = false) final String name,
			@RequestParam(value = "color", required = false) final String color,
			@RequestParam(value = "isPrivate", required = false) final String isPrivate,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		GenericResponse result = tagService.createTag(name, color, isPrivate, user);
		if (result.isSuccess()) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body(result);
		}
	}
}
