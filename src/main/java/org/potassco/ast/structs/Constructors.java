package org.potassco.ast.structs;

import org.potassco.cpp.clingo_h;
import org.potassco.jna.Size;

import com.sun.jna.Structure;

/**
 * Struct to map AST types to lists of required attributes to construct ASTs.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_constructors_t}
 */
//! 
/* typedef struct clingo_ast_constructors {
  
  size_t size;
} clingo_ast_constructors_t; */
public class Constructors extends Structure {
	  private Constructor[] constructors; // clingo_ast_constructor_t const *constructors;
	  private Size size; // size_t size;
}
