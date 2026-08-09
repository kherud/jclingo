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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.potassco.clingo.internal.Callbacks;
import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.clingo_application;
import org.potassco.clingo.internal.ffi.clingo_main_function_t;
import org.potassco.clingo.internal.ffi.clingo_model_printer_t;
import org.potassco.clingo.solving.Model;

/**
 * Builds the {@code clingo_application_t} clingo expects from an {@link Application}.
 */
final class ApplicationBridge {

    private ApplicationBridge() {
    }

    /**
     * @param arena owns the struct, its stubs and the strings clingo keeps; it has to outlive {@code clingo_main}
     */
    static MemorySegment of(Application application, Arena arena) {
        MemorySegment struct = clingo_application.allocate(arena);
        // the application is asked for these while it is running, so the strings it returns live as long as the run
        ErrorPolicy policy = ErrorPolicy.synchronous();

        clingo_application.program_name(struct, Upcalls.stub(
                clingo_application.program_name.Function.class,
                data -> arena.allocateFrom(application.getName()),
                clingo_application.program_name.descriptor(), arena, policy));

        clingo_application.version(struct, Upcalls.stub(
                clingo_application.version.Function.class,
                data -> arena.allocateFrom(application.getVersion()),
                clingo_application.version.descriptor(), arena, policy));

        clingo_application.message_limit(struct, Upcalls.stub(
                clingo_application.message_limit.Function.class,
                data -> application.getMessageLimit(),
                clingo_application.message_limit.descriptor(), arena, policy));

        clingo_application.main(struct, Upcalls.stub(
                clingo_main_function_t.Function.class,
                (control, files, size, data) -> {
                    application.main(new Control(control), paths(files, size));
                    return true;
                },
                clingo_main_function_t.descriptor(), arena, policy));

        clingo_application.logger(struct, Callbacks.logger(application::log, arena, policy));

        clingo_application.printer(struct, Upcalls.stub(
                clingo_model_printer_t.Function.class,
                (model, printer, printerData, data) -> {
                    application.printModel(new Model(model));
                    return true;
                },
                clingo_model_printer_t.descriptor(), arena, policy));

        clingo_application.register_options(struct, Upcalls.stub(
                clingo_application.register_options.Function.class,
                (options, data) -> {
                    application.registerOptions(new ApplicationOptions(options, arena));
                    return true;
                },
                clingo_application.register_options.descriptor(), arena, policy));

        clingo_application.validate_options(struct, Upcalls.stub(
                clingo_application.validate_options.Function.class,
                data -> application.validateOptions(),
                clingo_application.validate_options.descriptor(), arena, policy));

        return struct;
    }

    /**
     * The file array is not zero terminated, so its explicit size is the only way to know where it ends.
     */
    private static Path[] paths(MemorySegment files, long size) {
        MemorySegment array = files.reinterpret(size * ValueLayout.ADDRESS.byteSize());
        Path[] paths = new Path[(int) size];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = Paths.get(NativeStr.toJava(array.getAtIndex(ValueLayout.ADDRESS, i)));
        }
        return paths;
    }
}
