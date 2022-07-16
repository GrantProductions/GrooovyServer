package ca.on.grant.grooovy.util;

import java.util.Set;

public class ReviewVO {
	private long id;
	private boolean isPrivate;
	private long createdDateTime;
	private String formattedDateTime;
	private int stars;
	private String text;
	private String url;
	private UserVO author;
	private Set<TagVO> tags;

	public ReviewVO(long id, boolean isPrivate, long createdDateTime, String formattedDateTime, int stars, String text, String url,
			UserVO author, Set<TagVO> tags) {
		this.id = id;
		this.isPrivate = isPrivate;
		this.createdDateTime = createdDateTime;
		this.formattedDateTime = formattedDateTime;
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

	public long getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(long createdDateTime) {
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

	public UserVO getAuthor() {
		return author;
	}

	public void setAuthor(UserVO author) {
		this.author = author;
	}

	public Set<TagVO> getTags() {
		return tags;
	}

	public void setTags(Set<TagVO> tags) {
		this.tags = tags;
	}

	public String getFormattedDateTime() {
		return formattedDateTime;
	}

	public void setFormattedDateTime(String formattedDateTime) {
		this.formattedDateTime = formattedDateTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
