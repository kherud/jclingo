/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
 
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
