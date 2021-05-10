package org.potassco.jna;
import org.potassco.cpp.clingo_h;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

//! Callback to customize clingo main function.
//!
//! @param[in] control corresponding control object
//! @param[in] files files passed via command line arguments
//! @param[in] size number of files
//! @param[in] data user data for the callback
//!
//! @return whether the call was successful
public abstract class MainFunctionCallback implements Callback {
    public abstract boolean call(int type, Pointer event, Pointer data, Pointer goon);

    public boolean callback(int type, Pointer event, Pointer data, Pointer goon) {
        return call(type, event, data, goon);
    }
}