package org.potassco.api.callback;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import org.potassco.api.dtype.NativeSize;
import org.potassco.api.enums.SolveEventType;

/**
 * Callback function called during search to notify when the search is finished or a model is ready.
 *
 * If a (non-recoverable) clingo API function fails in this callback, it must return false.
 * In case of errors not related to clingo, set error code ::clingo_error_unknown and return false to stop solving with an error.
 *
 * The event is either a pointer to a model, a pointer to an int64_t* and a size_t, a pointer to two statistics objects (per step and accumulated statistics), or a solve result.
 * If the search is finished, the model is NULL.
 */
public abstract class SolveEventCallback implements Callback {
    /**
     * @param event the current event.
     * @param data user data of the callback
     * @param goon can be set to false to stop solving
     * @return whether the call was successful
     */
    public boolean callback(SolveEventType type, Pointer event, Pointer data, ByteByReference goon) {
        return call(type, event, data, goon);
    }

    public abstract boolean call(SolveEventType type, Pointer event, Pointer data, ByteByReference goon);
}
