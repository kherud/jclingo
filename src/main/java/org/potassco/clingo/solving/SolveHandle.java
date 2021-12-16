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
     * @return the current model
     */
    public Model getModel() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_solve_handle_model(solveHandle, pointerByReference));
        if (pointerByReference.getValue() == null)
            throw new NoSuchElementException();
        return new Model(pointerByReference.getValue());
    }

    @Override
    public boolean hasNext() {
        try {
            currentModel = getModel();
            resume();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public Model next() {
        return currentModel;
    }
}
