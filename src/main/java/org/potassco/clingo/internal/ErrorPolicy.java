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

package org.potassco.clingo.internal;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.potassco.clingo.control.ErrorCode;
import org.potassco.clingo.internal.ffi.ClingoH;

/**
 * What an upcall stub does when its body throws.
 *
 * @see Upcalls#stub
 */
public final class ErrorPolicy {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final Consumer<Throwable> sink;
    private final boolean solveEvent;

    private ErrorPolicy(Consumer<Throwable> sink, boolean solveEvent) {
        this.sink = sink;
        this.solveEvent = solveEvent;
    }

    /**
     * For callbacks running synchronously inside our own downcall: the error is remembered for the calling thread
     * and rethrown unchanged by the {@link Clingo#check} of that downcall.
     */
    public static ErrorPolicy synchronous() {
        return new ErrorPolicy(Upcalls::setPending, false);
    }

    /**
     * For callbacks that may run on a solver thread: the error is handed to {@code slot}, whoever consumes it.
     * Only the first error is kept, since later ones are usually consequences of it.
     */
    public static ErrorPolicy crossThread(AtomicReference<Throwable> slot) {
        return new ErrorPolicy(error -> slot.compareAndSet(null, error), false);
    }

    /**
     * Like {@link #crossThread}, but for the solve event callback, which must never report failure to clingo -
     * that would abort the process. It stops the search instead and reports success.
     */
    public static ErrorPolicy solveEvent(AtomicReference<Throwable> slot) {
        return new ErrorPolicy(error -> slot.compareAndSet(null, error), true);
    }

    /**
     * @return a handle of type {@code (Throwable, ...)R} matching the leading arguments of {@code descriptor}
     */
    MethodHandle handlerFor(FunctionDescriptor descriptor) {
        return MethodHandles.insertArguments(find(handlerType(descriptor)), 1, sink);
    }

    private MethodType handlerType(FunctionDescriptor descriptor) {
        if (solveEvent) {
            return MethodType.methodType(boolean.class, Throwable.class, Consumer.class, int.class,
                    MemorySegment.class, MemorySegment.class, MemorySegment.class);
        }
        return MethodType.methodType(descriptor.toMethodType().returnType(), Throwable.class, Consumer.class);
    }

    private static MethodHandle find(MethodType type) {
        try {
            return LOOKUP.findStatic(ErrorPolicy.class, nameOf(type), type);
        }
        catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static String nameOf(MethodType type) {
        if (type.parameterCount() > 2) {
            return "failSolveEvent";
        }
        Class<?> returnType = type.returnType();
        if (returnType == void.class) {
            return "failVoid";
        }
        if (returnType == boolean.class) {
            return "failBoolean";
        }
        if (returnType == int.class) {
            return "failInt";
        }
        if (returnType == MemorySegment.class) {
            return "failPointer";
        }
        throw new IllegalArgumentException("unsupported callback return type " + returnType);
    }

    @SuppressWarnings("unused") // referenced by method handle
    private static void failVoid(Throwable error, Consumer<Throwable> sink) {
        report(error, sink);
    }

    @SuppressWarnings("unused") // referenced by method handle
    private static boolean failBoolean(Throwable error, Consumer<Throwable> sink) {
        report(error, sink);
        return false;
    }

    @SuppressWarnings("unused") // referenced by method handle
    private static int failInt(Throwable error, Consumer<Throwable> sink) {
        report(error, sink);
        return 1;
    }

    @SuppressWarnings("unused") // referenced by method handle
    private static MemorySegment failPointer(Throwable error, Consumer<Throwable> sink) {
        report(error, sink);
        return MemorySegment.NULL;
    }

    /**
     * Reporting failure of a solve event to clingo aborts the process, so the search is stopped instead.
     */
    @SuppressWarnings("unused") // referenced by method handle
    private static boolean failSolveEvent(Throwable error, Consumer<Throwable> sink, int type, MemorySegment event,
                                          MemorySegment data, MemorySegment goon) {
        sink.accept(error);
        goon.reinterpret(1).set(ValueLayout.JAVA_BOOLEAN, 0, false);
        return true;
    }

    /**
     * Keeps clingo's own error state consistent with what actually happened; the code is cosmetic, the exception
     * itself travels through {@code sink}.
     */
    private static void report(Throwable error, Consumer<Throwable> sink) {
        sink.accept(error);
        try (Arena arena = Arena.ofConfined()) {
            // clingo copies the message, so it does not have to outlive this call
            ClingoH.clingo_set_error(codeOf(error).getValue(), arena.allocateFrom(describe(error)));
        }
    }

    private static ErrorCode codeOf(Throwable error) {
        if (error instanceof OutOfMemoryError) {
            return ErrorCode.BAD_ALLOC;
        }
        if (error instanceof RuntimeException) {
            return ErrorCode.RUNTIME;
        }
        return ErrorCode.UNKNOWN;
    }

    private static String describe(Throwable error) {
        String message = error.getMessage();
        return message == null ? error.getClass().getName() : error.getClass().getName() + ": " + message;
    }
}
