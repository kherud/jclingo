package org.potassco.jna;
import org.potassco.cpp.c_enum;
import org.potassco.cpp.clingo_h;
import org.potassco.cpp.struct;
import org.potassco.cpp.typedef;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * The central interface to clingo.h
 * <p>
 * The file should keep the same structure and order
 * as clingo.h for better orientation. clingo_h.java
 * is a line by line copy of clingo.h in order to detect 
 * changes in new versions of clingo.h. This interface
 * keeps the same order as clingo.h but defines the actual
 * interface methods to access shared objects or dynamic link
 * libraries.
 * <p>
 * We keep javadoc links to clingo_h.c for every single function
 * interface in order to access clingo.h details.
 * 
 * @author Josef Schneeberger
 *
 */
public interface ClingoLibrary extends Library {
    ClingoLibrary INSTANCE = Native.load("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll", ClingoLibrary.class);

    //! Signed integer type used for aspif and solver literals.
  	// typedef<int32_t> clingo_literal_t = null;
    //! Unsigned integer type used for aspif atoms.
    // typedef<uint32_t> clingo_atom_t = null;
    //! Unsigned integer type used in various places.
    // typedef<uint32_t> clingo_id_t = null;
    //! Signed integer type for weights in sum aggregates and minimize constraints.
    // typedef<int32_t> clingo_weight_t = null;
    //! A LiteralSt with an associated weight.
    // typedef<struct> clingo_weighted_literal_t
    
    //! Enumeration of error codes.
    // typedef<c_enum> clingo_error_e = null;
    // typedef<c_int> clingo_error_t = null;

    /** {@link clingo_h#clingo_error_string} */
    public String clingo_error_string(int code);

    /** {@link clingo_h#clingo_error_code} */
    public int clingo_error_code();

    /** {@link clingo_h#clingo_error_message} */
    public String clingo_error_message();

    /** {@link clingo_h#clingo_set_error} */
    public void clingo_set_error(int code, String message);

    /** {@link clingo_h#clingo_warning_string} */
    public String clingo_warning_string(int code);

    /** {@link clingo_h#clingo_version} */
    void clingo_version(IntByReference major, IntByReference minor, IntByReference patch);
    
    // clingo_truth_value_e
    // clingo_truth_value_t
    
    // Signature Functions

    /** {@link clingo_h#clingo_signature_create} */
    public byte clingo_signature_create(String p_name, int arity, int positive, LongByReference p_signature);

    /** {@link clingo_h#clingo_signature_name} */
    public String clingo_signature_name(long signature); // CLINGO_VISIBILITY_DEFAULT char const *clingo_signature_name(clingo_signature_t signature);

    /** {@link clingo_h#clingo_signature_arity} */
    public int clingo_signature_arity(long signature); // CLINGO_VISIBILITY_DEFAULT uint32_t clingo_signature_arity(clingo_signature_t signature);

    /** {@link clingo_h#clingo_signature_is_positive} */
    public byte clingo_signature_is_positive(long signature); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_positive(clingo_signature_t signature);

    /** {@link clingo_h#clingo_signature_is_negative} */
    public byte clingo_signature_is_negative(long signature); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_negative(clingo_signature_t signature);

    /** {@link clingo_h#clingo_signature_is_equal_to} */
    public byte clingo_signature_is_equal_to(long signature_a, long signature_b); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_equal_to(clingo_signature_t a, clingo_signature_t b);

    /** {@link clingo_h#clingo_signature_is_less_than} */
    public byte clingo_signature_is_less_than(long signature_a, long signature_b); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_less_than(clingo_signature_t a, clingo_signature_t b);

    /** {@link clingo_h#clingo_signature_hash} */
    public SizeT clingo_signature_hash(long signature); // CLINGO_VISIBILITY_DEFAULT size_t clingo_signature_hash(clingo_signature_t signature);

    /** {@link clingo_h#clingo_symbol_create_number} */
    public void clingo_symbol_create_number(int number, LongByReference p_symbol);

    /** {@link clingo_h#clingo_symbol_create_supremum} */
    public void clingo_symbol_create_supremum(LongByReference p_symbol);

    /** {@link clingo_h#clingo_symbol_create_infimum} */
    public void clingo_symbol_create_infimum(LongByReference p_symbol);

    /** {@link clingo_h#clingo_symbol_create_string} */
    public byte clingo_symbol_create_string(String p_string, LongByReference p_symbol);

    /** {@link clingo_h#clingo_symbol_create_id} */
    public byte clingo_symbol_create_id(String p_name, byte positive, LongByReference p_symbol);

    /** {@link clingo_h#clingo_symbol_create_function} */
    public byte clingo_symbol_create_function(String p_name, long[] p_arguments, SizeT arguments_size, byte positive, LongByReference p_symbol);

    // Symbol Inspection Functions

    /** {@link clingo_h#clingo_symbol_number} */
    public byte clingo_symbol_number(long symbol, IntByReference p_number);

    /** {@link clingo_h#clingo_symbol_name} */
    public byte clingo_symbol_name(long symbol, String[] p_p_name);

    /** {@link clingo_h#clingo_symbol_string} */
    public byte clingo_symbol_string(long symbol, String[] p_p_string);
    
    /** {@link clingo_h#clingo_symbol_is_positive} */
    public byte clingo_symbol_is_positive(long symbol, ByteByReference p_positive);

    /** {@link clingo_h#clingo_symbol_is_negative} */
    public byte clingo_symbol_is_negative(long symbol, ByteByReference p_negative);

    /** {@link clingo_h#clingo_symbol_arguments} */
    public byte clingo_symbol_arguments(long symbol, PointerByReference p_p_arguments, SizeByReference p_arguments_size);

    /** {@link clingo_h#clingo_symbol_type} */
    public int clingo_symbol_type(long symbol);

    /** {@link clingo_h#clingo_symbol_to_string_size} */
    public byte clingo_symbol_to_string_size(long symbol, SizeByReference p_size);

    /** {@link clingo_h#clingo_symbol_to_string} */
    public byte clingo_symbol_to_string(long symbol, Memory p_string, SizeT size);
    
    // Symbol Comparison Functions

    /** {@link clingo_h#clingo_symbol_is_equal_to} */
    public byte clingo_symbol_is_equal_to(long a, long b);

    /** {@link clingo_h#clingo_symbol_is_less_than} */
    public byte clingo_symbol_is_less_than(long a, long b); // CLINGO_VISIBILITY_DEFAULT bool clingo_symbol_is_less_than(clingo_symbol_t a, clingo_symbol_t b);

    /** {@link clingo_h#clingo_symbol_hash} */
    public SizeT clingo_symbol_hash(long symbol); // CLINGO_VISIBILITY_DEFAULT size_t clingo_symbol_hash(clingo_symbol_t symbol);

    /** {@link clingo_h#clingo_add_string} */
    public byte clingo_add_string(String p_string, String[] p_p_result);

    /** {@link clingo_h#clingo_parse_term} */
    public byte clingo_parse_term(String p_string, Pointer logger, PointerByReference p_logger_data, int message_limit, LongByReference p_symbol);

    // symbolic atoms

    /** {@link clingo_h#clingo_symbolic_atoms_size} */
    public byte clingo_symbolic_atoms_size(Pointer p_atoms, SizeByReference p_size);

    /** {@link clingo_h#clingo_symbolic_atoms_begin} */
    public byte clingo_symbolic_atoms_begin(Pointer p_atoms, LongByReference p_signature, LongByReference p_iterator);

    /** {@link clingo_h#clingo_symbolic_atoms_end} */
    public byte clingo_symbolic_atoms_end(Pointer p_atoms, LongByReference p_iterator);

    /** {@link clingo_h#clingo_symbolic_atoms_find} */
    public byte clingo_symbolic_atoms_find(Pointer p_atoms, long symbol, LongByReference p_iterator);

    /** {@link clingo_h#clingo_symbolic_atoms_iterator_is_equal_to} */
    public byte clingo_symbolic_atoms_iterator_is_equal_to(Pointer p_atoms, long iterator_a, long iterator_b, ByteByReference p_equal);

    /** {@link clingo_h#clingo_symbolic_atoms_symbol} */
    public byte clingo_symbolic_atoms_symbol(Pointer p_atoms, long iterator, LongByReference p_symbol);

    /** {@link clingo_h#clingo_symbolic_atoms_is_fact} */
    public byte clingo_symbolic_atoms_is_fact(Pointer p_atoms, long iterator, ByteByReference p_fact);

    /** {@link clingo_h#clingo_symbolic_atoms_is_external} */
    public byte clingo_symbolic_atoms_is_external(Pointer p_atoms, long iterator, ByteByReference p_external);

    /** {@link clingo_h#clingo_symbolic_atoms_literal} */
    public byte clingo_symbolic_atoms_literal(Pointer p_atoms, long iterator, IntByReference p_literal);

    /** {@link clingo_h#clingo_symbolic_atoms_signatures_size} */
    public byte clingo_symbolic_atoms_signatures_size(Pointer p_atoms, SizeByReference p_size);

    /** {@link clingo_h#clingo_symbolic_atoms_signatures} */
    public byte clingo_symbolic_atoms_signatures(Pointer p_atoms, long[] p_signatures, SizeT size);

    /** {@link clingo_h#clingo_symbolic_atoms_next} */
    public byte clingo_symbolic_atoms_next(Pointer p_atoms, long iterator, LongByReference p_next);

    /** {@link clingo_h#clingo_symbolic_atoms_is_valid} */
    public byte clingo_symbolic_atoms_is_valid(Pointer p_atoms, long iterator, ByteByReference p_valid);

    // theory atoms

    // Theory Term Inspection

    /** {@link clingo_h#clingo_theory_atoms_term_type} */
    public byte clingo_theory_atoms_term_type(Pointer p_atoms, int term, IntByReference p_type);

    /** {@link clingo_h#clingo_theory_atoms_term_number} */
    public byte clingo_theory_atoms_term_number(Pointer p_atoms, int term, IntByReference p_number);

    /** {@link clingo_h#clingo_theory_atoms_term_name} */
    public byte clingo_theory_atoms_term_name(Pointer p_atoms, int term, final String[] p_p_name);

    /** {@link clingo_h#clingo_theory_atoms_term_arguments} */
    public byte clingo_theory_atoms_term_arguments(Pointer p_atoms, int term, PointerByReference p_p_arguments, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_term_to_string_size} */
    public byte clingo_theory_atoms_term_to_string_size(Pointer p_atoms, int term, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_term_to_string} */
    public byte clingo_theory_atoms_term_to_string(Pointer p_atoms, int term, Memory p_string, SizeT size);

    // Theory Element Inspection

    /** {@link clingo_h#clingo_theory_atoms_element_tuple} */
    public byte clingo_theory_atoms_element_tuple(Pointer p_atoms, int element, PointerByReference p_p_tuple, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_element_condition} */
    public byte clingo_theory_atoms_element_condition(Pointer p_atoms, int element, PointerByReference p_p_condition, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_element_condition_id} */
    public byte clingo_theory_atoms_element_condition_id(Pointer p_atoms, int element, IntByReference p_condition);

    /** {@link clingo_h#clingo_theory_atoms_element_to_string_size} */
    public byte clingo_theory_atoms_element_to_string_size(Pointer p_atoms, int element, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_element_to_string} */
    public byte clingo_theory_atoms_element_to_string(Pointer p_atoms, int element, Memory p_string, SizeT size);

    // Theory Atom Inspection

    /** {@link clingo_h#clingo_theory_atoms_size} */
    public byte clingo_theory_atoms_size(Pointer p_atoms, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_atom_term} */
    public byte clingo_theory_atoms_atom_term(Pointer p_atoms, int atom, IntByReference p_term);

    /** {@link clingo_h#clingo_theory_atoms_atom_elements} */
    public byte clingo_theory_atoms_atom_elements(Pointer p_atoms, int atom, PointerByReference p_p_elements, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_atom_has_guard} */
    public byte clingo_theory_atoms_atom_has_guard(Pointer p_atoms, int atom, ByteByReference p_has_guard);

    /** {@link clingo_h#clingo_theory_atoms_atom_guard} */
    public byte clingo_theory_atoms_atom_guard(Pointer p_atoms, int atom, PointerByReference p_p_connective, IntByReference p_term);

    /** {@link clingo_h#clingo_theory_atoms_atom_literal} */
    public byte clingo_theory_atoms_atom_literal(Pointer p_atoms, int atom, IntByReference p_literal);

    /** {@link clingo_h#clingo_theory_atoms_atom_to_string_size} */
    public byte clingo_theory_atoms_atom_to_string_size(Pointer p_atoms, int atom, SizeByReference p_size);

    /** {@link clingo_h#clingo_theory_atoms_atom_to_string} */
    public byte clingo_theory_atoms_atom_to_string(Pointer p_atoms, int atom, Memory p_string, SizeT size);

    // propagator
    
    // public static final typedef<struct> clingo_assignment_t = null;
    
    // AssignmentSt Functions

    /** {@link clingo_h#clingo_assignment_decision_level} */
    public int clingo_assignment_decision_level(Pointer assignment);

    /** {@link clingo_h#clingo_assignment_root_level} */
    public int clingo_assignment_root_level(Pointer assignment);

    /** {@link clingo_h#clingo_assignment_has_conflict} */
    public byte clingo_assignment_has_conflict(Pointer assignment);

    /** {@link clingo_h#clingo_assignment_has_literal} */
    public byte clingo_assignment_has_literal(Pointer assignment, int literal);

    /** {@link clingo_h#clingo_assignment_level} */
    public byte clingo_assignment_level(Pointer assignment, int literal, IntByReference p_level);
    
    /** {@link clingo_h#clingo_assignment_decision} */
    public byte clingo_assignment_decision(Pointer assignment, int level, IntByReference p_literal);

    /** {@link clingo_h#clingo_assignment_is_fixed} */
    public byte clingo_assignment_is_fixed(Pointer assignment, int literal, ByteByReference is_fixed);

    /** {@link clingo_h#clingo_assignment_is_true} */
	public byte clingo_assignment_is_true(Pointer assignment, int literal, ByteByReference is_true);

    /** {@link clingo_h#clingo_assignment_is_false} */
	public byte clingo_assignment_is_false(Pointer assignment, int literal, ByteByReference is_false);

    /** {@link clingo_h#clingo_assignment_truth_value} */
	public byte clingo_assignment_truth_value(Pointer assignment, int literal, IntByReference value);

    /** {@link clingo_h#clingo_assignment_size} */
	public SizeT clingo_assignment_size(Pointer assignment);

    /** {@link clingo_h#clingo_assignment_at} */
	public byte clingo_assignment_at(Pointer assignment, SizeT offset, IntByReference literal);

    /** {@link clingo_h#clingo_assignment_is_total} */
	public byte clingo_assignment_is_total(Pointer assignment);

    /** {@link clingo_h#clingo_assignment_trail_size} */
	public byte clingo_assignment_trail_size(Pointer assignment, SizeByReference size);

    /** {@link clingo_h#clingo_assignment_trail_begin} */
	public byte clingo_assignment_trail_begin(Pointer assignment, int level, IntByReference offset);

    /** {@link clingo_h#clingo_assignment_trail_end} */
	public byte clingo_assignment_trail_end(Pointer assignment, int level, IntByReference offset);

    /** {@link clingo_h#clingo_assignment_trail_at} */
	public byte clingo_assignment_trail_at(Pointer assignment, int offset, IntByReference literal);
    
    // clingo_propagator_check_mode_e
	// clingo_propagator_check_mode_t
    // clingo_weight_constraint_type_e
	// clingo_weight_constraint_type_t
    // clingo_propagate_init_t
    
    // Initialization Functions

    /** {@link clingo_h#clingo_propagate_init_solver_literal} */
	public byte clingo_propagate_init_solver_literal(Pointer propagate_init, int aspif_literal, IntByReference p_solver_literal);

    /** {@link clingo_h#clingo_propagate_init_add_watch} */
	public byte clingo_propagate_init_add_watch(Pointer propagate_init, int solver_literal);

    /** {@link clingo_h#clingo_propagate_init_add_watch_to_thread} */
	public byte clingo_propagate_init_add_watch_to_thread(Pointer propagate_init, int solver_literal, int thread_id);

    /** {@link clingo_h#clingo_propagate_init_symbolic_atoms} */
	public byte clingo_propagate_init_symbolic_atoms(Pointer propagate_init, PointerByReference p_p_atoms);

    /** {@link clingo_h#clingo_propagate_init_theory_atoms} */
	public byte clingo_propagate_init_theory_atoms(Pointer propagate_init, PointerByReference p_p_atoms);

    /** {@link clingo_h#clingo_propagate_init_number_of_threads} */
	public int clingo_propagate_init_number_of_threads(Pointer propagate_init);

    /** {@link clingo_h#clingo_propagate_init_set_check_mode} */
	public void clingo_propagate_init_set_check_mode(Pointer propagate_init, int mode);

    /** {@link clingo_h#clingo_propagate_init_get_check_mode} */
	public int clingo_propagate_init_get_check_mode(Pointer propagate_init);

    /** {@link clingo_h#clingo_propagate_init_assignment} */
	public Pointer clingo_propagate_init_assignment(Pointer propagate_init);

    /** {@link clingo_h#clingo_propagate_init_add_literal} */
	public byte clingo_propagate_init_add_literal(Pointer propagate_init, byte freeze, IntByReference p_result);

    /** {@link clingo_h#clingo_propagate_init_add_clause} */
	public byte clingo_propagate_init_add_clause(Pointer propagate_init, int[] p_clause, SizeT size, ByteByReference p_result);

    /** {@link clingo_h#clingo_propagate_init_add_weight_constraint} */
	public byte clingo_propagate_init_add_weight_constraint(Pointer propagate_init, int literal, Pointer p_literals, SizeT size, int bound, int type, byte compare_equal, ByteByReference p_result);

    /** {@link clingo_h#clingo_propagate_init_add_minimize} */
	public byte clingo_propagate_init_add_minimize(Pointer propagate_init, int literal, int weight, int priority);

    /** {@link clingo_h#clingo_propagate_init_propagate} */
	public byte clingo_propagate_init_propagate(Pointer propagate_init, ByteByReference p_result);
    
    // clingo_clause_type_e
    // clingo_clause_type_t
	// clingo_propagate_control_t
    
    // Propagation Functions

    /** {@link clingo_h#clingo_propagate_control_thread_id} */
	public int clingo_propagate_control_thread_id(Pointer p_control);

    /** {@link clingo_h#clingo_propagate_control_assignment} */
	public Pointer clingo_propagate_control_assignment(Pointer p_control);

    /** {@link clingo_h#clingo_propagate_control_add_literal} */
	public byte clingo_propagate_control_add_literal(Pointer p_control, IntByReference p_result);

    /** {@link clingo_h#clingo_propagate_control_add_watch} */
	public byte clingo_propagate_control_add_watch(Pointer p_control, int literal);

    /** {@link clingo_h#clingo_propagate_control_has_watch} */
	public byte clingo_propagate_control_has_watch(Pointer p_control, int literal);

    /** {@link clingo_h#clingo_propagate_control_remove_watch} */
	public void clingo_propagate_control_remove_watch(Pointer p_control, int literal);

    /** {@link clingo_h#clingo_propagate_control_add_clause} */
	public byte clingo_propagate_control_add_clause(Pointer p_control, IntByReference p_clause, SizeT size, int type, ByteByReference p_result);

    /** {@link clingo_h#clingo_propagate_control_propagate} */
	public byte clingo_propagate_control_propagate(Pointer p_control, ByteByReference p_result);

    /** {@link clingo_h#clingo_propagator_init_callback_t} */
    // clingo_propagator_init_callback_t
    // clingo_propagator_propagate_callback_t    
    // clingo_propagator_undo_callback_t    
    // clingo_propagator_check_callback_t    
    // clingo_propagate_init_t

    //! Can be used to propagate solver literals given a @link clingo_assignment_t partial assignment@endlink.
    //!
    //! Called during propagation with a non-empty array of @link clingo_propagate_init_add_watch() watched solver literals@endlink
    //! that have been assigned to true since the last call to either propagate, undo, (or the start of the search) - the change set.
    //! Only watched solver literals are contained in the change set.
    //! Each literal in the change set is true w.r.t. the current @link clingo_assignment_t assignment@endlink.
    //! @ref clingo_propagate_control_add_clause() can be used to add clauses.
    //! If a clause is unit resulting, it can be propagated using @ref clingo_propagate_control_propagate().
    //! If the result of either of the two methods is false, the propagate function must return immediately.
    //!
    //! The following snippet shows how to use the methods to add clauses and propagate consequences within the callback.
    //! The important point is to return true (true to indicate there was no error) if the result of either of the methods is false.
    //! ~~~~~~~~~~~~~~~{.c}
    //! bool result;
    //! clingo_literal_t clause[] = { ... };
    //!
    //! // add a clause
    //! if (!clingo_propagate_control_add_clause(control, clause, clingo_clause_type_learnt, &result) { return false; }
    //! if (!result) { return true; }
    //! // propagate its consequences
    //! if (!clingo_propagate_control_propagate(control, &result) { return false; }
    //! if (!result) { return true; }
    //!
    //! // add further clauses and propagate them
    //! ...
    //!
    //! return true;
    //! ~~~~~~~~~~~~~~~
    //!
    //! @note
    //! This function can be called from different solving threads.
    //! Each thread has its own assignment and id, which can be obtained using @ref clingo_propagate_control_thread_id().
    //!
    //! @param[in] control control object for the target solver
    //! @param[in] changes the change set
    //! @param[in] size the size of the change set
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
    //! @see ::clingo_propagator_propagate_callback_t
// bool (*propagate) (clingo_propagate_control_t *control, clingo_literal_t const *changes, size_t size, void *data);
    //! Called whenever a solver undoes assignments to watched solver literals.
    //!
    //! This callback is meant to update assignment dependent state in the propagator.
    //!
    //! @note No clauses must be propagated in this callback and no errors should be set.
    //!
    //! @param[in] control control object for the target solver
    //! @param[in] changes the change set
    //! @param[in] size the size of the change set
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
    //! @see ::clingo_propagator_undo_callback_t
// void (*undo) (clingo_propagate_control_t const *control, clingo_literal_t const *changes, size_t size, void *data);
    //! This function is similar to @ref clingo_propagate_control_propagate() but is called without a change set on propagation fixpoints.
    //!
    //! When exactly this function is called, can be configured using the @ref clingo_propagate_init_set_check_mode() function.
    //!
    //! @note This function is called even if no watches have been added.
    //!
    //! @param[in] control control object for the target solver
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
    //! @see ::clingo_propagator_check_callback_t
// bool (*check) (clingo_propagate_control_t *control, void *data);
    //! This function allows a propagator to implement domain-specific heuristics.
    //!
    //! It is called whenever propagation reaches a fixed point and
    //! should return a free solver literal that is to be assigned true.
    //! In case multiple propagators are registered,
    //! this function can return 0 to let a propagator registered later make a decision.
    //! If all propagators return 0, then the fallback literal is
    //!
    //! @param[in] thread_id the solver's thread id
    //! @param[in] assignment the assignment of the solver
    //! @param[in] fallback the literal choosen by the solver's heuristic
    //! @param[out] decision the literal to make true
    //! @return whether the call was successful
    /** {@link clingo_h#clingo_propagator} */
// bool (*decide) (clingo_id_t thread_id, clingo_assignment_t const *assignment, clingo_literal_t fallback, void *data, clingo_literal_t *decision);
    // typedef<struct> clingo_propagator = null;
    
    // backend
    
    // clingo_heuristic_type_e
  	// clingo_heuristic_type_t
  	// clingo_external_type_e
    // clingo_external_type_t
  	// clingo_backend_t
    
	/** {@link clingo_h#clingo_backend_begin} */
  	public byte clingo_backend_begin(Pointer p_backend);

	/** {@link clingo_h#clingo_backend_end} */
  	public byte clingo_backend_end(Pointer p_backend);

	/** {@link clingo_h#clingo_backend_rule} */
  	public byte clingo_backend_rule(Pointer p_backend, byte choice, IntByReference p_head, SizeT head_size, IntByReference p_body, SizeT body_size);

	/** {@link clingo_h#clingo_backend_weight_rule} */
  	public byte clingo_backend_weight_rule(Pointer p_backend, byte choice, IntByReference p_head, SizeT head_size, int lower_bound, IntByReference p_body, SizeT body_size);

	/** {@link clingo_h#clingo_backend_minimize} */
  	public byte clingo_backend_minimize(Pointer p_backend, int priority, int[] p_literals, SizeT size);

	/** {@link clingo_h#clingo_backend_project} */
  	public byte clingo_backend_project(Pointer p_backend, int[] p_atoms, SizeT size);

	/** {@link clingo_h#clingo_backend_external} */
  	public byte clingo_backend_external(Pointer p_backend, int atom, int type);

	/** {@link clingo_h#clingo_backend_assume} */
  	public byte clingo_backend_assume(Pointer p_backend, int[] p_literals, SizeT size);

	/** {@link clingo_h#clingo_backend_heuristic} */
  	public byte clingo_backend_heuristic(Pointer p_backend, int atom, int type, int bias, int priority, IntByReference p_condition, SizeT size);

	/** {@link clingo_h#clingo_backend_acyc_edge} */
  	public byte clingo_backend_acyc_edge(Pointer p_backend, int node_u, int node_v, IntByReference p_condition, SizeT size);

  	/** {@link clingo_h#clingo_backend_add_atom} */
	public byte clingo_backend_add_atom(Pointer p_backend, IntByReference p_symbol, IntByReference p_atom);

    // ConfigurationSt

    /** {@link clingo_h#clingo_configuration_root} */
    public byte clingo_configuration_root(Pointer p_configuration, IntByReference p_key);

    /** {@link clingo_h#clingo_configuration_type} */
    public byte clingo_configuration_type(Pointer p_configuration, int key, IntByReference p_type);

    /** {@link clingo_h#clingo_configuration_description} */
    public byte clingo_configuration_description(Pointer p_configuration, int key, String[] p_p_description);
    
    // Functions to access arrays
    
    /** {@link clingo_h#clingo_configuration_description} */
    public byte clingo_configuration_array_size(Pointer p_configuration, int key, SizeByReference p_size);

    /** {@link clingo_h#clingo_configuration_array_at} */
    public byte clingo_configuration_array_at(Pointer p_configuration, int key, SizeT offset, IntByReference p_subkey);
    
    // Functions to access maps
    
    /** {@link clingo_h#clingo_configuration_map_size} */
    public byte clingo_configuration_map_size(Pointer p_configuration, int key, SizeByReference p_size);

    /** {@link clingo_h#clingo_configuration_map_has_subkey} */
    public byte clingo_configuration_map_has_subkey(Pointer p_configuration, int key, String p_name, ByteByReference p_result);

    /** {@link clingo_h#clingo_configuration_map_subkey_name} */
    public byte clingo_configuration_map_subkey_name(Pointer p_configuration, int key, SizeT offset, String[] p_p_name);
    
    /** {@link clingo_h#clingo_configuration_map_at} */
    public byte clingo_configuration_map_at(Pointer p_configuration, int key, final String p_name, IntByReference p_subkey);
    
    // Functions to access values

    /** {@link clingo_h#clingo_configuration_value_is_assigned} */
    public byte clingo_configuration_value_is_assigned(Pointer p_configuration, int key, ByteByReference p_assigned);

    /** {@link clingo_h#clingo_configuration_value_get_size} */
    public byte clingo_configuration_value_get_size(Pointer p_configuration, int key, SizeByReference p_size);

    /** {@link clingo_h#clingo_configuration_value_get} */
    public byte clingo_configuration_value_get(Pointer p_configuration, int key, Memory p_value, SizeT size);

    /** {@link clingo_h#clingo_configuration_value_set} */
    public byte clingo_configuration_value_set(Pointer p_configuration, int key, String p_value);
    
    // statistics

    /** {@link clingo_h#clingo_statistics_root} */
    public byte clingo_statistics_root(Pointer statistics, LongByReference p_key);

    /** {@link clingo_h#clingo_statistics_type} */
    public byte clingo_statistics_type(Pointer statistics, long key, IntByReference type);
    
    // Functions to access arrays

    /** {@link clingo_h#clingo_statistics_array_size} */
    public byte clingo_statistics_array_size(Pointer statistics, long key, SizeByReference p_size);

    /** {@link clingo_h#clingo_statistics_array_at} */
    public byte clingo_statistics_array_at(Pointer statistics, long key, SizeT offset, LongByReference p_subkey);

    /** {@link clingo_h#clingo_statistics_array_push} */
    public byte clingo_statistics_array_push(Pointer p_statistics, long key, int type, LongByReference p_subkey);
    
    // Functions to access maps

    /** {@link clingo_h#clingo_statistics_map_size} */
    public byte clingo_statistics_map_size(Pointer statistics, long key, SizeByReference p_size);

    /** {@link clingo_h#clingo_statistics_map_has_subkey} */
    public byte clingo_statistics_map_has_subkey(Pointer statistics, long key, String p_name, ByteByReference p_result);

    /** {@link clingo_h#clingo_statistics_map_subkey_name} */
    public byte clingo_statistics_map_subkey_name(Pointer statistics, long key, SizeT offset, String[] p_p_name);

    /** {@link clingo_h#clingo_statistics_map_at} */
    public byte clingo_statistics_map_at(Pointer statistics, long key, String p_name, LongByReference p_subkey);

    /** {@link clingo_h#clingo_statistics_map_add_subkey} */
    public byte clingo_statistics_map_add_subkey(Pointer p_statistics, long key, String p_name, int type, LongByReference p_subkey);

    // Functions to inspect and change values

    /** {@link clingo_h#clingo_statistics_value_get} */
    public byte clingo_statistics_value_get(Pointer statistics, long key, DoubleByReference p_value);

    /** {@link clingo_h#clingo_statistics_value_set} */
    public byte clingo_statistics_value_set(Pointer p_statistics, long key, double value);
    
    // model and solve control

    /** {@link clingo_h#clingo_model_type} */
    public byte clingo_model_type(Pointer p_model, IntByReference p_type);

    /** {@link clingo_h#clingo_model_number} */
    public byte clingo_model_number(Pointer p_model, LongByReference p_number);

    /** {@link clingo_h#clingo_model_symbols_size} */
    public byte clingo_model_symbols_size(Pointer model, int show, SizeByReference size);

    /** {@link clingo_h#clingo_model_symbols} */
    public byte clingo_model_symbols(Pointer p_model, int show, long[] p_symbols, SizeT size);

    /** {@link clingo_h#clingo_model_contains} */
    public byte clingo_model_contains(Pointer model, long atom, ByteByReference p_contained);

    /** {@link clingo_h#clingo_model_is_true} */
    public byte clingo_model_is_true(Pointer model, int literal, ByteByReference p_result);

    /** {@link clingo_h#clingo_model_cost_size} */
    public byte clingo_model_cost_size(Pointer model, SizeByReference p_size);

    /** {@link clingo_h#clingo_model_cost} */
    public byte clingo_model_cost(Pointer model, IntByReference p_costs, SizeT size);

    /** {@link clingo_h#clingo_model_optimality_proven} */
    public byte clingo_model_optimality_proven(Pointer model, ByteByReference p_proven);

    /** {@link clingo_h#clingo_model_thread_id} */
    public byte clingo_model_thread_id(Pointer model, IntByReference p_id);

    /** {@link clingo_h#clingo_model_extend} */
    public byte clingo_model_extend(Pointer p_model, long p_symbols, SizeT size);
    
    // Functions for Adding Clauses
    
    /** {@link clingo_h#clingo_model_context} */
    public byte clingo_model_context(Pointer model, PointerByReference p_p_control);
    
    /** {@link clingo_h#clingo_solve_control_symbolic_atoms} */
    public byte clingo_solve_control_symbolic_atoms(Pointer p_control, PointerByReference p_p_atoms);
    
    /** {@link clingo_h#clingo_solve_control_add_clause} */
    public byte clingo_solve_control_add_clause(Pointer p_control, Pointer p_clause, SizeT size);
    
    // solve result
    
  	// clingo_solve_result_e
  	// clingo_solve_result_bitset_t
  
    // solve handle
    
    // Solving
    
  	// clingo_solve_mode_e
  	// clingo_solve_mode_bitset_t
  	// clingo_solve_event_type_e
  	// clingo_solve_event_type_t
  	// clingo_solve_event_callback_t
  	// clingo_solve_handle_t

    /** {@link clingo_h#clingo_solve_handle_get} */
    boolean clingo_solve_handle_get(Pointer handle, IntByReference result);

    /** {@link clingo_h#clingo_solve_handle_wait} */
    public void clingo_solve_handle_wait(Pointer p_handle, double timeout, ByteByReference p_result);

    /** {@link clingo_h#clingo_solve_handle_model} */
    public byte clingo_solve_handle_model(Pointer p_handle, PointerByReference p_p_model);

    /** {@link clingo_h#clingo_solve_handle_core} */
	public byte clingo_solve_handle_core(Pointer p_handle, PointerByReference p_p_core, SizeByReference p_size);

    /** {@link clingo_h#clingo_solve_handle_resume} */
	public byte clingo_solve_handle_resume(Pointer p_handle);
	
    /** {@link clingo_h#clingo_solve_handle_cancel} */
	public byte clingo_solve_handle_cancel(Pointer p_handle);

    /** {@link clingo_h#clingo_solve_handle_close} */
    byte clingo_solve_handle_close(Pointer handle);
    
    //! @}
    // {{{1 ast v2
    
    //! @example ast.c
    //! The example shows how to rewrite a non-ground logic program.
    //!
    //! ## Output ##
    //!
    //! ~~~~~~~~
    //! ./ast 0
    //! Solving with enable = false...
    //! ModelSt:
    //! Solving with enable = true...
    //! ModelSt: enable a
    //! ModelSt: enable b
    //! Solving with enable = false...
    //! ModelSt:
    //! ~~~~~~~~
    //!
    //! ## Code ##
    
    //! @defgroup ASTv2 Abstract Syntax Trees Version 2
    //! Functions and data structures to work with program ASTs.
    
    //! @addtogroup ASTv2
    //! @{
    
    //! Enumeration of theory sequence types.
  /* enum clingo_ast_theory_sequence_type_e {
      clingo_ast_theory_sequence_type_tuple, //!< Theory tuples "(t1,...,tn)".
      clingo_ast_theory_sequence_type_list,  //!< Theory lists "[t1,...,tn]".
      clingo_ast_theory_sequence_type_set    //!< Theory sets "{t1,...,tn}".
  }; */ public static final typedef<c_enum> clingo_ast_theory_sequence_type_e = null;
    //! Corresponding type to ::clingo_ast_theory_sequence_type.
// public static final typedef<c_int> clingo_ast_theory_sequence_type_t = null;
    
    //! Enumeration of comparison relations.
  /* enum clingo_ast_comparison_operator_e {
      clingo_ast_comparison_operator_greater_than  = 0, //!< Operator ">".
      clingo_ast_comparison_operator_less_than     = 1, //!< Operator "<".
      clingo_ast_comparison_operator_less_equal    = 2, //!< Operator "<=".
      clingo_ast_comparison_operator_greater_equal = 3, //!< Operator ">=".
      clingo_ast_comparison_operator_not_equal     = 4, //!< Operator "!=".
      clingo_ast_comparison_operator_equal         = 5  //!< Operator "==".
  }; */ public static final typedef<c_enum> clingo_ast_comparison_operator_e = null;
    //! Corresponding type to ::clingo_ast_comparison_operator.
// public static final typedef<c_int> clingo_ast_comparison_operator_t = null;
    
    //! Enumeration of signs.
  /* enum clingo_ast_sign_e {
      clingo_ast_sign_no_sign         = 0, //!< For positive literals.
      clingo_ast_sign_negation        = 1, //!< For negative literals (prefix "not").
      clingo_ast_sign_double_negation = 2  //!< For double negated literals (prefix "not not").
  }; */ public static final typedef<c_enum> clingo_ast_sign_e = null;
    //! Corresponding type to ::clingo_ast_sign_t.
// public static final typedef<c_int> clingo_ast_sign_t = null;
    
    //! Enumeration of unary operators.
  /* enum clingo_ast_unary_operator_e {
      clingo_ast_unary_operator_minus    = 0, //!< Operator "-".
      clingo_ast_unary_operator_negation = 1, //!< Operator "~".
      clingo_ast_unary_operator_absolute = 2  //!< Operator "|.|".
  }; */ public static final typedef<c_enum> clingo_ast_unary_operator_e = null;
    //! Corresponding type to ::clingo_ast_unary_operator.
// public static final typedef<c_int> clingo_ast_unary_operator_t = null;
    
    //! Enumeration of binary operators.
  /* enum clingo_ast_binary_operator_e {
      clingo_ast_binary_operator_xor            = 0, //!< Operator "^".
      clingo_ast_binary_operator_or             = 1, //!< Operator "?".
      clingo_ast_binary_operator_and            = 2, //!< Operator "&".
      clingo_ast_binary_operator_plus           = 3, //!< Operator "+".
      clingo_ast_binary_operator_minus          = 4, //!< Operator "-".
      clingo_ast_binary_operator_multiplication = 5, //!< Operator "*".
      clingo_ast_binary_operator_division       = 6, //!< Operator "/".
      clingo_ast_binary_operator_modulo         = 7, //!< Operator "\".
      clingo_ast_binary_operator_power          = 8  //!< Operator "**".
  }; */ public static final typedef<c_enum> clingo_ast_binary_operator_e = null;
    //! Corresponding type to ::clingo_ast_binary_operator.
// public static final typedef<c_int> clingo_ast_binary_operator_t = null;
    
    //! Enumeration of aggregate functions.
  /* enum clingo_ast_aggregate_function_e {
      clingo_ast_aggregate_function_count = 0, //!< Operator "^".
      clingo_ast_aggregate_function_sum   = 1, //!< Operator "?".
      clingo_ast_aggregate_function_sump  = 2, //!< Operator "&".
      clingo_ast_aggregate_function_min   = 3, //!< Operator "+".
      clingo_ast_aggregate_function_max   = 4  //!< Operator "-".
  }; */ public static final typedef<c_enum> clingo_ast_aggregate_function_e = null;
    //! Corresponding type to ::clingo_ast_aggregate_function.
// public static final typedef<c_int> clingo_ast_aggregate_function_t = null;
    
    //! Enumeration of theory operators.
  /* enum clingo_ast_theory_operator_type_e {
       clingo_ast_theory_operator_type_unary        = 0, //!< An unary theory operator.
       clingo_ast_theory_operator_type_binary_left  = 1, //!< A left associative binary operator.
       clingo_ast_theory_operator_type_binary_right = 2  //!< A right associative binary operator.
  }; */ public static final typedef<c_enum> clingo_ast_theory_operator_type_e = null;
    //! Corresponding type to ::clingo_ast_theory_operator_type.
// public static final typedef<c_int> clingo_ast_theory_operator_type_t = null;
    
    //! Enumeration of the theory atom types.
  /* enum clingo_ast_theory_atom_definition_type_e {
      clingo_ast_theory_atom_definition_type_head      = 0, //!< For theory atoms that can appear in the head.
      clingo_ast_theory_atom_definition_type_body      = 1, //!< For theory atoms that can appear in the body.
      clingo_ast_theory_atom_definition_type_any       = 2, //!< For theory atoms that can appear in both head and body.
      clingo_ast_theory_atom_definition_type_directive = 3  //!< For theory atoms that must not have a body.
  }; */ public static final typedef<c_enum> clingo_ast_theory_atom_definition_type_e = null;
    //! Corresponding type to ::clingo_ast_theory_atom_definition_type.
// public static final typedef<c_int> clingo_ast_theory_atom_definition_type_t = null;
    
    //! Enumeration of script types.
  /* enum clingo_ast_script_type_e {
      clingo_ast_script_type_lua    = 0, //!< For Lua scripts.
      clingo_ast_script_type_python = 1  //!< For Python scripts.
  }; */ public static final typedef<c_enum> clingo_ast_script_type_e = null;
    //! Corresponding type to ::clingo_ast_script_type.
// public static final typedef<c_int> clingo_ast_script_type_t = null;
    
    //! Enumeration of AST types.
  /* enum clingo_ast_type_e {
      // terms
      clingo_ast_type_id,
      clingo_ast_type_variable,
      clingo_ast_type_symbolic_term,
      clingo_ast_type_unary_operation,
      clingo_ast_type_binary_operation,
      clingo_ast_type_interval,
      clingo_ast_type_function,
      clingo_ast_type_pool,
      // csp terms
      clingo_ast_type_csp_product,
      clingo_ast_type_csp_sum,
      clingo_ast_type_csp_guard,
      // simple atoms
      clingo_ast_type_boolean_constant,
      clingo_ast_type_symbolic_atom,
      clingo_ast_type_comparison,
      clingo_ast_type_csp_literal,
      // aggregates
      clingo_ast_type_aggregate_guard,
      clingo_ast_type_conditional_literal,
      clingo_ast_type_aggregate,
      clingo_ast_type_body_aggregate_element,
      clingo_ast_type_body_aggregate,
      clingo_ast_type_head_aggregate_element,
      clingo_ast_type_head_aggregate,
      clingo_ast_type_disjunction,
      clingo_ast_type_disjoint_element,
      clingo_ast_type_disjoint,
      // theory atoms
      clingo_ast_type_theory_sequence,
      clingo_ast_type_theory_function,
      clingo_ast_type_theory_unparsed_term_element,
      clingo_ast_type_theory_unparsed_term,
      clingo_ast_type_theory_guard,
      clingo_ast_type_theory_atom_element,
      clingo_ast_type_theory_atom,
      // literals
      clingo_ast_type_literal,
      // theory definition
      clingo_ast_type_theory_operator_definition,
      clingo_ast_type_theory_term_definition,
      clingo_ast_type_theory_guard_definition,
      clingo_ast_type_theory_atom_definition,
      // statements
      clingo_ast_type_rule,
      clingo_ast_type_definition,
      clingo_ast_type_show_signature,
      clingo_ast_type_show_term,
      clingo_ast_type_minimize,
      clingo_ast_type_script,
      clingo_ast_type_program,
      clingo_ast_type_external,
      clingo_ast_type_edge,
      clingo_ast_type_heuristic,
      clingo_ast_type_project_atom,
      clingo_ast_type_project_signature,
      clingo_ast_type_defined,
      clingo_ast_type_theory_definition
  }; */ public static final typedef<c_enum> clingo_ast_type_e = null;
    //! Corresponding type to ::clingo_ast_type.
// public static final typedef<c_int> clingo_ast_type_t = null;
    
    //! Enumeration of attributes types used by the AST.
  /* enum clingo_ast_attribute_type_e {
      clingo_ast_attribute_type_number       = 0, //!< For an attribute of type "int".
      clingo_ast_attribute_type_symbol       = 1, //!< For an attribute of type "clingo_ast_symbol_t".
      clingo_ast_attribute_type_location     = 2, //!< For an attribute of type "clingo_location_t".
      clingo_ast_attribute_type_string       = 3, //!< For an attribute of type "char const *".
      clingo_ast_attribute_type_ast          = 4, //!< For an attribute of type "clingo_ast_t *".
      clingo_ast_attribute_type_optional_ast = 5, //!< For an attribute of type "clingo_ast_t *" that can be NULL.
      clingo_ast_attribute_type_string_array = 6, //!< For an attribute of type "char const **".
      clingo_ast_attribute_type_ast_array    = 7, //!< For an attribute of type "clingo_ast_t **".
  }; */ public static final typedef<c_enum> clingo_ast_attribute_type_e = null;
    //! Corresponding type to ::clingo_ast_attribute_type.
// public static final typedef<c_int> clingo_ast_attribute_type_t = null;
    
    //! Enumeration of attributes used by the AST.
  /* enum clingo_ast_attribute_e {
      clingo_ast_attribute_argument,
      clingo_ast_attribute_arguments,
      clingo_ast_attribute_arity,
      clingo_ast_attribute_atom,
      clingo_ast_attribute_atoms,
      clingo_ast_attribute_atom_type,
      clingo_ast_attribute_bias,
      clingo_ast_attribute_body,
      clingo_ast_attribute_code,
      clingo_ast_attribute_coefficient,
      clingo_ast_attribute_comparison,
      clingo_ast_attribute_condition,
      clingo_ast_attribute_csp,
      clingo_ast_attribute_elements,
      clingo_ast_attribute_external,
      clingo_ast_attribute_external_type,
      clingo_ast_attribute_function,
      clingo_ast_attribute_guard,
      clingo_ast_attribute_guards,
      clingo_ast_attribute_head,
      clingo_ast_attribute_is_default,
      clingo_ast_attribute_left,
      clingo_ast_attribute_left_guard,
      clingo_ast_attribute_literal,
      clingo_ast_attribute_location,
      clingo_ast_attribute_modifier,
      clingo_ast_attribute_name,
      clingo_ast_attribute_node_u,
      clingo_ast_attribute_node_v,
      clingo_ast_attribute_operator_name,
      clingo_ast_attribute_operator_type,
      clingo_ast_attribute_operators,
      clingo_ast_attribute_parameters,
      clingo_ast_attribute_positive,
      clingo_ast_attribute_priority,
      clingo_ast_attribute_right,
      clingo_ast_attribute_right_guard,
      clingo_ast_attribute_script_type,
      clingo_ast_attribute_sequence_type,
      clingo_ast_attribute_sign,
      clingo_ast_attribute_symbol,
      clingo_ast_attribute_term,
      clingo_ast_attribute_terms,
      clingo_ast_attribute_value,
      clingo_ast_attribute_variable,
      clingo_ast_attribute_weight,
  }; */ public static final typedef<c_enum> clingo_ast_attribute_e = null;
    //! Corresponding type to ::clingo_ast_attribute.
// public static final typedef<c_int> clingo_ast_attribute_t = null;
    
    //! Struct to map attributes to their string representation.
  /* typedef struct clingo_ast_attribute_names {
      char const * const * names;
      size_t size;
  } clingo_ast_attribute_names_t; */ public static final typedef<struct> clingo_ast_attribute_names_t = null;

    //! A map from attributes to their string representation.
// public static clingo_ast_attribute_names_t g_clingo_ast_attribute_names = null; // CLINGO_VISIBILITY_DEFAULT extern clingo_ast_attribute_names_t g_clingo_ast_attribute_names;
    
    //! Struct to define an argument that consists of a name and a type.
  /* typedef struct clingo_ast_argument {
      clingo_ast_attribute_t attribute;
      clingo_ast_attribute_type_t type;
  } clingo_ast_argument_t; */ public static final typedef<struct> clingo_ast_argument_t = null;

    //! A lists of required attributes to construct an AST.
  /* typedef struct clingo_ast_constructor {
      char const *name;
      clingo_ast_argument_t const *arguments;
      size_t size;
  } clingo_ast_constructor_t; */ public static final typedef<struct> clingo_ast_constructor_t = null;

    //! Struct to map AST types to lists of required attributes to construct ASTs.
  /* typedef struct clingo_ast_constructors {
      clingo_ast_constructor_t const *constructors;
      size_t size;
  } clingo_ast_constructors_t; */ public static final typedef<struct> clingo_ast_constructors_t = null;

    //! A map from AST types to their constructors.
    //!
    //! @note The idea of this variable is to provide enough information to auto-generate code for language bindings.
// public static final clingo_ast_constructors_t g_clingo_ast_constructors = null; // CLINGO_VISIBILITY_DEFAULT extern clingo_ast_constructors_t g_clingo_ast_constructors;
    
    //! This struct provides a view to nodes in the AST.
// public static final typedef<struct> clingo_ast_t = null;
    
    // Functions to construct ASTs
    
  	/** {@link clingo_h#clingo_ast_build} */
  	public byte clingo_ast_build(int type, PointerByReference p_p_ast);
    
    // Functions to manage life time of ASTs

  	/** {@link clingo_h#clingo_ast_acquire} */
	public void clingo_ast_acquire(Pointer p_ast);

  	/** {@link clingo_h#clingo_ast_release} */
	public void clingo_ast_release(Pointer p_ast);
    
    // Functions to copy ASTs

  	/** {@link clingo_h#clingo_ast_copy} */
	public byte clingo_ast_copy(Pointer p_ast, PointerByReference p_p_copy);
	
  	/** {@link clingo_h#clingo_ast_deep_copy} */
	public byte clingo_ast_deep_copy(Pointer p_ast, PointerByReference p_p_copy);
    
    // Functions to compare ASTs

  	/** {@link clingo_h#clingo_ast_less_than} */
	public byte clingo_ast_less_than(Pointer p_a, Pointer p_b);

  	/** {@link clingo_h#clingo_ast_equal} */
	public byte clingo_ast_equal(Pointer p_a, Pointer p_b);

  	/** {@link clingo_h#clingo_ast_hash} */
	public SizeT clingo_ast_hash(Pointer p_ast);
    
    // Functions to convert ASTs to strings

  	/** {@link clingo_h#clingo_ast_to_string_size} */
	public byte clingo_ast_to_string_size(Pointer p_ast, SizeByReference p_size);
	
  	/** {@link clingo_h#clingo_ast_to_string} */
	public byte clingo_ast_to_string(Pointer p_ast, Memory p_string, SizeT size);
    
    // Functions to inspect ASTs

  	/** {@link clingo_h#clingo_ast_get_type} */
	public byte clingo_ast_get_type(Pointer p_ast, IntByReference p_type);

  	/** {@link clingo_h#clingo_ast_has_attribute} */
	public byte clingo_ast_has_attribute(Pointer p_ast, Pointer attribute, ByteByReference p_has_attribute);

  	/** {@link clingo_h#clingo_ast_attribute_type} */
	public byte clingo_ast_attribute_type(Pointer p_ast, Pointer attribute, IntByReference p_type);
    
    // Functions to get/set numeric attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_number} */
	public byte clingo_ast_attribute_get_number(Pointer p_ast, Pointer attribute, IntByReference p_value);

  	/** {@link clingo_h#clingo_ast_attribute_set_number} */
	public byte clingo_ast_attribute_set_number(Pointer p_ast, Pointer attribute, int value);
    
    // Functions to get/set symbolic attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_symbol} */
	public byte clingo_ast_attribute_get_symbol(Pointer p_ast, Pointer attribute, LongByReference p_value);

  	/** {@link clingo_h#clingo_ast_attribute_set_symbol} */
	public byte clingo_ast_attribute_set_symbol(Pointer p_ast, Pointer attribute, long value);
    
    // Functions to get/set location attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_location} */
	public byte clingo_ast_attribute_get_location(Pointer p_ast, Pointer attribute, PointerByReference p_value);

  	/** {@link clingo_h#clingo_ast_attribute_set_location} */
	public byte clingo_ast_attribute_set_location(Pointer p_ast, Pointer attribute, Pointer p_value);
    
    // Functions to get/set string attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_string} */
	public byte clingo_ast_attribute_get_string(Pointer p_ast, Pointer attribute, String[] p_p_value);

  	/** {@link clingo_h#clingo_ast_attribute_get_string} */
	public byte clingo_ast_attribute_set_string(Pointer p_ast, Pointer attribute, String p_value);
    
    // Functions to get/set AST attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_ast} */
	public byte clingo_ast_attribute_get_ast(Pointer p_ast, Pointer attribute, IntByReference p_p_value);

  	/** {@link clingo_h#clingo_ast_attribute_set_ast} */
	public byte clingo_ast_attribute_set_ast(Pointer p_ast, Pointer attribute, IntByReference p_value);
    
    // Functions to get/set optional AST attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_optional_ast} */
	public byte clingo_ast_attribute_get_optional_ast(Pointer p_ast, Pointer attribute, IntByReference p_p_value);

  	/** {@link clingo_h#clingo_ast_attribute_set_optional_ast} */
	public byte clingo_ast_attribute_set_optional_ast(Pointer p_ast, Pointer attribute, IntByReference p_value);
    
    // Functions to get/set string array attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_string_at} */
	public byte clingo_ast_attribute_get_string_at(Pointer p_ast, Pointer attribute, SizeT index, String[] p_p_value);

  	/** {@link clingo_h#clingo_ast_attribute_set_string_at} */
	public byte clingo_ast_attribute_set_string_at(Pointer p_ast, Pointer attribute, SizeT index, String p_value);

  	/** {@link clingo_h#clingo_ast_attribute_delete_string_at} */
	public byte clingo_ast_attribute_delete_string_at(Pointer p_ast, Pointer attribute, SizeT index);

  	/** {@link clingo_h#clingo_ast_attribute_size_string_array} */
	public byte clingo_ast_attribute_size_string_array(Pointer p_ast, Pointer attribute, SizeByReference p_size);

  	/** {@link clingo_h#clingo_ast_attribute_insert_string_at} */
	public byte clingo_ast_attribute_insert_string_at(Pointer p_ast, Pointer attribute, SizeT index, String p_value);
    
    // Functions to get/set AST array attributes of ASTs

  	/** {@link clingo_h#clingo_ast_attribute_get_ast_at} */
	public byte clingo_ast_attribute_get_ast_at(Pointer p_ast, Pointer attribute, SizeT index, IntByReference p_p_value);

  	/** {@link clingo_h#clingo_ast_attribute_set_ast_at} */
	public byte clingo_ast_attribute_set_ast_at(Pointer p_ast, Pointer attribute, SizeT index, IntByReference p_value);

  	/** {@link clingo_h#clingo_ast_attribute_delete_ast_at} */
	public byte clingo_ast_attribute_delete_ast_at(Pointer p_ast, Pointer attribute, SizeT index);

  	/** {@link clingo_h#clingo_ast_attribute_size_ast_array} */
	public byte clingo_ast_attribute_size_ast_array(Pointer p_ast, Pointer attribute, SizeByReference p_size);

  	/** {@link clingo_h#clingo_ast_attribute_insert_ast_at} */
	public byte clingo_ast_attribute_insert_ast_at(Pointer p_ast, Pointer attribute, SizeT index, IntByReference p_value);
    
    // Functions to construct ASTs from strings
    
    // clingo_ast_callback_t

  	/** {@link clingo_h#clingo_ast_callback_t} */
	public interface AstCallback extends Callback {
		byte callback(Pointer ast, Pointer data);
	}

  	/** {@link clingo_h#clingo_ast_parse_string} */
	public byte clingo_ast_parse_string(String p_program, AstCallback callback, Pointer p_callback_data, Pointer logger, Pointer p_logger_data, int message_limit);

  	/** {@link clingo_h#clingo_ast_parse_files} */
	public byte clingo_ast_parse_files(String const_p_const_p_files, SizeT size, AstCallback callback, String p_callback_data, Pointer logger, String p_logger_data, int message_limit);
    
    // Object to build non-ground programs.
	
	//  clingo_program_builder_t

  	/** {@link clingo_h#clingo_program_builder_begin} */
	public byte clingo_program_builder_begin(Pointer p_builder);

  	/** {@link clingo_h#clingo_program_builder_end} */
	public byte clingo_program_builder_end(Pointer p_builder);

  	/** {@link clingo_h#clingo_program_builder_add} */
	public byte clingo_program_builder_add(Pointer p_builder, Pointer p_ast);
    
    // Functions to unpool ASts
    
    // clingo_ast_unpool_type_e
    // clingo_ast_unpool_type_bitset_t

  	/** {@link clingo_h#clingo_ast_unpool} */
	public byte clingo_ast_unpool(Pointer p_ast, int unpool_type, AstCallback callback, String p_callback_data);
    
    // ground program observer
    
	// TODO
    //! @defgroup ProgramInspection Program Inspection
    //! Functions and data structures to inspect programs.
    //! @ingroup ControlSt
    
    //! @addtogroup ProgramInspection
    //! @{
    
    //! An instance of this struct has to be registered with a solver to observe ground directives as they are passed to the solver.
    //!
    //! @note This interface is closely modeled after the aspif format.
    //! For more information please refer to the specification of the aspif format.
    //!
    //! Not all callbacks have to be implemented and can be set to NULL if not needed.
    //! If one of the callbacks in the struct fails, grounding is stopped.
    //! If a non-recoverable clingo API call fails, a callback must return false.
    //! Otherwise ::clingo_error_unknown should be set and false returned.
    //!
    //! @see clingo_control_register_observer()
    //typedef struct clingo_ground_program_observer {
    //! Called once in the beginning.
    //!
    //! If the incremental flag is true, there can be multiple calls to @ref clingo_control_solve().
    //!
    //! @param[in] incremental whether the program is incremental
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*init_program)(bool incremental, void *data);
    //! Marks the beginning of a block of directives passed to the solver.
    //!
    //! @see @ref end_step
    //!
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*begin_step)(void *data);
    //! Marks the end of a block of directives passed to the solver.
    //!
    //! This function is called before solving starts.
    //!
    //! @see @ref begin_step
    //!
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*end_step)(void *data);
    
    //! Observe rules passed to the solver.
    //!
    //! @param[in] choice determines if the head is a choice or a disjunction
    //! @param[in] head the head atoms
    //! @param[in] head_size the number of atoms in the head
    //! @param[in] body the body literals
    //! @param[in] body_size the number of literals in the body
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*rule)(bool choice, clingo_atom_t const *head, size_t head_size, clingo_literal_t const *body, size_t body_size, void *data);
    //! Observe weight rules passed to the solver.
    //!
    //! @param[in] choice determines if the head is a choice or a disjunction
    //! @param[in] head the head atoms
    //! @param[in] head_size the number of atoms in the head
    //! @param[in] lower_bound the lower bound of the weight rule
    //! @param[in] body the weighted body literals
    //! @param[in] body_size the number of weighted literals in the body
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*weight_rule)(bool choice, clingo_atom_t const *head, size_t head_size, clingo_weight_t lower_bound, clingo_weighted_literal_t const *body, size_t body_size, void *data);
    //! Observe minimize constraints (or weak constraints) passed to the solver.
    //!
    //! @param[in] priority the priority of the constraint
    //! @param[in] literals the weighted literals whose sum to minimize
    //! @param[in] size the number of weighted literals
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*minimize)(clingo_weight_t priority, clingo_weighted_literal_t const* literals, size_t size, void *data);
    //! Observe projection directives passed to the solver.
    //!
    //! @param[in] atoms the atoms to project on
    //! @param[in] size the number of atoms
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*project)(clingo_atom_t const *atoms, size_t size, void *data);
    //! Observe shown atoms passed to the solver.
    //! \note Facts do not have an associated aspif atom.
    //! The value of the atom is set to zero.
    //!
    //! @param[in] symbol the symbolic representation of the atom
    //! @param[in] atom the aspif atom (0 for facts)
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*output_atom)(clingo_symbol_t symbol, clingo_atom_t atom, void *data);
    //! Observe shown terms passed to the solver.
    //!
    //! @param[in] symbol the symbolic representation of the term
    //! @param[in] condition the literals of the condition
    //! @param[in] size the size of the condition
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*output_term)(clingo_symbol_t symbol, clingo_literal_t const *condition, size_t size, void *data);
    //! Observe shown csp variables passed to the solver.
    //!
    //! @param[in] symbol the symbolic representation of the variable
    //! @param[in] value the value of the variable
    //! @param[in] condition the literals of the condition
    //! @param[in] size the size of the condition
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*output_csp)(clingo_symbol_t symbol, int value, clingo_literal_t const *condition, size_t size, void *data);
    //! Observe external statements passed to the solver.
    //!
    //! @param[in] atom the external atom
    //! @param[in] type the type of the external statement
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*external)(clingo_atom_t atom, clingo_external_type_t type, void *data);
    //! Observe assumption directives passed to the solver.
    //!
    //! @param[in] literals the literals to assume (positive literals are true and negative literals false for the next solve call)
    //! @param[in] size the number of atoms
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*assume)(clingo_literal_t const *literals, size_t size, void *data);
    //! Observe heuristic directives passed to the solver.
    //!
    //! @param[in] atom the target atom
    //! @param[in] type the type of the heuristic modification
    //! @param[in] bias the heuristic bias
    //! @param[in] priority the heuristic priority
    //! @param[in] condition the condition under which to apply the heuristic modification
    //! @param[in] size the number of atoms in the condition
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*heuristic)(clingo_atom_t atom, clingo_heuristic_type_t type, int bias, unsigned priority, clingo_literal_t const *condition, size_t size, void *data);
    //! Observe edge directives passed to the solver.
    //!
    //! @param[in] node_u the start vertex of the edge
    //! @param[in] node_v the end vertex of the edge
    //! @param[in] condition the condition under which the edge is part of the graph
    //! @param[in] size the number of atoms in the condition
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*acyc_edge)(int node_u, int node_v, clingo_literal_t const *condition, size_t size, void *data);
    
    //! Observe numeric theory terms.
    //!
    //! @param[in] term_id the id of the term
    //! @param[in] number the value of the term
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*theory_term_number)(clingo_id_t term_id, int number, void *data);
    //! Observe string theory terms.
    //!
    //! @param[in] term_id the id of the term
    //! @param[in] name the value of the term
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*theory_term_string)(clingo_id_t term_id, char const *name, void *data);
    //! Observe compound theory terms.
    //!
    //! The name_id_or_type gives the type of the compound term:
    //! - if it is -1, then it is a tuple
    //! - if it is -2, then it is a set
    //! - if it is -3, then it is a list
    //! - otherwise, it is a function and name_id_or_type refers to the id of the name (in form of a string term)
    //!
    //! @param[in] term_id the id of the term
    //! @param[in] name_id_or_type the name or type of the term
    //! @param[in] arguments the arguments of the term
    //! @param[in] size the number of arguments
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*theory_term_compound)(clingo_id_t term_id, int name_id_or_type, clingo_id_t const *arguments, size_t size, void *data);
    //! Observe theory elements.
    //!
    //! @param element_id the id of the element
    //! @param terms the term tuple of the element
    //! @param terms_size the number of terms in the tuple
    //! @param condition the condition of the elemnt
    //! @param condition_size the number of literals in the condition
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*theory_element)(clingo_id_t element_id, clingo_id_t const *terms, size_t terms_size, clingo_literal_t const *condition, size_t condition_size, void *data);
    //! Observe theory atoms without guard.
    //!
    //! @param[in] atom_id_or_zero the id of the atom or zero for directives
    //! @param[in] term_id the term associated with the atom
    //! @param[in] elements the elements of the atom
    //! @param[in] size the number of elements
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
// bool (*theory_atom)(clingo_id_t atom_id_or_zero, clingo_id_t term_id, clingo_id_t const *elements, size_t size, void *data);
    //! Observe theory atoms with guard.
    //!
    //! @param[in] atom_id_or_zero the id of the atom or zero for directives
    //! @param[in] term_id the term associated with the atom
    //! @param[in] elements the elements of the atom
    //! @param[in] size the number of elements
    //! @param[in] operator_id the id of the operator (a string term)
    //! @param[in] right_hand_side_id the id of the term on the right hand side of the atom
    //! @param[in] data user data for the callback
    //! @return whether the call was successful
    /*    bool (*theory_atom_with_guard)(clingo_id_t atom_id_or_zero, clingo_id_t term_id, clingo_id_t const *elements, size_t size, clingo_id_t operator_id, clingo_id_t right_hand_side_id, void *data);
    } clingo_ground_program_observer_t; */ public static final typedef<struct> clingo_ground_program_observer_t = null;
    
    // clingo_ground_program_observer_t
    
    // ControlSt
    
    // clingo_solve_result_e
    // clingo_solve_result_bitset_t
  	// clingo_part
  	// clingo_part_t
    // clingo_ground_callback_t
    // clingo_control_t
  
    /** {@link clingo_h#clingo_control_new} */
    public byte clingo_control_new(StringArray arguments, int arguments_size, Pointer logger, Pointer logger_data, int message_limit, PointerByReference control);
  
    /** {@link clingo_h#clingo_control_free} */
    void clingo_control_free(Pointer control);
  
    // Grounding Functions

    /** {@link clingo_h#clingo_control_load} */
    public byte clingo_control_load(Pointer p_control, String p_file);

    /** {@link clingo_h#clingo_control_add} */
    public byte  clingo_control_add(Pointer p_control, String name, String[] parameters, SizeT parameters_size, String program);

    /** {@link clingo_h#clingo_control_ground} */
    public byte clingo_control_ground(Pointer p_control, PartSt[] p_parts, SizeT parts_size, GroundCallback ground_callback, Pointer p_ground_callback_data);
    
    // Solving Functions

    /** {@link clingo_h#clingo_control_solve} */
    public byte clingo_control_solve(Pointer control, int mode, Pointer assumptions, SizeT assumptions_size, SolveEventCallback notify, Pointer data, PointerByReference handle);

    /** {@link clingo_h#clingo_control_cleanup} */
	public byte clingo_control_cleanup(Pointer p_control);

    /** {@link clingo_h#clingo_control_assign_external} */
	public byte clingo_control_assign_external(Pointer p_control, int literal, int value);

    /** {@link clingo_h#clingo_control_release_external} */
	public byte clingo_control_release_external(Pointer p_control, int literal);

    /** {@link clingo_h#clingo_control_release_external} */
	public byte clingo_control_register_propagator(Pointer p_control, PropagatorSt p_propagator, Pointer p_data, byte sequential);

    /** {@link clingo_h#clingo_control_release_external} */
	public byte clingo_control_is_conflicting(Pointer p_control);

    /** {@link clingo_h#clingo_control_statistics} */
    public byte clingo_control_statistics(Pointer control, PointerByReference p_p_statistics);

    /** {@link clingo_h#clingo_control_interrupt} */
    public void clingo_control_interrupt(Pointer p_control);

    /** {@link clingo_h#clingo_control_clasp_facade} */
    public byte clingo_control_clasp_facade(Pointer p_control, PointerByReference p_p_clasp);
    
    // ConfigurationSt Functions

    /** {@link clingo_h#clingo_control_configuration} */
    public byte clingo_control_configuration(Pointer p_control, PointerByReference p_p_configuration);

    /** {@link clingo_h#clingo_control_set_enable_enumeration_assumption} */
	public byte clingo_control_set_enable_enumeration_assumption(Pointer p_control, byte enable);

    /** {@link clingo_h#clingo_control_get_enable_enumeration_assumption} */
	public byte clingo_control_get_enable_enumeration_assumption(Pointer p_control);

    /** {@link clingo_h#clingo_control_set_enable_cleanup} */
	public byte clingo_control_set_enable_cleanup(Pointer p_control, byte enable);

    /** {@link clingo_h#clingo_control_get_enable_cleanup} */
	public byte clingo_control_get_enable_cleanup(Pointer p_control);
    
    // Program Inspection Functions
    
    /** {@link clingo_h#clingo_control_get_const} */
	public byte clingo_control_get_const(Pointer p_control, String p_name, IntByReference p_symbol);

    /** {@link clingo_h#clingo_control_has_const} */
	public byte clingo_control_has_const(Pointer p_control, String p_name, ByteByReference p_exists);

    /** {@link clingo_h#clingo_control_symbolic_atoms} */
    public byte clingo_control_symbolic_atoms(Pointer p_control, PointerByReference p_p_atoms);

    /** {@link clingo_h#clingo_control_theory_atoms} */
    public byte clingo_control_theory_atoms(Pointer p_control, PointerByReference p_p_atoms);

    /** {@link clingo_h#clingo_control_theory_atoms} */
    public byte clingo_control_register_observer(Pointer p_control, Pointer p_observer, byte replace, Pointer p_data);

    /** {@link clingo_h#clingo_control_backend} */
    public byte clingo_control_backend(Pointer p_control, PointerByReference p_p_backend);
    
    /** {@link clingo_h#clingo_control_program_builder} */
	public byte clingo_control_program_builder(Pointer p_control, PointerByReference p_p_builder);
	
    // Extending BaseClingo
	
	// clingo_main_function_t
	// clingo_default_model_printer_t
    // clingo_model_printer_t
    // clingo_application
  	// clingo_application_t

    /** {@link clingo_h#clingo_options_add} */
    public byte clingo_options_add(Pointer p_options, String p_group, String p_option, String p_description, OptionParseCallback p_parse /*(char const *value, void *data)*/, Pointer p_data, byte multi, String p_argument);

    /** {@link clingo_h#clingo_options_add_flag} */
    public byte clingo_options_add_flag(Pointer p_options, String p_group, String p_option, String p_description, byte p_target);
    
    /** {@link clingo_h#clingo_main} */
	public int clingo_main(Pointer p_application, String p_arguments, SizeT size, Pointer p_data);
    
}
