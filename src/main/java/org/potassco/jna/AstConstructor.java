package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/** {@link clingo_h#clingo_ast_constructor_t} */
public class AstConstructor extends Structure {
	public String name;
	/**
	 * Pointer to {@link AstArgument}
	 */
	public Pointer arguments;
	public short size;

	public AstConstructor() {
		super();
	}

	public AstConstructor(Pointer p) {
		super(p);
		read();
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("name", "arguments", "size");
	}

}