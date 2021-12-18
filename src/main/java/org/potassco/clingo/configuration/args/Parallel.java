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

public class Parallel implements Option {

    private final int nThreads;
    private final Mode mode;

    public Parallel(int nThreads) {
        this(nThreads, Mode.Split);
    }

    public Parallel(int nThreads, Mode mode) {
        if (nThreads < 1 || nThreads > 64)
            throw new IllegalStateException("Amount of threads n used by clingo must be 0 < n <= 64");
        this.nThreads = nThreads;
        this.mode = mode;
    }

    public enum Mode {
        Compete("compete"),
        Split("split");

        private final String mode;

        Mode(String mode) {
            this.mode = mode;
        }

        @Override
        public String toString() {
            return mode;
        }
    }

    public static Parallel single() {
        return new Parallel(1);
    }

    public static Parallel two() {
        return new Parallel(2);
    }

    public static Parallel available() {
        int nCores = Runtime.getRuntime().availableProcessors();
        return new Parallel(nCores);
    }

    @Override
    public String getShellKey() {
        return "--parallel-mode";
    }

    @Override
    public String getNativeKey() {
        return "solve.parallel_mode";
    }

    @Override
    public String getValue() {
        return String.format("%d,%s", nThreads, mode.toString());
    }

    @Override
    public Option getDefault() {
        return Parallel.single();
    }
}
