package org.potassco.clingo.solving;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration for the different consequence types.
 */
public enum ConsequenceType {

	/**
	 * The literal is not a consequence
	 */
	FALSE(0),

	/**
	 * The literal is a consequence
	 */
	TRUE(1),

	/**
	 * The literal might or might not be a consequence
	 */
	UNKNOWN(2);

	private static final Map<Integer, ConsequenceType> mapping = new HashMap<>();

	static {
		for (ConsequenceType consequenceType : ConsequenceType.values()) {
			mapping.put(
					consequenceType.getValue(),
					consequenceType
			);
		}
	}

	public static ConsequenceType fromValue(int type) {
		return Objects.requireNonNull(mapping.get(type));
	}

	private final int code;

	ConsequenceType(int code) {
		this.code = code;
	}

	public int getValue() {
		return code;
	}
}
