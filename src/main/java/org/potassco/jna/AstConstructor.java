package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class AstConstructor extends Structure {
    public static class ByReference extends AstConstructor implements Structure.ByReference { }
	public String name;
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