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

package org.potassco.clingo.theory;

import java.util.Objects;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.potassco.clingo.symbol.Symbol;

@Structure.FieldOrder({"type", "value"})
public class Value extends Structure {

	// all fields annotated with SuppressWarnings are required to be public by JNA
	@SuppressWarnings("WeakerAccess")
	public int type;
	public ValueUnion value;

	public Value(int type, ValueUnion value) {
		super();
		this.type = type;
		this.value = value;
	}

	@SuppressWarnings("WeakerAccess")
	public Value() {
		super();
	}

	Value(Pointer peer) {
		super(peer);
	}

	@Override
	public String toString() {
		switch (type) {
			case 0:
				return String.valueOf(value.int_number);
			case 1:
				return String.valueOf(value.double_number);
			case 2:
				return Symbol.fromLong(value.symbol).toString();
			default:
				throw new IllegalStateException("Unknown value type");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Value value1 = (Value) o;
		return type == value1.type && value.equals(value1.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	public ValueType getType() {
		return ValueType.fromValue(type);
	}

	public ValueUnion getValue() {
		return value;
	}

	static class ByReference extends Value implements Structure.ByReference { }

	static class ByValue extends Value implements Structure.ByValue { }

}
