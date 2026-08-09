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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.internal.ffi.clingo_options_add$parse;

/**
 * Registry for the command line options of an {@link Application}.
 */
public class ApplicationOptions {

    private final MemorySegment options;

    /**
     * clingo keeps the parse callbacks and the addresses of the flags registered here until the run is over, so they
     * live in the arena of that run.
     */
    private final Arena arena;

    public ApplicationOptions(MemorySegment options, Arena arena) {
        this.options = options;
        this.arena = arena;
    }

    /**
     * Add an option that is processed with a custom parser.
     * <p>
     * Note that the parser also has to take care of storing the semantic value of
     * the option somewhere.
     * <p>
     * Parameter option specifies the name(s) of the option.
     * For example, "ping,p" adds the short option "-p" and its long form "--ping".
     * It is also possible to associate an option with a help level by adding ",@l" to the option specification.
     * Options with a level greater than zero are only shown if the argument to help is greater or equal to l.
     *
     * @param group       options are grouped into sections as given by this string
     * @param option      specifies the command line option
     * @param description the description of the option
     * @param callback    callback to parse the value of the option
     */
    public void addOption(String group, String option, String description, ParseCallback callback) {
        addOption(group, option, description, callback, false, null);
    }

    /**
     * Add an option that is processed with a custom parser.
     * <p>
     * Note that the parser also has to take care of storing the semantic value of
     * the option somewhere.
     * <p>
     * Parameter option specifies the name(s) of the option.
     * For example, "ping,p" adds the short option "-p" and its long form "--ping".
     * It is also possible to associate an option with a help level by adding ",@l" to the option specification.
     * Options with a level greater than zero are only shown if the argument to help is greater or equal to l.
     *
     * @param group       options are grouped into sections as given by this string
     * @param option      specifies the command line option
     * @param description the description of the option
     * @param callback    callback to parse the value of the option
     * @param multi       whether the option can appear multiple times on the command-line
     * @param argument    optional string to change the value name in the generated help output
     */
    public void addOption(String group, String option, String description, ParseCallback callback, boolean multi,
                          String argument) {
        clingo_options_add$parse.Function parse = (value, data) -> {
            callback.call(NativeStr.toJava(value));
            return true;
        };
        MemorySegment parse$ = Upcalls.stub(clingo_options_add$parse.Function.class, parse,
                clingo_options_add$parse.descriptor(), arena, ErrorPolicy.synchronous());
        try (Arena call = Arena.ofConfined()) {
            Clingo.check(ClingoH.clingo_options_add(options, call.allocateFrom(group), call.allocateFrom(option),
                    call.allocateFrom(description), parse$, MemorySegment.NULL, multi,
                    NativeStr.of(call, argument)));
        }
    }

    /**
     * Add an option that is a simple flag.
     * <p>
     * This function is similar to {@link ApplicationOptions#addOption} but simpler because it only supports flags, which do not have values.
     * If a flag is passed via the command-line the parameter target is set to true.
     *
     * @param group       options are grouped into sections as given by this string
     * @param option      specifies the command line option
     * @param description the description of the option
     * @param target      boolean set to true if the flag is given on the command-line
     */
    public void addFlag(String group, String option, String description, Flag target) {
        MemorySegment target$ = target.bind(arena);
        try (Arena call = Arena.ofConfined()) {
            Clingo.check(ClingoH.clingo_options_add_flag(options, call.allocateFrom(group), call.allocateFrom(option),
                    call.allocateFrom(description), target$));
        }
    }
}
