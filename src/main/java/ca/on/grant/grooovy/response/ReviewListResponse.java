package ca.on.grant.grooovy.response;

import java.util.List;

import ca.on.grant.grooovy.db.entity.Review;

public class ReviewListResponse {
	private double averageRating;
	private int totalReviews;
	private List<Review> reviews;
	private int fives;
	private int fours;
	private int threes;
	private int twos;
	private int ones;

	public ReviewListResponse(double averageRating, int totalReviews, List<Review> reviews, int fives, int fours, int threes, int twos,
			int ones) {
		this.averageRating = averageRating;
		this.totalReviews = totalReviews;
		this.reviews = reviews;
		this.fives = fives;
		this.fours = fours;
		this.threes = threes;
		this.twos = twos;
		this.ones = ones;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public int getFives() {
		return fives;
	}

	public void setFives(int fives) {
		this.fives = fives;
	}

	public int getFours() {
		return fours;
	}

	public void setFours(int fours) {
		this.fours = fours;
	}

	public int getThrees() {
		return threes;
	}

	public void setThrees(int threes) {
		this.threes = threes;
	}

	public int getTwos() {
		return twos;
	}

	public void setTwos(int twos) {
		this.twos = twos;
	}

	public int getOnes() {
		return ones;
	}

	public void setOnes(int ones) {
		this.ones = ones;
	}

	public int getTotalReviews() {
		return totalReviews;
	}

	public void setTotalReviews(int totalReviews) {
		this.totalReviews = totalReviews;
	}
}
