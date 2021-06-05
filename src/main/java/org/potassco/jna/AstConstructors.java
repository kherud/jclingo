package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/** {@link clingo_h#clingo_ast_constructors_t} */
public class AstConstructors extends Structure {
	/**
	 * Pointer to array of {@link AstConstructor}
	 */
	public Pointer constructors;
	public SizeT size;

	public AstConstructors(Pointer p) {
		super(p);
		read();
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("constructors", "size");
	}

}