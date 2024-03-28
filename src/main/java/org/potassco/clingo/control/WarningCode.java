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

package org.potassco.clingo.control;

import java.util.HashMap;
import java.util.Map;

import org.potassco.clingo.internal.Clingo;

/**
 * Enumeration of warning codes.
 */
public enum WarningCode {
    /**
     * undefined arithmetic operation or weight of aggregate
     */
    OPERATION_UNDEFINED(0),
    /**
     * to report multiple errors; a corresponding runtime error is raised later
     */
    RUNTIME_ERROR(1),
    /**
     * undefined atom in program
     */
    ATOM_UNDEFINED(2),
    /**
     * same file included multiple times
     */
    FILE_INCLUDED(3),
    /**
     * CSP variable with unbounded domain
     */
    VARIABLE_UNBOUNDED(4),
    /**
     * global variable in tuple of aggregate element
     */
    GLOBAL_VARIABLE(5),
    /**
     * other kinds of warnings
     */
    OTHER(6);

    private final int code;

    WarningCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return Clingo.INSTANCE.clingo_warning_string(code);
    }

    private static final Map<Integer, WarningCode> mapping = new HashMap<>();

    static {
        for (WarningCode solveEventType : WarningCode.values()) {
            mapping.put(
                    solveEventType.getValue(),
                    solveEventType
            );
        }
    }

    public static WarningCode fromValue(int code) {
        return mapping.get(code);
    }

    public int getValue() {
        return code;
    }

}
