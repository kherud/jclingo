package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class AstConstructors extends Structure {
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