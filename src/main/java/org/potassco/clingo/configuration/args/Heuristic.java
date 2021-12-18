/*
 * Copyright (C) 2021 denkbares GmbH. All rights reserved.
 */

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

public enum Heuristic implements Option {
    Berkmin("Berkmin", false),
    Vmtf("Vmtf", true),
    Vsids("Vsids", true),
    Domain("Domain", false),
    Unit("Unit", false),
    None("None", false);

    private final String mode;
    private final boolean requiresFactor;
    private String factor;

    Heuristic(String mode, boolean requiresFactor) {
        this.mode = mode;
        this.requiresFactor = requiresFactor;
    }

    public void setFactor(int factor) {
        this.factor = String.valueOf(factor);
    }

    @Override
    public String getShellKey() {
        return "--heuristic";
    }

    @Override
    public String getNativeKey() {
        return "solver.heuristic";
    }

    @Override
    public String getValue() {
        if (requiresFactor && factor == null)
            throw new IllegalStateException("Heuristic '" + mode + "' requires a factor");
        return mode;
    }

    @Override
    public Option getDefault() {
        Heuristic heuristic = Heuristic.Vsids;
        heuristic.setFactor(92);
        return heuristic;
    }
}
