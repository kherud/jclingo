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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Handle for solve calls.
 * They can be used to control solving, like, retrieving models or cancelling a search.
 * <p>
 * A `SolveHandle` is an AutoCloseable and should be used with Java's `try with` statement.
 * Alternatively it must be manually closed.
 */
public class SolveHandle implements AutoCloseable, Iterator<Model> {

    private final Pointer solveHandle;
    private Model currentModel;

    private boolean continueIteration = true;

    public SolveHandle(Pointer solveHandle) {
        this.solveHandle = solveHandle;
    }

    /**
     * Cancel the running search.
     */
    public void cancel() {
        Clingo.check(Clingo.INSTANCE.clingo_solve_handle_cancel(solveHandle));
    }

    @Override
    public void close() {
        Clingo.check(Clingo.INSTANCE.clingo_solve_handle_close(solveHandle));
    }

    /**
     * Discards the last model and starts searching for the next one.
     */
    public void resume() {
        Clingo.check(Clingo.INSTANCE.clingo_solve_handle_resume(solveHandle));
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
        ByteByReference byteByReference = new ByteByReference();
        Clingo.INSTANCE.clingo_solve_handle_wait(solveHandle, timeout, byteByReference);
        return byteByReference.getValue() > 0;
    }

    /**
     * Get the result of a solve call.
     * If the search is not completed yet, the function blocks until the result is ready.
     *
     * @return the solve result
     */
    public SolveResult getSolveResult() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_solve_handle_get(solveHandle, intByReference));
        return new SolveResult(intByReference.getValue());
    }

    /**
     * When a problem is unsatisfiable, get a subset of the assumptions that made the problem unsatisfiable.
     * <p>
     * If the program is not unsatisfiable, core is set to NULL and size to zero.
     *
     * @return the array of solver literals
     */
    public int[] getCore() {
        PointerByReference pointerByReference = new PointerByReference();
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_solve_handle_core(solveHandle, pointerByReference, nativeSizeByReference));
        int coreSize = (int) nativeSizeByReference.getValue();
        return coreSize == 0 ? new int[coreSize] : pointerByReference.getValue().getIntArray(0, coreSize);
    }

    /**
     * Get the next model or null if there are no more models.
     *
     * @return the current model or null
     */
    public Model getModel() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_solve_handle_model(solveHandle, pointerByReference));
        if (pointerByReference.getValue() == null)
            return null;
        return new Model(pointerByReference.getValue());
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
}
