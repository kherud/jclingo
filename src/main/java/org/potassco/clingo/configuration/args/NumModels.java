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
 
package org.potassco.clingo.configuration.args;

public class NumModels implements Option {

    private final int amountModels;

    public NumModels(int amountModels) {
        this.amountModels = amountModels;
    }

    public static NumModels all() {
        return new NumModels(0);
    }

    public static NumModels one() {
        return new NumModels(1);
    }

    public static NumModels optimal() {
        return new NumModels(-1);
    }

    @Override
    public String getShellKey() {
        return "--models";
    }

    @Override
    public String getNativeKey() {
        return "solve.models";
    }

    @Override
    public String getValue() {
        return String.valueOf(amountModels);
    }

    @Override
    public Option getDefault() {
        return NumModels.optimal();
    }
}
