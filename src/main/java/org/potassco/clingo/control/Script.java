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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;

/**
 * Custom scripting language to run functions during grounding.
 * <p>
 * This class can be inherited to embed custom scripting languages into logic programs.
 */
// TODO: think of a better concept
public class Script extends Structure {

    /**
     * Add a custom scripting language to clingo.
     *
     * @param name   the name of the scripting language
     * @param script struct with functions implementing the language
     */
    static void register(String name, Script script) {
        Clingo.check(Clingo.INSTANCE.clingo_register_script(name, script, null));
    }

    /**
     * Get the version of the registered scripting language.
     *
     * @param name the name of the scripting language
     * @return the version
     */
    static String getVersion(String name) {
        return Clingo.INSTANCE.clingo_script_version(name);
    }

    /**
     * Evaluate the given source code.
     *
     * @param location the location in the logic program of the source code
     * @param code     the code to evaluate
     */
    public void execute(Location location, String code) {

    }

    /**
     * Call the function with the given name and arguments.
     *
     * @param location       the location in the logic program of the function call
     * @param name           the name of the function
     * @param symbolCallback callback to return a pool of symbols
     */
    public void call(Location location, String name, long[] symbols, Clingo.SymbolCallback symbolCallback) {

    }

    /**
     * Check if the given function is callable.
     *
     * @param name the name of the function
     * @return whether the function is callable
     */
    public boolean callable(String name) {
        return false;
    }

    /**
     * Run the main function.
     *
     * @param control the control object to pass to the main function
     */
    public void main(Control control) {

    }

    /**
     * This function is called once when the script is deleted.
     *
     * @param data data user data as given when registering the script
     * @return native pointer what to free
     */
    public Pointer free(Pointer data) {
        return null;
    }

    public Script() {
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_add_string("1.0.0", stringByReference));
        this.version = stringByReference[0];
    }

    public Script(String version) {
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_add_string(version, stringByReference));
        this.version = stringByReference[0];
    }

    /**
     * JNA DECLARATIONS IGNORE
     */

    public ExecuteCallback execute = this::execute;
    public CallCallback call = this::call;
    public CallableCallback callable = this::callable;
    public MainCallback main = this::main;
    public FreeCallback free = this::free;
    public String version;

    private interface ExecuteCallback extends Callback {
        /**
         * Evaluate the given source code.
         *
         * @param location the location in the logic program of the source code
         * @param code     the code to evaluate
         * @param data     user data as given when registering the script
         * @return whether the function call was successful
         */
        default boolean callback(Pointer location, String code, Pointer data) {
            call(new Location(location), code);
            return true;
        }

        void call(Location location, String code);
    }

    private interface CallCallback extends Callback {
        /**
         * Call the function with the given name and arguments.
         *
         * @param location           the location in the logic program of the function call
         * @param name               the name of the function
         * @param arguments          the arguments to the function
         * @param argumentsSize      the number of arguments
         * @param symbolCallback     callback to return a pool of symbols
         * @param symbolCallbackData user data for the symbol callback
         * @param data               user data as given when registering the script
         * @return whether the function call was successful
         */
        default boolean callback(Pointer location, String name, long[] arguments, NativeSize argumentsSize, Clingo.SymbolCallback symbolCallback, Pointer symbolCallbackData, Pointer data) {
            call(new Location(location), name, arguments, symbolCallback);
            return true;
        }

        void call(Location location, String name, long[] symbols, Clingo.SymbolCallback symbolCallback);
    }

    private interface CallableCallback extends Callback {
        /**
         * Check if the given function is callable.
         *
         * @param name   the name of the function
         * @param result whether the function is callable
         * @param data   user data as given when registering the script
         * @return whether the function call was successful
         */
        default boolean callback(String name, ByteByReference result, Pointer data) {
            boolean callable = call(name);
            result.setValue((byte) (callable ? 1 : 0));
            return true;
        }

        boolean call(String name);
    }

    private interface MainCallback extends Callback {
        /**
         * Run the main function.
         *
         * @param control the control object to pass to the main function
         * @param data    user data as given when registering the script
         * @return whether the function call was successful
         */
        default boolean callback(Pointer control, Pointer data) {
            call(new Control(control));
            return true;
        }

        void call(Control control);
    }

    // TODO: this does not seem to be a good solution
    private interface FreeCallback extends Callback {
        /**
         * This function is called once when the script is deleted.
         *
         * @param data data user data as given when registering the script
         * @return native pointer what to free
         */
        default Pointer callback(Pointer data) {
            return null;
        }

        Pointer call(Pointer data);
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("execute", "call", "callable", "main", "free", "version");
    }
}
