package org.potassco.ast.structs;

import org.potassco.cpp.clingo_h;
import com.sun.jna.Structure;

/**
 * A lists of required attributes to construct an AST.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_constructor_t}
 */
public class Constructor extends Structure {
	  private String name; // char const *name;
	  private Argument arguments; // clingo_ast_argument_t const *arguments;
	  private long size; // size_t size;
}
