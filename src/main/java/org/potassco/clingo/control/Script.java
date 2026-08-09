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
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.ast.Location;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.NativeArrays;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.internal.ffi.clingo_script;
import org.potassco.clingo.internal.ffi.clingo_symbol_callback_t;
import org.potassco.clingo.symbol.Symbol;

/**
 * Custom scripting language to run functions during grounding.
 * <p>
 * This class can be inherited to embed custom scripting languages into logic programs.
 */
// TODO: think of a better concept
public class Script {

    static {
        Clingo.init();
    }

    private final String version;

    public Script() {
        this("1.0.0");
    }

    public Script(String version) {
        try (Arena call = Arena.ofConfined()) {
            MemorySegment version$ = call.allocateFrom(version);
            // clingo keeps the version string for the lifetime of the process, so it has to be internalized
            this.version = Out.getString(out -> ClingoH.clingo_add_string(version$, out));
        }
    }

    /**
     * Add a custom scripting language to clingo. It stays registered for the lifetime of the process.
     *
     * @param name   the name of the scripting language
     * @param script struct with functions implementing the language
     */
    public static void register(String name, Script script) {
        Arena arena = Arena.global();
        try (Arena call = Arena.ofConfined()) {
            Clingo.check(ClingoH.clingo_register_script(call.allocateFrom(name), script.toNative(arena),
                    MemorySegment.NULL));
        }
    }

    /**
     * Get the version of the registered scripting language.
     *
     * @param name the name of the scripting language
     * @return the version
     */
    public static String getVersion(String name) {
        try (Arena call = Arena.ofConfined()) {
            return NativeStr.toJava(ClingoH.clingo_script_version(call.allocateFrom(name)));
        }
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
     * Call the function with the given name and arguments, injecting the result through {@code symbols}.
     *
     * @param location  the location in the logic program of the function call
     * @param name      the name of the function
     * @param arguments the arguments of the call
     * @param symbols   sink for the symbols the function evaluates to
     */
    public void call(Location location, String name, Symbol[] arguments, SymbolSink symbols) {

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
     * @return the version of this scripting language
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sink a {@link Script} injects the result of a function call into.
     */
    @FunctionalInterface
    public interface SymbolSink {
        void add(Symbol... symbols);
    }

    /**
     * @param arena owns the struct and its stubs; clingo uses them for the lifetime of the process
     */
    private MemorySegment toNative(Arena arena) {
        MemorySegment struct = clingo_script.allocate(arena);
        ErrorPolicy policy = ErrorPolicy.synchronous();

        clingo_script.execute(struct, Upcalls.stub(
                clingo_script.execute.Function.class,
                (location, code, data) -> {
                    execute(Location.fromNative(location), NativeStr.toJava(code));
                    return true;
                },
                clingo_script.execute.descriptor(), arena, policy));

        clingo_script.call(struct, Upcalls.stub(
                clingo_script.call.Function.class,
                (location, name, arguments, size, symbolCallback, symbolData, data) -> {
                    call(Location.fromNative(location), NativeStr.toJava(name), symbols(arguments, size),
                            values -> inject(symbolCallback, symbolData, values));
                    return true;
                },
                clingo_script.call.descriptor(), arena, policy));

        clingo_script.callable(struct, Upcalls.stub(
                clingo_script.callable.Function.class,
                (name, result, data) -> {
                    result.reinterpret(1).set(ValueLayout.JAVA_BOOLEAN, 0, callable(NativeStr.toJava(name)));
                    return true;
                },
                clingo_script.callable.descriptor(), arena, policy));

        clingo_script.main(struct, Upcalls.stub(
                clingo_script.main.Function.class,
                (control, data) -> {
                    main(new Control(control));
                    return true;
                },
                clingo_script.main.descriptor(), arena, policy));

        clingo_script.version(struct, arena.allocateFrom(version));
        return struct;
    }

    private static Symbol[] symbols(MemorySegment arguments, long size) {
        long[] values = NativeArrays.toLongs(arguments, size);
        Symbol[] symbols = new Symbol[values.length];
        for (int i = 0; i < values.length; i++) {
            symbols[i] = Symbol.fromLong(values[i]);
        }
        return symbols;
    }

    private static void inject(MemorySegment callback, MemorySegment data, Symbol[] symbols) {
        long[] values = new long[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            values[i] = symbols[i].getLong();
        }
        try (Arena call = Arena.ofConfined()) {
            Clingo.check(clingo_symbol_callback_t.invoke(callback,
                    call.allocateFrom(ValueLayout.JAVA_LONG, values), values.length, data));
        }
    }
}
