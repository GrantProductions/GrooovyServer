package ca.on.grant.grooovy.db.service;

import java.util.Arrays;

public class MessageVO {
	private final String key;
	private final Object[] arguments;

	public MessageVO(String key, Object... arguments) {
		super();
		this.key = key;
		this.arguments = arguments;
	}

	public String getKey() {
		return key;
	}

	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageVO [key=");
		builder.append(key);
		builder.append(", arguments=");
		builder.append(Arrays.toString(arguments));
		builder.append("]");
		return builder.toString();
	}
}

