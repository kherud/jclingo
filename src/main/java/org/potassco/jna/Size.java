package org.potassco.jna;

import org.potassco.cpp.clingo_h;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

/**
 * Struct used to specify the program parts that have to be grounded.
 *
 * Programs may be structured into parts, which can be grounded independently with ::clingo_control_ground.
 * Program parts are mainly interesting for incremental grounding and multi-shot solving.
 * For single-shot solving, program parts are not needed.
 *
 * @note Parts of a logic program without an explicit <tt>\#program</tt>
 * specification are by default put into a program called `base` without
 * arguments.
 *
 * @see clingo_control_ground()
 *
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_size_t}
 */
public class Size extends IntegerType {
	private static final long serialVersionUID = 1L;

	public Size() {
		this(0);
	}

	public Size(long value) {
		super(Native.SIZE_T_SIZE, value, true);
	}
}