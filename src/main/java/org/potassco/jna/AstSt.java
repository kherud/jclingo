package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class AstSt extends Structure {
	public Pointer pointer;

	public AstSt(String name, Pointer pointer, long size) {
		super();
		this.pointer = pointer;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("pointer");
	}

}
