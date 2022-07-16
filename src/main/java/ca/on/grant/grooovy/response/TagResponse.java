package ca.on.grant.grooovy.response;

public class TagResponse {
	private long id;
	private String name;
	private String color;
	private boolean isPrivate;

	public TagResponse(long id, String name, String color, boolean isPrivate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.isPrivate = isPrivate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
