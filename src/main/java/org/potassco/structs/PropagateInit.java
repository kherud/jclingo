package org.potassco.structs;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Structure;

/**
 * Object to initialize a user-defined propagator before each solving step.
 *
 * Each @link SymbolicAtoms symbolic@endlink or @link TheoryAtoms theory atom@endlink is uniquely associated with an aspif atom in form of a positive integer (@ref ::clingo_literal_t).
 * Aspif literals additionally are signed to represent default negation.
 * Furthermore, there are non-zero integer solver literals (also represented using @ref ::clingo_literal_t).
 * There is a surjective mapping from program atoms to solver literals.
 *
 * All methods called during propagation use solver literals whereas clingo_symbolic_atoms_literal() and clingo_theory_atoms_atom_literal() return program literals.
 * The function clingo_propagate_init_solver_literal() can be used to map program literals or @link clingo_theory_atoms_element_condition_id() condition ids@endlink to solver literals.

 * @author Josef Schneeberger
 * {@link clingo_h#clingo_propagate_init_t}
 */
public class PropagateInit extends Structure {

}
