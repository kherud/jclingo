package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class AstConstructor extends Structure {
	public String name;
	public Pointer arguments;
	public Pointer size;

	public AstConstructor() {
		super();
	}

	public AstConstructor(Pointer p) {
		super(p);
		read();
	}

	public AstConstructor(Pointer[] pArray) {
		this.name = pArray[0].getString(0);
		this.arguments = pArray[1];
		this.size = pArray[2];
	}

	public AstConstructor(Pointer p1, Pointer p2, Pointer p3) {
		this.name = p1.getString(0);
		this.arguments = p2;
		this.size = p3;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("name", "arguments", "size");
	}

}