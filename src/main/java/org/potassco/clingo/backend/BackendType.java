package org.potassco.clingo.backend;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The available backends.
 */
public enum BackendType {
	/**
	 * The reify backend
	 */
	REIFY(0),
	/**
	 * Whether to reify sccs
	 */
	REIFY_SCCS(1),
	/**
	 * Whether to reify steps individually
	 */
	REIFY_STEPS(2),
	/**
	 * The aspif backend
	 */
	ASPIF(4),
	/**
	 * The smodels backend
	 */
	SMODELS(5);

	private static final Map<Integer, BackendType> mapping = new HashMap<>();

	static {
		for (BackendType solveEventType : BackendType.values()) {
			mapping.put(
					solveEventType.getValue(),
					solveEventType
			);
		}
	}

	public static BackendType fromValue(int type) {
		return Objects.requireNonNull(mapping.get(type));
	}

	private final int type;

	BackendType(int type) {
		this.type = type;
	}

	public int getValue() {
		return type;
	}

}
