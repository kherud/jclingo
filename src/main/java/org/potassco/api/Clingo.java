package org.potassco.api;

import org.potassco.cpp.clingo_h;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.StatisticsType;
import org.potassco.enums.TermType;
import org.potassco.jna.BaseClingo;
import org.potassco.jna.OptionParseCallback;
import org.potassco.jna.PartSt;
import org.potassco.jna.SizeT;

import com.sun.jna.Pointer;

public class Clingo {

	/**
	 * Create a new control object.
	 * <p>
	 * A control object has to be freed using clingo_control_free(). TODO: This will
	 * be done in the ControlSt class.
	 * 
	 * @param arguments array of command line arguments
	 * @return resulting control object
	 */
	public Control control(String[] arguments) {
		return new Control(arguments);
	}

	/**
	 * @param name    {@link clingo_h#clingo_control_ground}
	 * @param control
	 */
	public void ground(Pointer control, String name) {
		PartSt[] parts = new PartSt[1];
		parts[0] = new PartSt(name, null, 0L);
		SizeT size = new SizeT(1);
		BaseClingo.controlGround(control, parts, size, null, null);
	}

	public String version() {
		return BaseClingo.version();
	}

	/**
	 * @return the last error code set by a clingo API call.
	 */
	public int errorCode() {
		return BaseClingo.errorCode();
	}

	/**
	 * @return the last error message set if an API call fails.
	 */
	public String errorMessage() {
		return BaseClingo.errorMessage();
	}

	/**
	 * Set a custom error code and message in the active thread.
	 * 
	 * @param code    code the error code
	 * @param message message the error message
	 */
	public void setError(int code, String message) {
		BaseClingo.setError(code, message);
	}

	/**
	 * @return the last error code set if an API call fails.
	 */
	public int getError() {
		return BaseClingo.getError();
	}

	/**
	 * Convert warning code into string.
	 * 
	 * @param code
	 * @return the error string
	 */
	public String warningString(int code) {
		return BaseClingo.warningString(code);
	}

	/*
	 * ******************* SignatureSt Functions *******************
	 */

	/**
	 * Create a new signature.
	 *
	 * @param[in] name name of the signature
	 * @param[in] arity arity of the signature
	 * @param[in] positive false if the signature has a classical negation sign
	 * @param[out] signature the resulting signature
	 * @return whether the call was successful; might set one of the following error
	 *         codes: - ::clingo_error_bad_alloc
	 *         {@link clingo_h#clingo_signature_create}
	 * @return
	 * @throws ClingoException
	 */
	public Pointer signatureCreate(String name, int arity, boolean positive) throws ClingoException {
		return BaseClingo.signatureCreate(name, arity, positive);
	}

	/**
	 * Get the name of a signature.
	 * 
	 * @note The string is internalized and valid for the duration of the process.
	 * 
	 *       {@link clingo_h#clingo_signature_name}
	 * @param signature [in] signature the target signature
	 * @return the name of the signature
	 */
	public String signatureName(Pointer signature) {
		return BaseClingo.signatureName(signature);
	}

	/**
	 * Get the arity of a signature. {@link clingo_h#clingo_signature_arity}
	 * 
	 * @param signature [in] signature the target signature
	 * @return the arity of the signature
	 */
	public int signatureArity(Pointer signature) {
		return BaseClingo.signatureArity(signature);
	}

	/**
	 * Whether the signature is positive (is not classically negated).
	 * 
	 * @param signature the target signature
	 * @return
	 */
	public boolean signatureIsPositive(Pointer signature) {
		return BaseClingo.signatureIsPositive(signature);
	}

	/**
	 * Whether the signature is negative (is classically negated).
	 * 
	 * @param signature the target signature
	 * @return
	 */
	public boolean signatureIsNegative(Pointer signature) {
		return BaseClingo.signatureIsNegative(signature);
	}

	/**
	 * Check if two signatures are equal.
	 * 
	 * @param a first signature
	 * @param b second signature
	 * @return
	 */
	public boolean signatureIsEqualTo(Pointer a, Pointer b) {
		return BaseClingo.signatureIsEqualTo(a, b);
	}

	/**
	 * Check if a signature is less than another signature.
	 * <p>
	 * Signatures are compared first by sign (unsigned < signed), then by aritthen
	 * by name.
	 * 
	 * @param a first signature
	 * @param b second signature
	 * @return
	 */
	public boolean signatureIsLessThan(Pointer a, Pointer b) {
		return BaseClingo.signatureIsLessThan(a, b);
	}

	/**
	 * Calculate a hash code of a signature.
	 * 
	 * @param signature the target signature
	 * @return
	 */
	public SizeT signatureHash(Pointer signature) {
		return BaseClingo.signatureHash(signature);
	}

	/**
	 * Internalize a string.
	 * <p>
	 * This functions takes a string as input and returns an equal unique string
	 * that is (at the moment) not freed until the program is closed.
	 * 
	 * @param string the string to internalize
	 * @return the internalized string
	 */
	public String addString(String string) {
		return BaseClingo.addString(string);
	}

	/*
	 * ************ theory atoms ************
	 */

	// Theory Term Inspection

	/**
	 * Get the type of the given theory term.
	 * 
	 * @param atoms container where the term is stored
	 * @param term  id of the term
	 * @return the resulting type
	 */
	public TermType theoryAtomsTermType(Pointer atoms, int term) {
		return BaseClingo.theoryAtomsTermType(atoms, term);
	}

	/**
	 * Get the number of the given numeric theory term.
	 * 
	 * @param atoms container where the term is stored
	 * @param term  id of the term
	 * @return the resulting number
	 */
	public int theoryAtomsTermNumber(Pointer atoms, int term) {
		return BaseClingo.theoryAtomsTermNumber(atoms, term);
	}

	/**
	 * Get the name of the given constant or function theory term.
	 * <p>
	 * 
	 * @note The lifetime of the string is tied to the current solve step.
	 *       <p>
	 * @pre The term must be of type ::clingo_theory_term_type_function or
	 *      ::clingo_theory_term_type_symbol.
	 * @param atoms container where the term is stored
	 * @param term  id of the term
	 * @return the resulting name
	 */
	public String theoryAtomsTermName(Pointer atoms, int term) {
		return BaseClingo.theoryAtomsTermName(atoms, term);
	}

	/**
	 * Get the arguments of the given function theory term.
	 * <p>
	 * 
	 * @pre The term must be of type ::clingo_theory_term_type_function.
	 * @param atoms container where the term is stored
	 * @param term  id of the term
	 * @return the resulting arguments in form of an array of term ids
	 */
	public long[] theoryAtomsTermArguments(Pointer atoms, int term) {
		return BaseClingo.theoryAtomsTermArguments(atoms, term);
	}

	/**
	 * Get the string representation of the given theory term.
	 * 
	 * @param atoms container where the term is stored
	 * @param term  id of the term
	 * @return the resulting string
	 */
	public String theoryAtomsTermToString(Pointer atoms, int term) {
		return BaseClingo.theoryAtomsTermToString(atoms, term);
	}

	// Theory Element Inspection

	/**
	 * Get the tuple (array of theory terms) of the given theory element.
	 * 
	 * @param atoms   container where the element is stored
	 * @param element id of the element
	 * @return the resulting array of term ids
	 */
	public long[] theoryAtomsElementTuple(Pointer atoms, int element) {
		return BaseClingo.theoryAtomsElementTuple(atoms, element);
	}

	/**
	 * Get the condition (array of aspif literals) of the given theory element.
	 * 
	 * @param atoms   container where the element is stored
	 * @param element id of the element
	 * @return the resulting array of aspif literals
	 */
	public long[] theoryAtomsElementCondition(Pointer atoms, int element) {
		return BaseClingo.theoryAtomsElementCondition(atoms, element);
	}

	/**
	 * Get the id of the condition of the given theory element.
	 * <p>
	 * 
	 * @note This id can be mapped to a solver literal using
	 *       clingo_propagate_init_solver_literal(). This id is not (necessarily) an
	 *       aspif literal; to get aspif literals use
	 *       clingo_theory_atoms_element_condition().
	 * @param atoms   container where the element is stored
	 * @param element id of the element
	 * @return the resulting condition id
	 */
	public int theoryAtomsElementConditionId(Pointer atoms, int element) {
		return BaseClingo.theoryAtomsElementConditionId(atoms, element);
	}

	/**
	 * Get the string representation of the given theory element.
	 * 
	 * @param atoms   container where the element is stored
	 * @param element id of the element
	 * @return the resulting string
	 */
	public String theoryAtomsElementToString(Pointer atoms, int element) {
		return BaseClingo.theoryAtomsElementToString(atoms, element);
	}

	// Theory Atom Inspection

	/**
	 * Get the total number of theory atoms.
	 * 
	 * @param atoms the target
	 * @return the resulting number
	 */
	public SizeT theoryAtomsSize(Pointer atoms) {
		return BaseClingo.theoryAtomsSize(atoms);
	}

	/**
	 * Get the theory term associated with the theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting term id
	 */
	public long theoryAtomsAtomTerm(Pointer atoms, int atom) {
		return BaseClingo.theoryAtomsAtomTerm(atoms, atom);
	}

	/**
	 * Get the theory elements associated with the theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting array of elements
	 */
	public long[] theoryAtomsAtomElements(Pointer atoms, int atom) {
		return BaseClingo.theoryAtomsAtomElements(atoms, atom);
	}

	/**
	 * Whether the theory atom has a guard.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return whether the theory atom has a guard
	 */
	public byte theoryAtomsAtomHasGuard(Pointer atoms, int atom) {
		return BaseClingo.theoryAtomsAtomHasGuard(atoms, atom);
	}

	/**
	 * Get the guard consisting of a theory operator and a theory term of the given
	 * theory atom.
	 * <p>
	 * 
	 * @note The lifetime of the string is tied to the current solve step.
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return
	 * @return
	 */
	public long[] theoryAtomsAtomGuard(Pointer atoms, int atom) {
		return BaseClingo.theoryAtomsAtomGuard(atoms, atom);
	}

	/**
	 * Get the aspif literal associated with the given theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting literal
	 */
	public int theoryAtomsAtomLiteral(Pointer atoms, int atom) {
		return BaseClingo.theoryAtomsAtomLiteral(atoms, atom);
	}

	/**
	 * Get the string representation of the given theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting size
	 */
	public String theoryAtomsAtomToString(Pointer atoms, int atom) {
		return BaseClingo.theoryAtomsAtomToString(atoms, atom);
	}

	/*
	 * ********** propagator **********
	 */

	/*
	 * ************* configuration *************
	 */

	/**
	 * Get the root key of the configuration.
	 * 
	 * @param configuration the target configuration
	 * @return the root key
	 */
	public int configurationRoot(Pointer configuration) {
		return BaseClingo.configurationRoot(configuration);
	}

	/**
	 * Get the type of a key.
	 * <p>
	 * 
	 * @note The type is bitset, an entry can have multiple (but at least one) type.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @return the resulting type
	 */
	public ConfigurationType configurationType(Pointer configuration, int key) {
		return BaseClingo.configurationType(configuration, key);
	}

	/**
	 * Get the description of an entry.
	 * 
	 * @param configuration the target configuration
	 * @param key           the key
	 * @return
	 * @return the resulting type
	 */
	public String configurationDescription(Pointer configuration, int key) {
		return BaseClingo.configurationDescription(configuration, key);
	}

	/* Functions to access arrays */

	/**
	 * Get the size of an array entry.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_array.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @return the resulting size
	 */
	public SizeT configurationArraySize(Pointer configuration, int key) {
		return BaseClingo.configurationArraySize(configuration, key);
	}

	/**
	 * Get the subkey at the given offset of an array entry.
	 * <p>
	 * 
	 * @note Some array entries, like fore example the solver configuration, can be
	 *       accessed past there actual size to add subentries.
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_array.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @param offset        the offset in the array
	 * @return the resulting subkey
	 */
	public long configurationArrayAt(Pointer configuration, int key, SizeT offset) {
		return BaseClingo.configurationArrayAt(configuration, key, offset);
	}

	/* Functions to access maps */

	/**
	 * Get the number of subkeys of a map entry.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_map.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @return the resulting size
	 */
	public SizeT configurationMapSize(Pointer configuration, int key) {
		return BaseClingo.configurationMapSize(configuration, key);
	}

	/**
	 * Query whether the map has a key.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_map.
	 * @note Multiple levels can be looked up by concatenating keys with a period.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @param name          the name to lookup the subkey
	 * @return the resulting subkey
	 */
	public long configurationMapHasSubkey(Pointer configuration, int key, String name) {
		return BaseClingo.configurationMapHasSubkey(configuration, key, name);
	}

	/**
	 * Get the name associated with the offset-th subkey.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_map.
	 * @param[in] configuration the target configuration
	 * @param configuration the target configuration
	 * @param key           the key
	 * @param offset        the offset of the name
	 * @return the resulting name
	 */
	public String configurationMapSubkeyName(Pointer configuration, int key, SizeT offset) {
		return BaseClingo.configurationMapSubkeyName(configuration, key, offset);
	}

	/**
	 * Lookup a subkey under the given name.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_map.
	 * @note Multiple levels can be looked up by concatenating keys with a period.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @param name          the name to lookup the subkey
	 * @return the resulting subkey
	 */
	public int configurationMapAt(Pointer configuration, int key, String name) {
		return BaseClingo.configurationMapAt(configuration, key, name);
	}

	/* Functions to access values */

	/**
	 * Check whether a entry has a value.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_value.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @return whether the entry has a value
	 */
	public byte configurationValueIsAssigned(Pointer configuration, int key) {
		return BaseClingo.configurationValueIsAssigned(configuration, key);
	}

	/**
	 * Get the size of the string value of the given entry.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_value.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @return the resulting size
	 */
	public SizeT configurationValueGetSize(Pointer configuration, int key) {
		return BaseClingo.configurationValueGetSize(configuration, key);
	}

	/**
	 * Get the string value of the given entry.
	 * <p>
	 * 
	 * @pre The @link clingo_configuration_type() type@endlink of the entry must
	 *      be @ref ::clingo_configuration_type_value.
	 * @pre The given size must be larger or equal to size of the value.
	 * @param configuration the target configuration
	 * @param key           the key
	 * @param size          the size of the given char array
	 * @return the resulting string value
	 */
	public String configurationValueGet(Pointer configuration, int key, SizeT size) {
		return BaseClingo.configurationValueGet(configuration, key, size);
	}

	/**
	 * Set the value of an entry.
	 * 
	 * @param configuration the target configuration
	 * @param key           the key
	 * @param value         the value to set
	 */
	public void configurationValueSet(Pointer configuration, int key, String value) {
		BaseClingo.configurationValueSet(configuration, key, value);
	}

	/*
	 * ********** statistics **********
	 */

	// StatisticsType

	/**
	 * Get the root key of the statistics.
	 * 
	 * @param statistics the target statistics
	 * @return the root key
	 */
	public long statisticsRoot(Pointer statistics) {
		return BaseClingo.statisticsRoot(statistics);
	}

	/**
	 * Get the type of a key.
	 * 
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @return the resulting type
	 */
	public StatisticsType statisticsType(Pointer statistics, long key) {
		return BaseClingo.statisticsType(statistics, key);
	}

	// Functions to access arrays

	/**
	 * Get the size of an array entry.
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_array.
	 * @param statistics the target statistics
	 * @param key        the key
	 * @return the resulting size
	 */
	public SizeT clingoStatisticsArraySize(Pointer statistics, long key) {
		return BaseClingo.statisticsArraySize(statistics, key);
	}

	/**
	 * Get the subkey at the given offset of an array entry.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_array.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[in] offset the offset in the array
	 * @param[out] subkey the resulting subkey
	 * @return whether the call was success
	 */

	public int statisticsArrayAt(Pointer statistics, long key, SizeT offset) {
		return BaseClingo.statisticsArrayAt(statistics, key, offset);
	}

	/**
	 * Create the subkey at the end of an array entry.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_array.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[in] type the type of the new subkey
	 * @param[out] subkey the resulting subkey
	 * @return whether the call was success
	 */
	public int statisticsArrayPush(Pointer statistics, long key, int type) {
		return BaseClingo.statisticsArrayPush(statistics, key, type);
	}

	// Functions to access maps

	/**
	 * Get the number of subkeys of a map entry.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_map.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[out] size the resulting number
	 * @return whether the call was success
	 */
	public SizeT statisticsMapSize(Pointer statistics, long key) {
		return BaseClingo.statisticsMapSize(statistics, key);
	}

	/**
	 * Test if the given map contains a specific subkey.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_map.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[in] name name of the subkey
	 * @param[out] result true if the map has a subkey with the given name
	 * @return whether the call was success
	 */
	public byte statisticsMapHasSubkey(Pointer statistics, long key, String name) {
		return BaseClingo.statisticsMapHasSubkey(statistics, key, name);
	}

	/**
	 * Get the name associated with the offset-th subkey.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_map.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[in] offset the offset of the name
	 * @param[out] name the resulting name
	 * @return whether the call was success
	 */
	public String statisticsMapSubkeyName(Pointer statistics, long key, SizeT offset) {
		return BaseClingo.statisticsMapSubkeyName(statistics, key, offset);
	}

	/**
	 * Lookup a subkey under the given name.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_map.
	 * @note Multiple levels can be looked up by concatenating keys with a period.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[in] name the name to lookup the subkey
	 * @param[out] subkey the resulting subkey
	 * @return whether the call was success
	 */
	public int statisticsMapAt(Pointer statistics, long key, String name) {
		return BaseClingo.statisticsMapAt(statistics, key, name);
	}

	/**
	 * Add a subkey with the given name.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_map.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[in] name the name of the new subkey
	 * @param[in] type the type of the new subkey
	 * @param[out] subkey the index of the resulting subkey
	 * @return whether the call was success
	 */
	public int statisticsMapAddSubkey(Pointer statistics, long key, String name, int type) {
		return BaseClingo.statisticsMapAddSubkey(statistics, key, name, type);
	}

	/**
	 * Get the value of the given entry.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_value.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[out] value the resulting value
	 * @return whether the call was successful
	 */
	public double statisticsValueGet(Pointer statistics, long key) {
		return BaseClingo.statisticsValueGet(statistics, key);
	}

	/**
	 * Set the value of the given entry.
	 * <p>
	 * 
	 * @pre The @link clingo_statistics_type() type@endlink of the entry must
	 *      be @ref ::clingo_statistics_type_value.
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @param[out] value the new value
	 * @return whether the call was success
	 */
	public void statisticsValueSet(Pointer statistics, long key, double value) {
		BaseClingo.statisticsValueSet(statistics, key, value);
	}

	/*
	 * ************************ model and solve control ************************
	 */

	/**
	 * Get an object to inspect the symbolic atoms.
	 *
	 * @param[in] control the target
	 * @param[out] atoms the resulting object
	 * @return whether the call was successful
	 */
	public Pointer solveControlSymbolicAtoms(Pointer control) {
		return BaseClingo.solveControlSymbolicAtoms(control);
	}

	/**
	 * Add a clause that applies to the current solving step during model
	 * enumeration.
	 *
	 * @note The @ref PropagatorSt module provides a more sophisticated interface to
	 *       add clauses - even on partial assignments.
	 *
	 * @param[in] control the target
	 * @param[in] clause array of literals representing the clause
	 * @param[in] size the size of the literal array
	 * @return whether the call was successful; might set one of the following error
	 *         codes: - ::clingo_error_bad_alloc - ::clingo_error_runtime if adding
	 *         the clause fails
	 */
	public void solveControlAddClause(Pointer control, Pointer clause, SizeT size) {
		BaseClingo.solveControlAddClause(control, clause, size);
		;
	}

	// solve result

	// clingo_solve_result_bitset_t

	// solve handle

	// clingo_solve_mode_bitset_t
	// clingo_solve_event_type_t
	// clingo_solve_event_callback_t
	// clingo_solve_handle_t

	/**
	 * Get the next solve result.
	 * <p>
	 * Blocks until the result is ready. When yielding partial solve results can be
	 * obtained, i.e., when a model is ready, the result will be satisfiable but
	 * neither the search exhausted nor the optimality proven.
	 * 
	 * @param handle the target
	 * @return the solve result
	 */
	public int solveHandleGet(Pointer handle) {
		return BaseClingo.solveHandleGet(handle);
	}

	/**
	 * Wait for the specified amount of time to check if the next result is ready.
	 * <p>
	 * If the time is set to zero, this function can be used to poll if the search
	 * is still active. If the time is negative, the function blocks until the
	 * search is finished.
	 * 
	 * @param handle  the target
	 * @param timeout the maximum time to wait
	 * @return whether the search has finished
	 */
	public boolean solveHandleWait(Pointer handle, double timeout) {
		return BaseClingo.solveHandleWait(handle, timeout);
	}

	/**
	 * Get the next model (or zero if there are no more models).
	 * 
	 * @param handle the target
	 * @return the model (it is NULL if there are no more models)
	 */
	public Pointer solveHandleModel(Pointer handle) {
		return BaseClingo.solveHandleModel(handle);
	}

	/**
	 * When a problem is unsatisfiable, get a subset of the assumptions that made
	 * the problem unsatisfiable.
	 * <p>
	 * If the program is not unsatisfiable, core is set to NULL and size to zero.
	 * //! @param[out] core pointer where to store the core //! @param[out] size
	 * size of the given array
	 * 
	 * @param handle the target
	 * @return
	 */
	public long[] solveHandleCore(Pointer handle) {
		return BaseClingo.solveHandleCore(handle);
	}

	/**
	 * Discards the last model and starts the search for the next one.
	 * <p>
	 * If the search has been started asynchronously, this function continues the
	 * search in the background.
	 * <p>
	 * 
	 * @note This function does not block.
	 * @param handle the target
	 */
	public void solveHandleResume(Pointer handle) {
		BaseClingo.solveHandleResume(handle);
	}

	/**
	 * Stop the running search and block until done.
	 * 
	 * @param handle the target
	 */
	public void solveHandleCancel(Pointer handle) {
		BaseClingo.solveHandleCancel(handle);
	}

	/**
	 * Stops the running search and releases the handle.
	 *
	 * Blocks until the search is stopped (as if an implicit cancel was called
	 * before the handle is released).
	 * 
	 * @param handle the target
	 */
	public void solveHandleClose(Pointer handle) {
		BaseClingo.solveHandleClose(handle);
	}

	/*
	 * ********** ast v2 **********
	 */

	// TODO: add AST

	/*
	 * *********************** ground program observer ***********************
	 */

	// clingo_ground_program_observer_t

	// TODO: add observer

	/*
	 * **************** extending clingo ****************
	 */

	// clingo_options_t
	// clingo_main_function_t
	// clingo_default_model_printer_t
	// clingo_model_printer_t
	// clingo_application_t

	/**
	 * Add an option that is processed with a custom parser.
	 * <p>
	 * Note that the parser also has to take care of storing the semantic value of
	 * the option somewhere.
	 * <p>
	 * Parameter option specifies the name(s) of the option. For example, "ping,p"
	 * adds the short option "-p" and its long form "--ping". It is also possible to
	 * associate an option with a help level by adding ",@l" to the option
	 * specification. OptionsSt with a level greater than zero are only shown if the
	 * argument to help is greater or equal to l.
	 *
	 * @param options     object to register the option with
	 * @param group       options are grouped into sections as given by this string
	 * @param option      specifies the command line option
	 * @param description the description of the option
	 * @param parse       callback to parse the value of the option
	 * @param data        user data for the callback
	 * @param multi       whether the option can appear multiple times on the
	 *                    command-line
	 * @param argument    optional string to change the value name in the generated
	 *                    help output
	 * @return
	 */
	public void optionsAdd(Pointer options, String group, String option, String description, OptionParseCallback parse,
			Pointer data, byte multi, String argument) {
		BaseClingo.optionsAdd(options, group, option, description, parse, data, multi, argument);
	}

	/**
	 * Add an option that is a simple flag.
	 * <p>
	 * This function is similar to @ref clingo_options_add() but simpler because it
	 * only supports flags, which do not have values. If a flag is passed via the
	 * command-line the parameter target is set to true.
	 *
	 * @param options     object to register the option with
	 * @param group       options are grouped into sections as given by this string
	 * @param option      specifies the command line option
	 * @param description the description of the option
	 * @param target      target boolean set to true if the flag is given on the
	 *                    command-line
	 */
	public void optionsAddFlag(Pointer options, String group, String option, String description, boolean target) {
		BaseClingo.optionsAddFlag(options, group, option, description, target);
	}

	/**
	 * Run clingo with a customized main function (similar to python and lua
	 * embedding).
	 *
	 * @param[in] application struct with callbacks to override default clingo
	 *            functionality
	 * @param[in] arguments command line arguments
	 * @param[in] size number of arguments
	 * @param[in] data user data to pass to callbacks in application
	 * @return exit code to return from main function
	 */
	public int main(Pointer application, String arguments, SizeT size, Pointer data) {
		return BaseClingo.main(application, arguments, size, data);
	}

}
