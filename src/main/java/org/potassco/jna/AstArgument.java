package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class AstArgument extends Structure {
	public int attribute;
	public int type;

	public AstArgument() {
		super();
	}

	public AstArgument(Pointer p) {
		super(p);
		read();
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("attribute", "type");
	}

}