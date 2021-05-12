package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

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
 * {@link clingo_h#clingo_part_t}
 */
public class PartSt extends Structure {
//	public static class ByReference extends PartSt implements Structure.ByReference {
//
//		public ByReference(String name, Pointer params, long size) {
//			super(name, params, size);
//		} }
	
	public String name;
	public long[] params;
	public long size;

	public PartSt(String name, long[] params, long size) {
		super();
		this.name = name;
		if (params == null) {
			this.params = new long[1];
		} else {
			this.params = params;
		}
		this.size = size;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("name", "params", "size");
	}

}
