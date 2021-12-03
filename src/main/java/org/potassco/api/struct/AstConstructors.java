package org.potassco.api.struct;

import com.sun.jna.Structure;
import org.potassco.api.dtype.NativeSize;

/**
 * Struct to map AST types to lists of required attributes to construct ASTs.
 * @author Josef Schneeberger
 */
public class AstConstructors extends Structure {
	  private AstConstructor[] constructors;
	  private NativeSize size;
}
