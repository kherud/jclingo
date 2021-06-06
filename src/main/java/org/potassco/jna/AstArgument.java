package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/** {@link clingo_h#clingo_ast_argument_t} */
public class AstArgument extends Structure {
    public static class ByReference extends AstArgument implements Structure.ByReference { }
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