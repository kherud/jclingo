package org.potassco.api.struct;

import com.sun.jna.Structure;
import org.potassco.api.dtype.NativeSize;

/**
 * A lists of required attributes to construct an AST.
 * @author Josef Schneeberger
 */
public class AstConstructor extends Structure {
	  private String name;
	  private AstArgument arguments;
	  private NativeSize size;
}
