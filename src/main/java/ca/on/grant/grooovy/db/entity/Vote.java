package ca.on.grant.grooovy.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Vote {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	@Enumerated(EnumType.STRING)
	private Type type;
	@ManyToOne
	private User user;
	@ManyToOne
	private Review review;
	@Column
	private LocalDateTime createdDateTime;
	@Column
	private LocalDateTime deletedDateTime;
	
	public Vote() {}

	public Vote(long id, Type type, User user, Review review, LocalDateTime createdDateTime,
			LocalDateTime deletedDateTime) {
		this.id = id;
		this.type = type;
		this.user = user;
		this.review = review;
		this.createdDateTime = createdDateTime;
		this.deletedDateTime = deletedDateTime;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public enum Type {
		UP, DOWN;
	}

	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}


	public void setCreatedDateTime(LocalDateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}


	public LocalDateTime getDeletedDateTime() {
		return deletedDateTime;
	}


	public void setDeletedDateTime(LocalDateTime deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
}
