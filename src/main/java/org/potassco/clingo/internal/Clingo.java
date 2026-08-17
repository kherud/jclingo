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
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

import org.potassco.clingo.control.ErrorCode;
import org.potassco.clingo.internal.ffi.ClingoH;

/**
 * Entry point to the native library: loads it and turns its failure reports into exceptions.
 */
public final class Clingo {

    static {
        NativeLoader.ensureLoaded();
    }

    /**
     * The error accessors are declared without a prototype in clingo.h, which makes jextract treat them as variadic.
     * They are not, so they are bound here as the plain calls they are.
     */
    private static final MethodHandle ERROR_CODE = Linker.nativeLinker()
            .downcallHandle(ClingoH.clingo_error_code.address(), FunctionDescriptor.of(ValueLayout.JAVA_INT));
    private static final MethodHandle ERROR_MESSAGE = Linker.nativeLinker()
            .downcallHandle(ClingoH.clingo_error_message.address(), FunctionDescriptor.of(ValueLayout.ADDRESS));

    private Clingo() {
    }

    /**
     * @return the code of the last failed clingo call on this thread
     */
    public static ErrorCode getErrorCode() {
        try {
            return ErrorCode.fromValue((int) ERROR_CODE.invokeExact());
        }
        catch (Throwable e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @return the message of the last failed clingo call on this thread, or {@code null} if there is none
     */
    public static String getErrorMessage() {
        try {
            return NativeStr.toJava((MemorySegment) ERROR_MESSAGE.invokeExact());
        }
        catch (Throwable e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Loads the native library if that has not happened yet. Classes that can be the first thing a user touches call
     * this from their static initializer, so that the library is present before any native call is made.
     */
    public static void init() {
        // the static initializer above does the work
    }

    /**
     * @return the version of the loaded clingo library, e.g. {@code "5.8.2"}
     */
    public static String getVersion() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment major = arena.allocate(ValueLayout.JAVA_INT);
            MemorySegment minor = arena.allocate(ValueLayout.JAVA_INT);
            MemorySegment revision = arena.allocate(ValueLayout.JAVA_INT);
            ClingoH.clingo_version(major, minor, revision);
            return major.get(ValueLayout.JAVA_INT, 0) + "." + minor.get(ValueLayout.JAVA_INT, 0) + "."
                   + revision.get(ValueLayout.JAVA_INT, 0);
        }
    }

    /**
     * Turns the failure of a clingo call into an exception. Must be called on the thread that made the call, since
     * clingo keeps its error state per thread.
     *
     * @param success what the clingo function returned
     * @throws ClingoRuntimeException if clingo reported an error
     * @throws RuntimeException       if a callback of the failed call threw; the original exception is rethrown
     */
    public static void check(boolean success) {
        if (success) {
            return;
        }
        Throwable pending = Upcalls.takePending();
        if (pending instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        if (pending instanceof Error error) {
            throw error;
        }
        ErrorCode errorCode = getErrorCode();
        String message = getErrorMessage();
        if (message == null) {
            message = NativeStr.toJava(ClingoH.clingo_error_string(errorCode.getValue()));
        }
        ClingoRuntimeException failure = new ClingoRuntimeException(errorCode, message);
        if (pending != null) {
            failure.initCause(pending);
        }
        throw failure;
    }

    /**
     * Rethrows an error a callback reported from another thread, as the exception it originally was.
     */
    public static void rethrow(Throwable error) {
        if (error instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        if (error instanceof Error runtimeError) {
            throw runtimeError;
        }
        throw new ClingoRuntimeException(ErrorCode.UNKNOWN, error.getMessage() == null ? "callback failed"
                : error.getMessage(), error);
    }
}
