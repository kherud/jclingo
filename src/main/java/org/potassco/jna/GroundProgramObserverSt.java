package org.potassco.jna;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Structure;

/**
 * An instance of this struct has to be registered with a solver to observe ground directives as they are passed to the solver.
 *
 * @note This interface is closely modeled after the aspif format.
 * For more information please refer to the specification of the aspif format.
 *
 * Not all callbacks have to be implemented and can be set to NULL if not needed.
 * If one of the callbacks in the struct fails, grounding is stopped.
 * If a non-recoverable clingo API call fails, a callback must return false.
 * Otherwise ::clingo_error_unknown should be set and false returned.
 *
 * @see clingo_control_register_observer()
 * 
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ground_program_observer_t}
 */
public class GroundProgramObserverSt extends Structure {
	/**
	 * Called once in the beginning.
	 *
	 * If the incremental flag is true, there can be multiple calls to @ref clingo_control_solve().
	 *
	 * @param[in] incremental whether the program is incremental
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*init_program)(bool incremental, void *data);
	/**
	 * Marks the beginning of a block of directives passed to the solver.
	 *
	 * @see @ref end_step
	 *
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*begin_step)(void *data);
	/**
	 * Marks the end of a block of directives passed to the solver.
	 *
	 * This function is called before solving starts.
	 *
	 * @see @ref begin_step
	 *
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*end_step)(void *data);
	/**
	 * Observe rules passed to the solver.
	 *
	 * @param[in] choice determines if the head is a choice or a disjunction
	 * @param[in] head the head atoms
	 * @param[in] head_size the number of atoms in the head
	 * @param[in] body the body literals
	 * @param[in] body_size the number of literals in the body
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*rule)(bool choice, clingo_atom_t const *head, size_t head_size, clingo_literal_t const *body, size_t body_size, void *data);
	/**
	 * Observe weight rules passed to the solver.
	 *
	 * @param[in] choice determines if the head is a choice or a disjunction
	 * @param[in] head the head atoms
	 * @param[in] head_size the number of atoms in the head
	 * @param[in] lower_bound the lower bound of the weight rule
	 * @param[in] body the weighted body literals
	 * @param[in] body_size the number of weighted literals in the body
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*weight_rule)(bool choice, clingo_atom_t const *head, size_t head_size, clingo_weight_t lower_bound, clingo_weighted_literal_t const *body, size_t body_size, void *data);
	/**
	 * Observe minimize constraints (or weak constraints) passed to the solver.
	 *
	 * @param[in] priority the priority of the constraint
	 * @param[in] literals the weighted literals whose sum to minimize
	 * @param[in] size the number of weighted literals
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*minimize)(clingo_weight_t priority, clingo_weighted_literal_t const* literals, size_t size, void *data);
	/**
	 * Observe projection directives passed to the solver.
	 *
	 * @param[in] atoms the atoms to project on
	 * @param[in] size the number of atoms
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*project)(clingo_atom_t const *atoms, size_t size, void *data);
	/**
	 * Observe shown atoms passed to the solver.
	 * \note Facts do not have an associated aspif atom.
	 * The value of the atom is set to zero.
	 *
	 * @param[in] symbol the symbolic representation of the atom
	 * @param[in] atom the aspif atom (0 for facts)
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*output_atom)(clingo_symbol_t symbol, clingo_atom_t atom, void *data);
	/**
	 * Observe shown terms passed to the solver.
	 *
	 * @param[in] symbol the symbolic representation of the term
	 * @param[in] condition the literals of the condition
	 * @param[in] size the size of the condition
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*output_term)(clingo_symbol_t symbol, clingo_literal_t const *condition, size_t size, void *data);
	/**
	 * Observe shown csp variables passed to the solver.
	 *
	 * @param[in] symbol the symbolic representation of the variable
	 * @param[in] value the value of the variable
	 * @param[in] condition the literals of the condition
	 * @param[in] size the size of the condition
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*output_csp)(clingo_symbol_t symbol, int value, clingo_literal_t const *condition, size_t size, void *data);
	/**
	 * Observe external statements passed to the solver.
	 *
	 * @param[in] atom the external atom
	 * @param[in] type the type of the external statement
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*external)(clingo_atom_t atom, clingo_external_type_t type, void *data);
	/**
	 * Observe assumption directives passed to the solver.
	 *
	 * @param[in] literals the literals to assume (positive literals are true and negative literals false for the next solve call)
	 * @param[in] size the number of atoms
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*assume)(clingo_literal_t const *literals, size_t size, void *data);
	/**
	 * Observe heuristic directives passed to the solver.
	 *
	 * @param[in] atom the target atom
	 * @param[in] type the type of the heuristic modification
	 * @param[in] bias the heuristic bias
	 * @param[in] priority the heuristic priority
	 * @param[in] condition the condition under which to apply the heuristic modification
	 * @param[in] size the number of atoms in the condition
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*heuristic)(clingo_atom_t atom, clingo_heuristic_type_t type, int bias, unsigned priority, clingo_literal_t const *condition, size_t size, void *data);
	/**
	 * Observe edge directives passed to the solver.
	 *
	 * @param[in] node_u the start vertex of the edge
	 * @param[in] node_v the end vertex of the edge
	 * @param[in] condition the condition under which the edge is part of the graph
	 * @param[in] size the number of atoms in the condition
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*acyc_edge)(int node_u, int node_v, clingo_literal_t const *condition, size_t size, void *data);
	/**
	 * Observe numeric theory terms.
	 *
	 * @param[in] term_id the id of the term
	 * @param[in] number the value of the term
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*theory_term_number)(clingo_id_t term_id, int number, void *data);
	/**
	 * Observe string theory terms.
	 *
	 * @param[in] term_id the id of the term
	 * @param[in] name the value of the term
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*theory_term_string)(clingo_id_t term_id, char const *name, void *data);
	/**
	 * Observe compound theory terms.
	 *
	 * The name_id_or_type gives the type of the compound term:
	 * - if it is -1, then it is a tuple
	 * - if it is -2, then it is a set
	 * - if it is -3, then it is a list
	 * - otherwise, it is a function and name_id_or_type refers to the id of the name (in form of a string term)
	 *
	 * @param[in] term_id the id of the term
	 * @param[in] name_id_or_type the name or type of the term
	 * @param[in] arguments the arguments of the term
	 * @param[in] size the number of arguments
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*theory_term_compound)(clingo_id_t term_id, int name_id_or_type, clingo_id_t const *arguments, size_t size, void *data);
	/**
	 * Observe theory elements.
	 *
	 * @param element_id the id of the element
	 * @param terms the term tuple of the element
	 * @param terms_size the number of terms in the tuple
	 * @param condition the condition of the elemnt
	 * @param condition_size the number of literals in the condition
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*theory_element)(clingo_id_t element_id, clingo_id_t const *terms, size_t terms_size, clingo_literal_t const *condition, size_t condition_size, void *data);
	/**
	 * Observe theory atoms without guard.
	 *
	 * @param[in] atom_id_or_zero the id of the atom or zero for directives
	 * @param[in] term_id the term associated with the atom
	 * @param[in] elements the elements of the atom
	 * @param[in] size the number of elements
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
//	    bool (*theory_atom)(clingo_id_t atom_id_or_zero, clingo_id_t term_id, clingo_id_t const *elements, size_t size, void *data);
	/**
	 * Observe theory atoms with guard.
	 *
	 * @param[in] atom_id_or_zero the id of the atom or zero for directives
	 * @param[in] term_id the term associated with the atom
	 * @param[in] elements the elements of the atom
	 * @param[in] size the number of elements
	 * @param[in] operator_id the id of the operator (a string term)
	 * @param[in] right_hand_side_id the id of the term on the right hand side of the atom
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 */
	/*    bool (*theory_atom_with_guard)(clingo_id_t atom_id_or_zero, clingo_id_t term_id, clingo_id_t const *elements, size_t size, clingo_id_t operator_id, clingo_id_t right_hand_side_id, void *data);
	} clingo_ground_program_observer_t; */
}
