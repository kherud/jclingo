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

// parse callback to parse the value of the option
public interface ParseCallback extends Callback {
    /**
     * @param value the value of the option
     * @param data callback data
     * @return whether the call was successful
     */
    default boolean callback(String value, Pointer data) {
        call(value);
        return true;
    }

    /**
     * parse callback to parse the value of the option
     * @param value the value of the option
     */
    void call(String value);

}
