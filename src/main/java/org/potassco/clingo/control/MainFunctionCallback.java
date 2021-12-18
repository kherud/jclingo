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

import com.sun.jna.Pointer;
import org.potassco.clingo.internal.NativeSize;

import javax.security.auth.callback.Callback;

/**
 * Callback to customize clingo main function.
 */
public interface MainFunctionCallback extends Callback {
    /**
     * @param control corresponding control object
     * @param files files passed via command line arguments
     * @param size number of files
     * @param data user data for the callback
     * @return whether the call was successful
     */
    // TODO: is String[] correct here?
    default boolean callback(Pointer control, String[] files, NativeSize size, Pointer data) {
        call(control, files);
        return true;
    }

    void call(Pointer control, String[] files);

}
