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
 * Enumeration of theory sequence types.
 */
public enum TheorySequenceType {
    /**
     * Theory tuples "(t1,...,tn)".
     */
    TUPLE(0),
    /**
     * Theory lists "[t1,...,tn]".
     */
    LIST(1),
    /**
     * Theory sets "{t1,...,tn}".
     */
    SET(2);

    private static final Map<Integer, TheorySequenceType> mapping = new HashMap<>();

    static {
        for (TheorySequenceType solveEventType : TheorySequenceType.values()) {
            mapping.put(
                    solveEventType.getValue(),
                    solveEventType
            );
        }
    }

    public static TheorySequenceType fromValue(int type) {
        return mapping.get(type);
    }

    private final int type;

    TheorySequenceType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
