package org.potassco.callback;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface SolveEventCallback extends Callback {
	public boolean callback(Pointer model, Pointer data, Pointer goon);
}
