package org.potassco.jna;
import org.potassco.cpp.clingo_h;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

//! Callback to customize model printing.
//!
//! @param[in] model the model
//! @param[in] printer the default model printer
//! @param[in] printer_data user data for the printer
//! @param[in] data user data for the callback
//!
//! @return whether the call was successful
public abstract class ModelPrinterCallbackT implements Callback {
    public abstract boolean call(int type, Pointer event, Pointer data, Pointer goon);

    public boolean callback(int type, Pointer event, Pointer data, Pointer goon) {
        return call(type, event, data, goon);
    }
}