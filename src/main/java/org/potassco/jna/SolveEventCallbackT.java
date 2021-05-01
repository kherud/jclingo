package org.potassco.jna;
import org.potassco.cpp.clingo_h;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

//! Callback function called during search to notify when the search is finished or a model is ready.
//!
//! If a (non-recoverable) clingo API function fails in this callback, it must return false.
//! In case of errors not related to clingo, set error code ::clingo_error_unknown and return false to stop solving with an error.
//!
//! The event is either a pointer to a model, a pointer to an int64_t* and a size_t, a pointer to two statistics objects (per step and accumulated statistics), or a solve result.
//! @attention If the search is finished, the model is NULL.
//!
//! @param[in] event the current event.
//! @param[in] data user data of the callback
//! @param[out] goon can be set to false to stop solving
//! @return whether the call was successful

/**
 * Callback function called during search to notify when the search is finished or a model is ready.
 * 
 * If a (non-recoverable) clingo API function fails in this callback, it must return false.
 * In case of errors not related to clingo, set error code ::clingo_error_unknown and return
 * false to stop solving with an error.
 * 
 * The event is either a pointer to a model, a pointer to an int64_t* and a size_t, a pointer
 * to two statistics objects (per step and accumulated statistics), or a solve result.
 * 
 * @attention If the search is finished, the model is NULL.
 * 
 * @see clingo_control_solve()
 * 
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_solve_event_callback_t}
 */
public abstract class SolveEventCallbackT implements Callback {
    public abstract boolean call(int type, Pointer event, Pointer data, Pointer goon);

    /**
     * @param type an SolveEventType as int
     * @param event the current event.
     * @param data user data of the callback
     * @param goon can be set to false to stop solving
     * @return
     */
    public boolean callback(int type, Pointer event, Pointer data, Pointer goon) {
        return call(type, event, data, goon);
    }
}