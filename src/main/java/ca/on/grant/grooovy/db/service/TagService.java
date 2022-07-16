package ca.on.grant.grooovy.db.service;

import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.response.GenericResponse;

public interface TagService {
	public GenericResponse getTags(User user, String query);
	public GenericResponse createTag(String name, String color, String isPrivate, User user);
}
