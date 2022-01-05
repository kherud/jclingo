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

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback to intercept warning messages.
 */
@FunctionalInterface
public interface LoggerCallback extends Callback {
    /**
     * @param code associated warning code
     * @param message warning message
     * @param data user data for callback
     */
    default void callback(int code, String message, Pointer data) {
        call(WarningCode.fromValue(code), message);
    }

    /**
     * Callback to intercept warning messages.
     * @param code associated warning code
     * @param message warning message
     */
    void call(WarningCode code, String message);

}
