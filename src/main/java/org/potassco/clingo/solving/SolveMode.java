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

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of solve modes.
 */
public enum SolveMode {
	NONE(0),
    ASYNC(1), // non-blocking search
    YIELD(2), // yield models
	ASYNC_YIELD(3);

    private static final Map<Integer, SolveMode> mapping = new HashMap<>();

	static {
	    for (SolveMode solveEventType : SolveMode.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static SolveMode fromValue(int type) {
		return mapping.get(type);
	}

    private final int mode;

    SolveMode(int mode) {
        this.mode = mode;
    }

    public int getValue() {
        return mode;
    }

}
