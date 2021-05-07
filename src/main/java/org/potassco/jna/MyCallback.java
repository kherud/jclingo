package org.potassco.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface MyCallback extends Callback {
    boolean callback(int type, Pointer event, Pointer data, Pointer goon);
}
