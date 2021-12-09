package org.potassco.clingo.solving;

/**
 * Enumeration of solve result types.
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

	public boolean satisfiable() {
		return (bitset & Type.SATISFIABLE.code) > 0;
	}

	public boolean unsatisfiable() {
		return (bitset & Type.UNSATISFIABLE.code) > 0;
	}

	public boolean exhausted() {
		return (bitset & Type.EXHAUSTED.code) > 0;
	}

	public boolean interrupted() {
		return (bitset & Type.INTERRUPTED.code) > 0;
	}

}
