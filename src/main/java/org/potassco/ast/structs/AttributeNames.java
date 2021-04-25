package org.potassco.ast.structs;

import org.potassco.cpp.clingo_h;
import org.potassco.jna.SizeT;

import com.sun.jna.Structure;

/**
 * Struct to map attributes to their string representation.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_argument_t}
 */
public class AttributeNames extends Structure {
//	  char const * const * names;
	  private SizeT size;
}
