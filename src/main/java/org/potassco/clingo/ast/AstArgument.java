package org.potassco.clingo.ast;

import com.sun.jna.Structure;
import org.potassco.clingo.args.Attribute;
import org.potassco.clingo.args.AttributeType;

/**
 * Struct to define an argument that consists of a name and a type.
 * @author Josef Schneeberger
 */
public class AstArgument extends Structure {
	  private Attribute attribute; // clingo_ast_attribute_t
	  private AttributeType type; // clingo_ast_attribute_type_t
}
