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

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback to customize model printing.
 */
@FunctionalInterface
public interface ModelPrinterCallback extends Callback {
    /**
     * Callback to print a model in default format.
     */
    @FunctionalInterface
    interface DefaultModelPrinter extends Callback {
        /**
         * @param data user data for the callback
         * @return whether the call was successful
         */
        byte callback(Pointer data);
    }

    /**
     * @param model the model
     * @param printer the default model printer
     * @param printerData user data for the printer
     * @param data user data for the callback
     * @return whether the call was successful
     */
    default byte callback(Pointer model, DefaultModelPrinter printer, Pointer printerData, Pointer data) {
        call(new Model(model));
        return 1;
    }

    void call(Model model);
}
