package org.potassco.jna;

import org.potassco.cpp.clingo_h;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

/**
 * Represents a symbol.
 * 
 * This includes numbers, strings, functions (including constants when
 * arguments are empty and tuples when the name is empty), <tt>\#inf</tt> and <tt>\#sup</tt>.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_symbol_t}
 */
public class Symbol extends IntegerType {
	private static final long serialVersionUID = 1L;

	public Symbol() {
		this(0);
	}

	public Symbol(long value) {
		super(Native.SIZE_T_SIZE, value, true);
	}
}