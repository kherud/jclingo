package org.potassco.ast.structs;

import org.potassco.cpp.clingo_h;
import org.potassco.jna.Size;

import com.sun.jna.Structure;

/**
 * Struct to define an argument that consists of a name and a type.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_argument_t}
 */
public class Argument extends Structure {
	  private Attribute attribute; // clingo_ast_attribute_t 
	  private AttributeType type; // clingo_ast_attribute_type_t 
}
