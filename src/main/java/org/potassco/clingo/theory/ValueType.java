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

import java.util.HashMap;
import java.util.Map;

/**
 * Value types that can be returned by a theory.
 */
public enum ValueType {
	INT(0),
	DOUBLE(1),
	SYMBOL(2);

    private static final Map<Integer, ValueType> mapping = new HashMap<>();

	static {
	    for (ValueType solveEventType : ValueType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	private final int code;

    ValueType(int code) {
        this.code = code;
    }

	public static ValueType fromValue(int code) {
		return mapping.get(code);
	}

    public int getValue() {
        return code;
    }

}
