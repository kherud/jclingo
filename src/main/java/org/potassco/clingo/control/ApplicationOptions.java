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
import org.potassco.clingo.internal.Clingo;

public class ApplicationOptions {

    private final Pointer options;

    public ApplicationOptions(Pointer pointer) {
        this.options = pointer;
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
        Clingo.check(Clingo.INSTANCE.clingo_options_add(
                options,
                group,
                option,
                description,
                callback,
                null,
                (byte) 0,
                null)
        );
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
    public void addOption(String group, String option, String description, ParseCallback callback, boolean multi, String argument) {
        Clingo.check(Clingo.INSTANCE.clingo_options_add(
                options,
                group,
                option,
                description,
                callback,
                null,
                multi ? (byte) 1 : 0,
                argument)
        );
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
        Clingo.check(Clingo.INSTANCE.clingo_options_add_flag(options, group, option, description, target.getFlag()));
    }
}
