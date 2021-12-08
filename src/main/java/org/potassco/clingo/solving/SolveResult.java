package org.potassco.clingo.solving;


import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of solve result types.
 * @note documented in ControlSt Module
 * @author Josef Schneeberger
 */
public class SolveResult {
	public enum Type {
		SATISFIABLE(1),
		UNSATISFIABLE(2),
		EXHAUSTED(4),
		INTERRUPTED(8);

		private final int code;

		Type(int code) {
			this.code = code;
		}

	}

    private final int bitset;

    public SolveResult(int bitset) {
        this.bitset = bitset;
    }

    public int getBitset() {
        return bitset;
    }

	public boolean isType(Type type) {
		return (bitset & type.code) > 0;
	}

}
