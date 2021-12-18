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

public enum OptMode implements Option {

    Optimal("opt", false),
    Enum("enum", true),
    OptimalN("optN", false),
    Ignore("ignore", false);

    private final String mode;
    private final boolean requiresBound;
    private String bound;

    OptMode(String mode, boolean requiresBound) {
        this.mode = mode;
        this.requiresBound = requiresBound;
    }

    public void setBound(String bound) {
        this.bound = bound;
    }

    @Override
    public String getShellKey() {
        return "--opt-mode";
    }

    @Override
    public String getNativeKey() {
        return "solve.opt_mode";
    }

    @Override
    public String getValue() {
        if (requiresBound && bound == null)
            throw new IllegalStateException("OptMode '" + mode + "' requires a bound");
        return this.mode;
    }

    @Override
    public Option getDefault() {
        return OptMode.Optimal;
    }

}
