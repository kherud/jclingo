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
 
package org.potassco.clingo.solving;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	@Override
	public String toString() {
		List<Type> types = new ArrayList<>();
		for (Type type : Type.values())
			if (isType(type))
				types.add(type);
		return types.stream().map(Type::name).collect(Collectors.joining(" "));
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
