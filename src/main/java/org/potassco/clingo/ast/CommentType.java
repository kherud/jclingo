package org.potassco.clingo.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration of comment types
 */
public enum CommentType {

	LINE,
	BLOCK;

	private static final Map<Integer, CommentType> mapping = new HashMap<>();

	static {
		for (CommentType type : CommentType.values()) {
			mapping.put(
					type.ordinal(),
					type
			);
		}
	}

	public static CommentType fromOrdinal(int type) {
		return Objects.requireNonNull(mapping.get(type));
	}
}
