package ca.on.grant.grooovy.db.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.on.grant.grooovy.db.entity.Tag;
import ca.on.grant.grooovy.db.entity.User;
import ca.on.grant.grooovy.db.repository.TagRepository;
import ca.on.grant.grooovy.response.GenericResponse;
import ca.on.grant.grooovy.response.TagResponse;

@Service
public class TagServiceImpl implements TagService {
	private static final Logger LOG = LoggerFactory.getLogger(TagServiceImpl.class);
	private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");

	@Autowired
	private TagRepository tagRepository;

	public GenericResponse getTags(User user, String query) {
		if (query == null || query.trim().length() == 0) {
			return new GenericResponse(false, "Please specify a query", null);
		}
		List<Tag> tags = tagRepository.findAll();
		List<TagResponse> toReturn = new ArrayList<>();
		for (Tag t : tags) {
			final User owner = t.getOwner();
			if (owner == null || owner.getId() == user.getId()) {
				if (t.getName().contains(query)) {
					toReturn.add(new TagResponse(t.getName(), t.getColor(), owner != null));
				}
			}
		}
		return new GenericResponse(true, null, toReturn);
	}

	public GenericResponse createTag(String name, String color, String isPrivate, User user) {
		if (isPrivate == null) {
			return new GenericResponse(false, "Please specify privacy", null);
		}
		isPrivate = isPrivate.trim();
		if (!isBoolean(isPrivate)) {
			return new GenericResponse(false, "Invalid isPrivate value", null);
		}

		if (name == null) {
			return new GenericResponse(false, "Please specify a tag name", null);
		}
		name = name.trim();
		if (name.length() == 0) {
			return new GenericResponse(false, "Please specify a tag name", null);
		}

		if (color == null) {
			return new GenericResponse(false, "Please specify a color", null);
		}
		color = color.trim();
		
		if(color.length() == 0) {
			return new GenericResponse(false, "Please specify a color", null);
		}
		
		if(color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (!isHexadecimal(color)) {
			return new GenericResponse(false, "Invalid color. Only HEX is accepted", null);
		}

		final boolean parsedIsPrivate = Boolean.parseBoolean(isPrivate);

		List<Tag> tags = tagRepository.findAll();
		for (Tag t : tags) {
			LOG.info("Tag [{}]", t.getName());
			final User owner = t.getOwner();
			if (t.getName().equals(name)) {
				if ((owner == null && !parsedIsPrivate)
						|| owner != null && t.getOwner().getId() == user.getId() && parsedIsPrivate) {
					return new GenericResponse(false, "Tag already exists", null);
				}
			}
		}

		Tag newTag = new Tag(color, name, parsedIsPrivate ? user : null);
		tagRepository.save(newTag);
		return new GenericResponse(true, null, null);
	}

	private boolean isBoolean(String bool) {
		try {
			Boolean.parseBoolean(bool);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isHexadecimal(String input) {
		final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
		return matcher.matches();
	}

}
