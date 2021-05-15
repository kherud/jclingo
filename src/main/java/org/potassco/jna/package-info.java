package org.potassco.jna;

/**
 * C basic Types
 * https://en.wikipedia.org/wiki/C_data_types
 * https://stackoverflow.com/questions/11438794/is-the-size-of-c-int-2-bytes-or-4-bytes
 * 
 * <ul>
 * <li>bool
 * <li>int: Basic signed integer type. Capable of containing at least the [−32,767, +32,767] range. 16 bit
 * <li>c_int
 * <li>size_t: Unsigned integer. 16 bit
 * <li>uint32_t
 * <li>int32_t
 * <li>uint64_t
 * <li>unsigned: Basic unsigned integer type. Contains at least the [0, 65,535] range. 16 bit
 * </ul>
 * 
 * 
 * Type dictionary and mapping
 * <ul>
 * <li>bool: byte
 * <li>size_t: long
 * <li>
 * <li>clingo_ast_attribute_t (int): int
 * <li>clingo_ast_attribute_type_t (int): int
 * <li>clingo_ast_type_t (int): int
 * <li>clingo_ast_unpool_type_bitset_t (int): int
 * <li>clingo_atom_t (uint32_t): int
 * <li>clingo_clause_type_t (int): int
 * <li>clingo_error_t (int): int
 * <li>clingo_external_type_t (int): int
 * <li>clingo_heuristic_type_t (int): int
 * <li>clingo_id_t (uint32_t): int
 * <li>clingo_literal_t (int32_t): int
 * <li>clingo_propagator_check_mode_t (int): int
 * <li>clingo_signature_t (uint64_t): long
 * <li>clingo_symbol_t (uint64_t): long
 * <li>clingo_symbolic_atom_iterator_t (uint64_t): long
 * <li>clingo_truth_value_t (int): int
 * <li>clingo_weight_constraint_type_t (int): int
 * <li>clingo_weight_t (int32_t): int
 * <li>
 * <li>
 * <li>
 * <li>clingo_assignment_t - struct
 * <li>clingo_ast_t - struct
 * <li>clingo_location_t - struct: LocationSt
 * <li>clingo_propagate_control_t - struct
 * <li>clingo_propagate_init_t - struct
 * <li>clingo_propagator_t - struct
 * <li>clingo_statistics_t - struct
 * <li>clingo_symbolic_atoms_t - struct
 * <li>clingo_weighted_literal_t - struct
 * <li>
 * <li>
 * <li>
 * </ul>
 */
