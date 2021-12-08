package org.potassco.clingo.control;

/**
 * Enumeration of theory term types.
 * @author Josef Schneeberger
 */
public class ConfigurationType {
	public enum Type {
		VALUE(1),
		/** the entry is an array */
		ARRAY(2),
		/** the entry is a map */
		MAP(4);

		private final int type;

		Type(int type) {
			this.type = type;
		}
	}

	private final int bitset;

    ConfigurationType(int bitset) {
        this.bitset = bitset;
    }

	public boolean isType(Type type) {
		return (this.bitset & type.type) > 0;
	}

	public static boolean isType(int bitset, Type type) {
		return (bitset & type.type) > 0;
	}

    public int getBitset() {
        return bitset;
    }
}
