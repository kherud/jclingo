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

package org.potassco.clingo.solving;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeResource;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

/**
 * Handle for solve calls.
 * They can be used to control solving, like, retrieving models or cancelling a search.
 * <p>
 * A <code>SolveHandle</code> is an AutoCloseable and should be used with Java's <code>try with</code> statement.
 * Alternatively it must be manually closed.
 */
public class SolveHandle extends NativeResource implements Iterator<Model> {

    /**
     * Errors of callbacks that ran on a solver thread; reporting them to clingo would have exited the process.
     */
    private final AtomicReference<Throwable> callbackError;

    private Model currentModel;

    private boolean continueIteration = true;

    public SolveHandle(MemorySegment solveHandle) {
        this(solveHandle, Arena.ofShared(), new AtomicReference<>());
    }

    /**
     * @param arena         holds the solve event callback for the duration of the search
     * @param callbackError where callbacks running on solver threads record their errors
     */
    public SolveHandle(MemorySegment solveHandle, Arena arena, AtomicReference<Throwable> callbackError) {
        super(solveHandle, arena);
        this.callbackError = callbackError;
    }

    /**
     * Cancel the running search.
     */
    public void cancel() {
        checkOpen();
        Clingo.check(ClingoH.clingo_solve_handle_cancel(handle));
    }

    /**
     * Stops the search and frees the native handle. Repeated calls have no effect.
     */
    @Override
    protected void free() {
        invalidateModel();
        Clingo.check(ClingoH.clingo_solve_handle_close(handle));
        rethrowCallbackError();
    }

    /**
     * Discards the last model and starts searching for the next one.
     */
    public void resume() {
        checkOpen();
        invalidateModel();
        Clingo.check(ClingoH.clingo_solve_handle_resume(handle));
        rethrowCallbackError();
    }

    /**
     * Poll whether a result is ready.
     * This function corresponds to {@link SolveHandle#wait(double)} with a time of 0.
     */
    public boolean poll() {
        return wait(0.);
    }

    /**
     * Wait for solve call to finish or the next result with an optional timeout.
     * <p>
     * If a timeout is given, the behavior of the function changes depending
     * on the sign of the timeout. If a postive timeout is given, the function
     * blocks for the given amount time or until a result is ready. If the
     * timeout is negative, the function will block until a result is ready,
     * which also corresponds to the behavior of the function if no timeout is
     * given. A timeout of zero can be used to poll if a result is ready.
     *
     * @param timeout If a timeout is given, the function blocks for at most timeout seconds.
     * @return Indicates whether the solve call has finished or the next result is ready.
     */
    public boolean wait(double timeout) {
        checkOpen();
        try (Arena call = Arena.ofConfined()) {
            MemorySegment result = call.allocate(ValueLayout.JAVA_BOOLEAN);
            ClingoH.clingo_solve_handle_wait(handle, timeout, result);
            return result.get(ValueLayout.JAVA_BOOLEAN, 0);
        }
    }

    /**
     * Get the result of a solve call.
     * If the search is not completed yet, the function blocks until the result is ready.
     *
     * @return the solve result
     */
    public SolveResult getSolveResult() {
        checkOpen();
        SolveResult result = new SolveResult(Out.getInt(out -> ClingoH.clingo_solve_handle_get(handle, out)));
        rethrowCallbackError();
        return result;
    }

    /**
     * When a problem is unsatisfiable, get a subset of the assumptions that made the problem unsatisfiable.
     * <p>
     * If the program is not unsatisfiable, core is set to NULL and size to zero.
     *
     * @return the array of solver literals
     */
    public int[] getCore() {
        checkOpen();
        return Out.getInts((core, size) -> ClingoH.clingo_solve_handle_core(handle, core, size));
    }

    /**
     * Get the next model or null if there are no more models.
     *
     * @return the current model or null
     */
    public Model getModel() {
        checkOpen();
        return model(Out.getPointer(out -> ClingoH.clingo_solve_handle_model(handle, out)));
    }

    /**
     * When a problem is satisfiable and the search is finished, get the last computed model. If the program is
     * unsatisfiable or the search is not finished, this method returns null.
     *
     * @return the last computed model or null
     */
    public Model getLast() {
        checkOpen();
        return model(Out.getPointer(out -> ClingoH.clingo_solve_handle_last(handle, out)));
    }

    @Override
    public boolean hasNext() {
        if (continueIteration) {
            resume();
            currentModel = getModel();
            continueIteration = false;
        }
        return currentModel != null;
    }

    @Override
    public Model next() {
        if (hasNext()) {
            continueIteration = true;
            return currentModel;
        }
        throw new NoSuchElementException();
    }

    private Model model(MemorySegment pointer) {
        if (pointer == null || pointer.equals(MemorySegment.NULL)) {
            return null;
        }
        currentModel = new Model(pointer);
        return currentModel;
    }

    /**
     * clingo reuses a model between search steps, so the java object handed out for the previous one must not be used
     * any more once the search moves on.
     */
    private void invalidateModel() {
        if (currentModel != null) {
            currentModel.invalidate();
            currentModel = null;
        }
    }

    /**
     * Propagates an exception a callback raised on a solver thread. clingo would exit the process if the failure of a
     * solve event were reported to it, so the error is carried over and surfaces here instead.
     */
    private void rethrowCallbackError() {
        Throwable error = callbackError.getAndSet(null);
        if (error != null) {
            Clingo.rethrow(error);
        }
    }
}
