package org.potassco.clingo.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enumeration for entries of the configuration.
 */
public class ConfigurationType {
	public enum Type {
		VALUE(1),
		ARRAY(2),
		MAP(4);

		private final int type;

		Type(int type) {
			this.type = type;
		}

		public int getInt() {
			return type;
		}
	}

	private final int bitset;

    ConfigurationType(int bitset) {
        this.bitset = bitset;
    }

	@Override
	public String toString() {
		List<Type> types = new ArrayList<>();
		for (Type type : Type.values())
			if (isType(type))
				types.add(type);
		return types.stream().map(Type::name).collect(Collectors.joining(" "));
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
