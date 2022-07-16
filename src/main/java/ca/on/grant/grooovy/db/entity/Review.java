package ca.on.grant.grooovy.db.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private boolean isPrivate;
	@Column
	private LocalDateTime createdDateTime;
	@Column
	private int stars;
	@Column(length = 4000)
	private String text;
	@Column(length = 2048)
	private String url;
	@ManyToOne
	private User author;
	@ManyToMany
	private Set<Tag> tags;

	public Review(){
	}

	public Review(boolean isPrivate, LocalDateTime createdDateTime, int stars, String text, String url,
			User author, Set<Tag> tags) {
		this.isPrivate = isPrivate;
		this.createdDateTime = createdDateTime;
		this.stars = stars;
		this.text = text;
		this.url = url;
		this.author = author;
		this.tags = tags;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public LocalDateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(LocalDateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
}
