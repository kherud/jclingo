/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.potassco.clingo.internal;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.configuration.Configuration;
import org.potassco.clingo.configuration.ConfigurationType;
import org.potassco.clingo.control.*;
import org.potassco.clingo.solving.Observer;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.ast.AstCallback;
import org.potassco.clingo.solving.GroundCallback;
import org.potassco.clingo.control.ParseCallback;
import org.potassco.clingo.solving.SolveEventCallback;

public interface Clingo extends Library {

    Clingo INSTANCE = loadLibrary();

    static Clingo loadLibrary() {
//        Map<String, Object> options = Map.of(Library.OPTION_TYPE_MAPPER, new ClingoTypeMapper());
//        Native.setProtected(true);
        return Native.load("clingo", Clingo.class);  // options
    }

    static String getVersion() {
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        IntByReference revision = new IntByReference();
        INSTANCE.clingo_version(major, minor, revision);
        return String.format("%d.%d.%d", major.getValue(), minor.getValue(), revision.getValue());
    }

    static ErrorCode check(byte callSuccess) {
        if (callSuccess == 0) {
            String errorMessage = Clingo.INSTANCE.clingo_error_message();
            int errorId = Clingo.INSTANCE.clingo_error_code();
            ErrorCode errorCode = ErrorCode.fromValue(errorId);
            throw new RuntimeException(String.format("[%s] %s", errorCode.name(), errorMessage));
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * Convert error code into string.
     */
    String clingo_error_string(int code);

    /**
     * Get the last error code set by a clingo API call.
     * Each thread has its own local error code.
     *
     * @return error code
     */
    int clingo_error_code();

    /**
     * Get the last error message set if an API call fails.
     * Each thread has its own local error message.
     *
     * @return error message or NULL
     */
    String clingo_error_message();

    /**
     * Set a custom error code and message in the active thread.
     *
     * @param code    the error code
     * @param message the error message
     */
    void clingo_set_error(int code, String message);

    /**
     * Convert warning code into string.
     */
    // TODO: where do the warning codes come from?
    String clingo_warning_string(int code);

    /**
     * Obtain the clingo version.
     *
     * @param major    major version number
     * @param minor    minor version number
     * @param revision revision number
     */
    void clingo_version(IntByReference major, IntByReference minor, IntByReference revision);


    // SIGNATURE AND SYMBOLS

    // Signature Functions

    /**
     * Create a new signature.
     *
     * @param name      name of the signature
     * @param arity     arity of the signature
     * @param positive  false if the signature has a classical negation sign
     * @param signature the resulting signature
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_signature_create(String name, int arity, byte positive, LongByReference signature) throws LastErrorException;

    /**
     * Get the name of a signature.
     * <p>
     * The string is internalized and valid for the duration of the process.
     *
     * @param signature the target signature
     * @return the name of the signature
     */
    String clingo_signature_name(long signature);

    /**
     * Get the arity of a signature.
     *
     * @param signature the target signature
     * @return the arity of the signature
     */
    int clingo_signature_arity(long signature);

    /**
     * Whether the signature is positive (is not classically negated).
     *
     * @param signature the target signature
     * @return whether the signature has no sign
     */
    byte clingo_signature_is_positive(long signature);

    /**
     * Whether the signature is negative (is classically negated).
     *
     * @param signature the target signature
     * @return whether the signature has a sign
     */
    byte clingo_signature_is_negative(long signature);

    /**
     * Check if two signatures are equal.
     *
     * @param a first signature
     * @param b second signature
     * @return whether a == b
     */
    byte clingo_signature_is_equal_to(long a, long b);

    /**
     * Check if a signature is less than another signature.
     * <p>
     * Signatures are compared first by sign (int < signed), then by arity,
     * then by name.
     *
     * @param a first signature
     * @param b second signature
     * @return whether a < b
     */
    byte clingo_signature_is_less_than(long a, long b);

    /**
     * Calculate a hash code of a signature.
     *
     * @param signature the target signature
     * @return the hash code of the signature
     */
    NativeSize clingo_signature_hash(long signature);

    /**
     * Symbol Construction Functions
     * <p>
     * Construct a symbol representing a number.
     *
     * @param number the number
     * @param symbol the resulting symbol
     */
    void clingo_symbol_create_number(int number, LongByReference symbol);

    /**
     * Construct a symbol representing \#sup.
     *
     * @param symbol the resulting symbol
     */
    void clingo_symbol_create_supremum(LongByReference symbol);

    /**
     * Construct a symbol representing <tt>#inf</tt>.
     *
     * @param symbol the resulting symbol
     */
    void clingo_symbol_create_infimum(LongByReference symbol);

    /**
     * Construct a symbol representing a string.
     *
     * @param string the string
     * @param symbol the resulting symbol
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_symbol_create_string(String string, LongByReference symbol);

    /**
     * Construct a symbol representing an id.
     * <p>
     * This is just a shortcut for {@link Clingo#clingo_symbol_create_function} with
     * empty arguments.
     *
     * @param name     the name
     * @param positive whether the symbol has a classical negation sign
     * @param symbol   the resulting symbol
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_symbol_create_id(String name, byte positive, LongByReference symbol);

    /**
     * Construct a symbol representing a function or tuple.
     * <p>
     * To create tuples, the empty string has to be used as name.
     *
     * @param name           the name of the function
     * @param arguments      the arguments of the function
     * @param arguments_size the number of arguments
     * @param positive       whether the symbol has a classical negation sign
     * @param symbol         the resulting symbol
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_symbol_create_function(String name, long[] arguments, NativeSize arguments_size, byte positive, LongByReference symbol);

    // Symbol Inspection Functions

    /**
     * Get the number of a symbol.
     *
     * @param symbol the target symbol
     * @param number the resulting number
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if symbol is not of type {@link org.potassco.clingo.symbol.SymbolType#NUMBER}</li>
     * </ul>
     */
    byte clingo_symbol_number(long symbol, IntByReference number);

    /**
     * Get the name of a symbol.
     * <p>
     * The string is internalized and valid for the duration of the process.
     *
     * @param symbol the target symbol
     * @param name   the resulting name
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if symbol is not of type {@link org.potassco.clingo.symbol.SymbolType#FUNCTION}</li>
     * </ul>
     */
    byte clingo_symbol_name(long symbol, String[] name);

    /**
     * Get the string of a symbol.
     * <p>
     * The string is internalized and valid for the duration of the process.
     *
     * @param symbol the target symbol
     * @param string the resulting string
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if symbol is not of type {@link org.potassco.clingo.symbol.SymbolType#STRING}</li>
     * </ul>
     */
    byte clingo_symbol_string(long symbol, String[] string);

    /**
     * Check if a function is positive (does not have a sign).
     *
     * @param symbol   the target symbol
     * @param positive the result
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if symbol is not of type {@link org.potassco.clingo.symbol.SymbolType#FUNCTION}</li>
     * </ul>
     */
    byte clingo_symbol_is_positive(long symbol, ByteByReference positive);

    /**
     * Check if a function is negative (has a sign).
     *
     * @param symbol   the target symbol
     * @param negative the result
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if symbol is not of type {@link org.potassco.clingo.symbol.SymbolType#FUNCTION}</li>
     * </ul>
     */
    byte clingo_symbol_is_negative(long symbol, ByteByReference negative);

    /**
     * Get the arguments of a symbol.
     *
     * @param symbol         the target symbol
     * @param arguments      the resulting arguments
     * @param arguments_size the number of arguments
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if symbol is not of type {@link org.potassco.clingo.symbol.SymbolType#FUNCTION}</li>
     * </ul>
     */
    byte clingo_symbol_arguments(long symbol, PointerByReference arguments, NativeSizeByReference arguments_size);

    /**
     * Get the type of a symbol.
     *
     * @param symbol the target symbol
     * @return the type of the symbol
     */
    int clingo_symbol_type(long symbol);

    /**
     * Get the size of the string representation of a symbol (including the terminating 0).
     *
     * @param symbol the target symbol
     * @param size   the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_symbol_to_string_size(long symbol, NativeSizeByReference size);

    /**
     * Get the string representation of a symbol.
     *
     * @param symbol the target symbol
     * @param string the resulting string
     * @param size   the size of the string
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_symbol_to_string(long symbol, byte[] string, NativeSize size);

    /**
     * Symbol Comparison Functions
     * <p>
     * Check if two symbols are equal.
     *
     * @param a first symbol
     * @param b second symbol
     * @return whether a == b
     */
    byte clingo_symbol_is_equal_to(long a, long b);

    /**
     * Check if a symbol is less than another symbol.
     * <p>
     * Symbols are first compared by type.  If the types are equal, the values are
     * compared (where strings are compared using strcmp).  Functions are first
     * compared by signature and then lexicographically by arguments.
     *
     * @param a first symbol
     * @param b second symbol
     * @return whether a < b
     */
    byte clingo_symbol_is_less_than(long a, long b);

    /**
     * Calculate a hash code of a symbol.
     *
     * @param symbol the target symbol
     * @return the hash code of the symbol
     */
    NativeSize clingo_symbol_hash(long symbol);

    /**
     * Internalize a string.
     * <p>
     * This functions takes a string as input and returns an equal unique string
     * that is (at the moment) not freed until the program is closed.
     *
     * @param string the string to internalize
     * @param result the internalized string
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_add_string(String string, String[] result);

    /**
     * Parse a term in string form.
     * <p>
     * The result of this function is a symbol. The input term can contain
     * unevaluated functions, which are evaluated during parsing.
     *
     * @param string        the string to parse
     * @param logger        optional logger to report warnings during parsing
     * @param logger_data   user data for the logger
     * @param message_limit maximum number of times to call the logger
     * @param symbol        the resulting symbol
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if parsing fails</li>
     * </ul>
     */
    byte clingo_parse_term(String string, LoggerCallback logger, Pointer logger_data, int message_limit, LongByReference symbol);

    // symbolic atoms

    /**
     * Get the number of different atoms occurring in a logic program.
     *
     * @param atoms the target
     * @param size  the number of atoms
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_size(Pointer atoms, NativeSizeByReference size);

    /**
     * Get a forward iterator to the beginning of the sequence of all symbolic
     * atoms optionally restricted to a given signature.
     *
     * @param atoms     the target
     * @param signature optional signature
     * @param iterator  the resulting iterator
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_begin(Pointer atoms, LongByReference signature, LongByReference iterator);

    /**
     * Iterator pointing to the end of the sequence of symbolic atoms.
     *
     * @param atoms    the target
     * @param iterator the resulting iterator
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_end(Pointer atoms, LongByReference iterator);

    /**
     * Find a symbolic atom given its symbolic representation.
     *
     * @param atoms    the target
     * @param symbol   the symbol to lookup
     * @param iterator iterator pointing to the symbolic atom or to the end
     *                 of the sequence if no corresponding atom is found
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_find(Pointer atoms, long symbol, LongByReference iterator);

    /**
     * Check if two iterators point to the same element (or end of the sequence).
     *
     * @param atoms the target
     * @param a     the first iterator
     * @param b     the second iterator
     * @param equal whether the two iterators are equal
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_iterator_is_equal_to(Pointer atoms, long a, long b, ByteByReference equal);

    /**
     * Get the symbolic representation of an atom.
     *
     * @param atoms    the target
     * @param iterator iterator to the atom
     * @param symbol   the resulting symbol
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_symbol(Pointer atoms, long iterator, LongByReference symbol);

    /**
     * Check whether an atom is a fact.
     * <p>
     * This does not determine if an atom is a cautious consequence. The
     * grounding or solving component's simplifications can only detect this in
     * some cases.
     *
     * @param atoms    the target
     * @param iterator iterator to the atom
     * @param fact     whether the atom is a fact
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_is_fact(Pointer atoms, long iterator, ByteByReference fact);

    /**
     * Check whether an atom is external.
     * <p>
     * An atom is external if it has been defined using an external directive and
     * has not been released or defined by a rule.
     *
     * @param atoms    the target
     * @param iterator iterator to the atom
     * @param external whether the atom is a external
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_is_external(Pointer atoms, long iterator, ByteByReference external);

    /**
     * Returns the (numeric) aspif literal corresponding to the given symbolic atom.
     * <p>
     * Such a literal can be mapped to a solver literal (see the propagator
     * module) or be used in rules in aspif format (see the asp module).
     *
     * @param atoms    the target
     * @param iterator iterator to the atom
     * @param literal  the associated literal
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_literal(Pointer atoms, long iterator, IntByReference literal);

    /**
     * Get the number of different predicate signatures used in the program.
     *
     * @param atoms the target
     * @param size  the number of signatures
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_signatures_size(Pointer atoms, NativeSizeByReference size);

    /**
     * Get the predicate signatures occurring in a logic program.
     *
     * @param atoms      the target
     * @param signatures the resulting signatures
     * @param size       the number of signatures
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if the size is too small</li>
     * </ul>
     */
    byte clingo_symbolic_atoms_signatures(Pointer atoms, long[] signatures, NativeSize size);

    /**
     * Get an iterator to the next element in the sequence of symbolic atoms.
     *
     * @param atoms    the target
     * @param iterator the current iterator
     * @param next     the succeeding iterator
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_next(Pointer atoms, long iterator, LongByReference next);

    /**
     * Check whether the given iterator points to some element with the sequence
     * of symbolic atoms or to the end of the sequence.
     *
     * @param atoms    the target
     * @param iterator the iterator
     * @param valid    whether the iterator points to some element within the
     *                 sequence
     * @return whether the call was successful
     */
    byte clingo_symbolic_atoms_is_valid(Pointer atoms, long iterator, ByteByReference valid);

    // THEORY ATOMS

    // Theory Term Inspection

    /**
     * Get the type of the given theory term.
     *
     * @param atoms container where the term is stored
     * @param term  id of the term
     * @param type  the resulting type
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_term_type(Pointer atoms, int term, IntByReference type);

    /**
     * Get the number of the given numeric theory term.
     * <p>
     * The term must be of type {@link org.potassco.clingo.theory.TheoryTermType#NUMBER}.
     *
     * @param atoms  container where the term is stored
     * @param term   id of the term
     * @param number the resulting number
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_term_number(Pointer atoms, int term, IntByReference number);

    /**
     * Get the name of the given constant or function theory term.
     * <p>
     * The lifetime of the string is tied to the current solve step.
     * <p>
     * The term must be of type {@link org.potassco.clingo.theory.TheoryTermType#FUNCTION} or {@link org.potassco.clingo.theory.TheoryTermType#SYMBOL}.
     *
     * @param atoms container where the term is stored
     * @param term  id of the term
     * @param name  the resulting name
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_term_name(Pointer atoms, int term, String[] name);

    /**
     * Get the arguments of the given function theory term.
     * <p>
     * The term must be of type {@link org.potassco.clingo.theory.TheoryTermType#FUNCTION}.
     *
     * @param atoms     container where the term is stored
     * @param term      id of the term
     * @param arguments the resulting arguments in form of an array of term ids
     * @param size      the number of arguments
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_term_arguments(Pointer atoms, int term, PointerByReference arguments, NativeSizeByReference size);

    /**
     * Get the size of the string representation of the given theory term (including the terminating 0).
     *
     * @param atoms container where the term is stored
     * @param term  id of the term
     * @param size  the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_theory_atoms_term_to_string_size(Pointer atoms, int term, NativeSizeByReference size);

    /**
     * Get the string representation of the given theory term.
     *
     * @param atoms  container where the term is stored
     * @param term   id of the term
     * @param string the resulting string
     * @param size   the size of the string
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_theory_atoms_term_to_string(Pointer atoms, int term, byte[] string, NativeSize size);

    // Theory Element Inspection

    /**
     * Get the tuple (array of theory terms) of the given theory element.
     *
     * @param atoms   container where the element is stored
     * @param element id of the element
     * @param tuple   the resulting array of term ids
     * @param size    the number of term ids
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_element_tuple(Pointer atoms, int element, PointerByReference tuple, NativeSizeByReference size);

    /**
     * Get the condition (array of aspif literals) of the given theory element.
     *
     * @param atoms     container where the element is stored
     * @param element   id of the element
     * @param condition the resulting array of aspif literals
     * @param size      the number of term literals
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_element_condition(Pointer atoms, int element, PointerByReference condition, NativeSizeByReference size);

    /**
     * Get the id of the condition of the given theory element.
     * <p>
     * This id can be mapped to a solver literal using {@link Clingo#clingo_propagate_init_solver_literal}.
     * This id is not (necessarily) an aspif literal;
     * to get aspif literals use {@link Clingo#clingo_theory_atoms_element_condition}.
     *
     * @param atoms     container where the element is stored
     * @param element   id of the element
     * @param condition the resulting condition id
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_element_condition_id(Pointer atoms, int element, IntByReference condition);

    /**
     * Get the size of the string representation of the given theory element (including the terminating 0).
     *
     * @param atoms   container where the element is stored
     * @param element id of the element
     * @param size    the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_theory_atoms_element_to_string_size(Pointer atoms, int element, NativeSizeByReference size);

    /**
     * Get the string representation of the given theory element.
     *
     * @param atoms   container where the element is stored
     * @param element id of the element
     * @param string  the resulting string
     * @param size    the size of the string
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_theory_atoms_element_to_string(Pointer atoms, int element, byte[] string, NativeSize size);

    // Theory Atom Inspection

    /**
     * Get the total number of theory atoms.
     *
     * @param atoms the target
     * @param size  the resulting number
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_size(Pointer atoms, NativeSizeByReference size);

    /**
     * Get the theory term associated with the theory atom.
     *
     * @param atoms container where the atom is stored
     * @param atom  id of the atom
     * @param term  the resulting term id
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_atom_term(Pointer atoms, int atom, IntByReference term);

    /**
     * Get the theory elements associated with the theory atom.
     *
     * @param atoms    container where the atom is stored
     * @param atom     id of the atom
     * @param elements the resulting array of elements
     * @param size     the number of elements
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_atom_elements(Pointer atoms, int atom, PointerByReference elements, NativeSizeByReference size);

    /**
     * Whether the theory atom has a guard.
     *
     * @param atoms     container where the atom is stored
     * @param atom      id of the atom
     * @param has_guard whether the theory atom has a guard
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_atom_has_guard(Pointer atoms, int atom, ByteByReference has_guard);

    /**
     * Get the guard consisting of a theory operator and a theory term of the given theory atom.
     * <p>
     * The lifetime of the string is tied to the current solve step.
     *
     * @param atoms      container where the atom is stored
     * @param atom       id of the atom
     * @param connective the resulting theory operator
     * @param term       the resulting term
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_atom_guard(Pointer atoms, int atom, String[] connective, IntByReference term);

    /**
     * Get the aspif literal associated with the given theory atom.
     *
     * @param atoms   container where the atom is stored
     * @param atom    id of the atom
     * @param literal the resulting literal
     * @return whether the call was successful
     */
    byte clingo_theory_atoms_atom_literal(Pointer atoms, int atom, IntByReference literal);

    /**
     * Get the size of the string representation of the given theory atom (including the terminating 0).
     *
     * @param atoms container where the atom is stored
     * @param atom  id of the element
     * @param size  the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_theory_atoms_atom_to_string_size(Pointer atoms, int atom, NativeSizeByReference size);

    /**
     * Get the string representation of the given theory atom.
     *
     * @param atoms  container where the atom is stored
     * @param atom   id of the element
     * @param string the resulting string
     * @param size   the size of the string
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_theory_atoms_atom_to_string(Pointer atoms, int atom, byte[] string, NativeSize size);


    // PROPAGATOR

    // Assignment Functions

    /**
     * Get the current decision level.
     *
     * @param assignment the target assignment
     * @return the decision level
     */
    int clingo_assignment_decision_level(Pointer assignment);

    /**
     * Get the current root level.
     * <p>
     * Decisions levels smaller or equal to the root level are not backtracked during solving.
     *
     * @param assignment the target assignment
     * @return the decision level
     */
    int clingo_assignment_root_level(Pointer assignment);

    /**
     * Check if the given assignment is conflicting.
     *
     * @param assignment the target assignment
     * @return whether the assignment is conflicting
     */
    byte clingo_assignment_has_conflict(Pointer assignment);

    /**
     * Check if the given literal is part of a (partial) assignment.
     *
     * @param assignment the target assignment
     * @param literal    the literal
     * @return whether the literal is valid
     */
    byte clingo_assignment_has_literal(Pointer assignment, int literal);

    /**
     * Determine the decision level of a given literal.
     *
     * @param assignment the target assignment
     * @param literal    the literal
     * @param level      the resulting level
     * @return whether the call was successful
     */
    byte clingo_assignment_level(Pointer assignment, int literal, IntByReference level);

    /**
     * Determine the decision literal given a decision level.
     *
     * @param assignment the target assignment
     * @param level      the level
     * @param literal    the resulting literal
     * @return whether the call was successful
     */
    byte clingo_assignment_decision(Pointer assignment, int level, IntByReference literal);

    /**
     * Check if a literal has a fixed truth value.
     *
     * @param assignment the target assignment
     * @param literal    the literal
     * @param is_fixed   whether the literal is fixed
     * @return whether the call was successful
     */
    byte clingo_assignment_is_fixed(Pointer assignment, int literal, ByteByReference is_fixed);

    /**
     * Check if a literal is true.
     *
     * @param assignment the target assignment
     * @param literal    the literal
     * @param is_true    whether the literal is true
     * @return whether the call was successful
     */
    byte clingo_assignment_is_true(Pointer assignment, int literal, ByteByReference is_true);

    /**
     * Check if a literal has a fixed truth value.
     *
     * @param assignment the target assignment
     * @param literal    the literal
     * @param is_false   whether the literal is false
     * @return whether the call was successful
     */
    byte clingo_assignment_is_false(Pointer assignment, int literal, ByteByReference is_false);

    /**
     * Determine the truth value of a given literal.
     *
     * @param assignment the target assignment
     * @param literal    the literal
     * @param value      the resulting truth value
     * @return whether the call was successful
     */
    byte clingo_assignment_truth_value(Pointer assignment, int literal, IntByReference value);

    /**
     * The number of (positive) literals in the assignment.
     *
     * @param assignment the target
     * @return the number of literals
     */
    NativeSize clingo_assignment_size(Pointer assignment);

    /**
     * The (positive) literal at the given offset in the assignment.
     *
     * @param assignment the target
     * @param offset     the offset of the literal
     * @param literal    the literal
     * @return whether the call was successful
     */
    byte clingo_assignment_at(Pointer assignment, NativeSize offset, IntByReference literal);

    /**
     * Check if the assignment is total, i.e. there are no free literal.
     *
     * @param assignment the target
     * @return wheather the assignment is total
     */
    byte clingo_assignment_is_total(Pointer assignment);

    /**
     * Returns the number of literals in the trail, i.e., the number of assigned literals.
     *
     * @param assignment the target
     * @param size       the number of literals in the trail
     * @return whether the call was successful
     */
    byte clingo_assignment_trail_size(Pointer assignment, IntByReference size);

    /**
     * Returns the offset of the decision literal with the given decision level in
     * the trail.
     * <p>
     * Literals in the trail are ordered by decision levels, where the first
     * literal with a larger level than the previous literals is a decision; the
     * following literals with same level are implied by this decision literal.
     * Each decision level up to and including the current decision level has a
     * valid offset in the trail.
     *
     * @param assignment the target
     * @param level      the decision level
     * @param offset     the offset of the decision literal
     * @return whether the call was successful
     */
    byte clingo_assignment_trail_begin(Pointer assignment, int level, IntByReference offset);

    /**
     * Returns the offset following the last literal with the given decision level.
     * <p>
     * This function is the counter part to {@link Clingo#clingo_assignment_trail_begin}.
     *
     * @param assignment the target
     * @param level      the decision level
     * @param offset     the offset
     * @return whether the call was successful
     */
    byte clingo_assignment_trail_end(Pointer assignment, int level, IntByReference offset);

    /**
     * Returns the literal at the given position in the trail.
     *
     * @param assignment the target
     * @param offset     the offset of the literal
     * @param literal    the literal
     * @return whether the call was successful
     */
    byte clingo_assignment_trail_at(Pointer assignment, int offset, IntByReference literal);

    // Initialization Functions

    /**
     * Map the given program literal or condition id to its solver literal.
     *
     * @param init           the target
     * @param aspif_literal  the aspif literal to map
     * @param solver_literal the resulting solver literal
     * @return whether the call was successful
     */
    byte clingo_propagate_init_solver_literal(Pointer init, int aspif_literal, IntByReference solver_literal);

    /**
     * Add a watch for the solver literal in the given phase.
     *
     * @param init           the target
     * @param solver_literal the solver literal
     * @return whether the call was successful
     */
    byte clingo_propagate_init_add_watch(Pointer init, int solver_literal);

    /**
     * Add a watch for the solver literal in the given phase to the given solver thread.
     *
     * @param init           the target
     * @param solver_literal the solver literal
     * @param thread_id      the id of the solver thread
     * @return whether the call was successful
     */
    byte clingo_propagate_init_add_watch_to_thread(Pointer init, int solver_literal, int thread_id);

    /**
     * Remove the watch for the solver literal in the given phase.
     *
     * @param init           the target
     * @param solver_literal the solver literal
     * @return whether the call was successful
     */
    byte clingo_propagate_init_remove_watch(Pointer init, int solver_literal);

    /**
     * Remove the watch for the solver literal in the given phase from the given solver thread.
     *
     * @param init           the target
     * @param solver_literal the solver literal
     * @param thread_id      the id of the solver thread
     * @return whether the call was successful
     */
    byte clingo_propagate_init_remove_watch_from_thread(Pointer init, int solver_literal, int thread_id);

    /**
     * Freeze the given solver literal.
     * <p>
     * Any solver literal that is not frozen is subject to simplification and might be removed in a preprocessing step after propagator initialization.
     * A propagator should freeze all literals over which it might add clauses during propagation.
     * Note that any watched literal is automatically frozen and that it does not matter which phase of the literal is frozen.
     *
     * @param init           the target
     * @param solver_literal the solver literal
     * @return whether the call was successful
     */
    byte clingo_propagate_init_freeze_literal(Pointer init, int solver_literal);

    /**
     * Get an object to inspect the symbolic atoms.
     *
     * @param init  the target
     * @param atoms the resulting object
     * @return whether the call was successful
     */
    byte clingo_propagate_init_symbolic_atoms(Pointer init, PointerByReference atoms);

    /**
     * Get an object to inspect the theory atoms.
     *
     * @param init  the target
     * @param atoms the resulting object
     * @return whether the call was successful
     */
    byte clingo_propagate_init_theory_atoms(Pointer init, PointerByReference atoms);

    /**
     * Get the number of threads used in subsequent solving.
     *
     * @param init the target
     * @return the number of threads
     */
    int clingo_propagate_init_number_of_threads(Pointer init);

    /**
     * Configure when to call the check method of the propagator.
     *
     * @param init the target
     * @param mode bitmask when to call the propagator
     */
    void clingo_propagate_init_set_check_mode(Pointer init, int mode);

    /**
     * Get the current check mode of the propagator.
     *
     * @param init the target
     * @return bitmask when to call the propagator
     */
    int clingo_propagate_init_get_check_mode(Pointer init);

    /**
     * Get the top level assignment solver.
     *
     * @param init the target
     * @return the assignment
     */
    Pointer clingo_propagate_init_assignment(Pointer init);

    /**
     * Add a literal to the solver.
     * <p>
     * To be able to use the variable in clauses during propagation or add watches to it, it has to be frozen.
     * Otherwise, it might be removed during preprocessing.
     * <p>
     * If varibales were added, subsequent calls to functions adding constraints or {@link Clingo#clingo_propagate_init_propagate} are expensive.
     * It is best to add varables in batches.
     *
     * @param init   the target
     * @param freeze whether to freeze the literal
     * @param result the added literal
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_propagate_init_add_literal(Pointer init, byte freeze, IntByReference result);

    /**
     * Add the given clause to the solver.
     * <p>
     * No further calls on the init object or functions on the assignment should be called when the result of this method is false.
     *
     * @param init   the target
     * @param clause the clause to add
     * @param size   the size of the clause
     * @param result result indicating whether the problem became unsatisfiable
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_propagate_init_add_clause(Pointer init, int[] clause, NativeSize size, ByteByReference result);

    /**
     * Add the given weight constraint to the solver.
     * <p>
     * This function adds a constraint of form `literal <=> { lit=weight | (lit, weight) in literals } >= bound` to the solver.
     * Depending on the type the `<=>` connective can be either a left implication, right implication, or equivalence.
     * <p>
     * No further calls on the init object or functions on the assignment should be called when the result of this method is false.
     *
     * @param init          the target
     * @param literal       the literal of the constraint
     * @param literals      the weighted literals
     * @param size          the number of weighted literals
     * @param bound         the bound of the constraint
     * @param type          the type of the weight constraint
     * @param compare_equal if true compare equal instead of less than equal
     * @param result        result indicating whether the problem became unsatisfiable
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_propagate_init_add_weight_constraint(Pointer init, int literal, WeightedLiteral[] literals, NativeSize size, int bound, int type, byte compare_equal, ByteByReference result);

    /**
     * Add the given literal to minimize to the solver.
     * <p>
     * This corresponds to a weak constraint of form `:~ literal. [weight@priority]`.
     *
     * @param init     the target
     * @param literal  the literal to minimize
     * @param weight   the weight of the literal
     * @param priority the priority of the literal
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_propagate_init_add_minimize(Pointer init, int literal, int weight, int priority);

    /**
     * Propagates consequences of the underlying problem excluding registered propagators.
     * <p>
     * The function has no effect if SAT-preprocessing is enabled.
     * No further calls on the init object or functions on the assignment should be called when the result of this method is false.
     *
     * @param init   the target
     * @param result result indicating whether the problem became unsatisfiable
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_propagate_init_propagate(Pointer init, ByteByReference result);

    // Propagation Functions

    /**
     * Get the id of the underlying solver thread.
     * <p>
     * Thread ids are consecutive numbers starting with zero.
     *
     * @param control the target
     * @return the thread id
     */
    int clingo_propagate_control_thread_id(Pointer control);

    /**
     * Get the assignment associated with the underlying solver.
     *
     * @param control the target
     * @return the assignment
     */
    Pointer clingo_propagate_control_assignment(Pointer control);

    /**
     * Adds a new volatile literal to the underlying solver thread.
     * <p>
     * The literal is only valid within the current solving step and solver thread.
     * All volatile literals and clauses involving a volatile literal are deleted after the current search.
     *
     * @param control the target
     * @param result  the (positive) solver literal
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#LOGIC} if the assignment is conflicting</li>
     * </ul>
     */
    byte clingo_propagate_control_add_literal(Pointer control, IntByReference result);

    /**
     * Add a watch for the solver literal in the given phase.
     * <p>
     * Unlike {@link Clingo#clingo_propagate_init_add_watch} this does not add a watch to all solver threads but just the current one.
     *
     * @param control the target
     * @param literal the literal to watch
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#LOGIC} if the literal is invalid</li>
     * </ul>
     */
    byte clingo_propagate_control_add_watch(Pointer control, int literal);

    /**
     * Check whether a literal is watched in the current solver thread.
     *
     * @param control the target
     * @param literal the literal to check
     * @return whether the literal is watched
     */
    byte clingo_propagate_control_has_watch(Pointer control, int literal);

    /**
     * Removes the watch (if any) for the given solver literal.
     * <p>
     * Similar to {@link Clingo#clingo_propagate_init_add_watch} this just removes the watch in the current solver thread.
     *
     * @param control the target
     * @param literal the literal to remove
     */
    void clingo_propagate_control_remove_watch(Pointer control, int literal);

    /**
     * Add the given clause to the solver.
     * <p>
     * This method sets its result to false if the current propagation must be stopped for the solver to backtrack.
     * <p>
     * No further calls on the control object or functions on the assignment should be called when the result of this method is false.
     *
     * @param control the target
     * @param clause  the clause to add
     * @param size    the size of the clause
     * @param type    the clause type determining its lifetime
     * @param result  result indicating whether propagation has to be stopped
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_propagate_control_add_clause(Pointer control, int[] clause, NativeSize size, int type, ByteByReference result);

    /**
     * Propagate implied literals (resulting from added clauses).
     * <p>
     * This method sets its result to false if the current propagation must be stopped for the solver to backtrack.
     * <p>
     * No further calls on the control object or functions on the assignment should be called when the result of this method is false.
     *
     * @param control the target
     * @param result  result indicating whether propagation has to be stopped
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_propagate_control_propagate(Pointer control, ByteByReference result);

    // backend

    /**
     * Prepare the backend for usage.
     *
     * @param backend the target backend
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_backend_begin(Pointer backend);

    /**
     * Finalize the backend after using it.
     *
     * @param backend the target backend
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_backend_end(Pointer backend);

    /**
     * Add a rule to the program.
     *
     * @param backend   the target backend
     * @param choice    determines if the head is a choice or a disjunction
     * @param head      the head atoms
     * @param head_size the number of atoms in the head
     * @param body      the body literals
     * @param body_size the number of literals in the body
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_rule(Pointer backend, byte choice, int[] head, NativeSize head_size, int[] body, NativeSize body_size);

    /**
     * Add a weight rule to the program.
     * <p>
     * All weights and the lower bound must be positive.
     *
     * @param backend     the target backend
     * @param choice      determines if the head is a choice or a disjunction
     * @param head        the head atoms
     * @param head_size   the number of atoms in the head
     * @param lower_bound the lower bound of the weight rule
     * @param body        the weighted body literals
     * @param body_size   the number of weighted literals in the body
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_weight_rule(Pointer backend, byte choice, int[] head, NativeSize head_size, int lower_bound, WeightedLiteral[] body, NativeSize body_size);

    /**
     * Add a minimize constraint (or weak constraint) to the program.
     *
     * @param backend  the target backend
     * @param priority the priority of the constraint
     * @param literals the weighted literals whose sum to minimize
     * @param size     the number of weighted literals
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_minimize(Pointer backend, int priority, WeightedLiteral[] literals, NativeSize size);

    /**
     * Add a projection directive.
     *
     * @param backend the target backend
     * @param atoms   the atoms to project on
     * @param size    the number of atoms
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_project(Pointer backend, int[] atoms, NativeSize size);

    /**
     * Add an external statement.
     *
     * @param backend the target backend
     * @param atom    the external atom
     * @param type    the type of the external statement
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_external(Pointer backend, int atom, int type);

    /**
     * Add an assumption directive.
     *
     * @param backend  the target backend
     * @param literals the literals to assume (positive literals are true and negative literals false for the next solve call)
     * @param size     the number of atoms
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_assume(Pointer backend, int[] literals, NativeSize size);

    /**
     * Add an heuristic directive.
     *
     * @param backend   the target backend
     * @param atom      the target atom
     * @param type      the type of the heuristic modification
     * @param bias      the heuristic bias
     * @param priority  the heuristic priority
     * @param condition the condition under which to apply the heuristic modification
     * @param size      the number of atoms in the condition
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_heuristic(Pointer backend, int atom, int type, int bias, int priority, int[] condition, NativeSize size);

    /**
     * Add an edge directive.
     *
     * @param backend   the target backend
     * @param node_u    the start vertex of the edge
     * @param node_v    the end vertex of the edge
     * @param condition the condition under which the edge is part of the graph
     * @param size      the number of atoms in the condition
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_backend_acyc_edge(Pointer backend, int node_u, int node_v, int[] condition, NativeSize size);

    /**
     * Get a fresh atom to be used in aspif directives.
     *
     * @param backend the target backend
     * @param symbol  optional symbol to associate the atom with
     * @param atom    the resulting atom
     * @return whether the call was successful
     */
    byte clingo_backend_add_atom(Pointer backend, LongByReference symbol, IntByReference atom);

    // configuration

    /**
     * Get the root key of the configuration.
     *
     * @param configuration the target configuration
     * @param key           the root key
     * @return whether the call was successful
     */
    byte clingo_configuration_root(Pointer configuration, IntByReference key);

    /**
     * Get the type of a key.
     * <p>
     * The type is bitset, an entry can have multiple (but at least one) type.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param type          the resulting type
     * @return whether the call was successful
     */
    byte clingo_configuration_type(Pointer configuration, int key, IntByReference type);

    /**
     * Get the description of an entry.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param description   the description
     * @return whether the call was successful
     */
    byte clingo_configuration_description(Pointer configuration, int key, String[] description);

    // Functions to access arrays

    /**
     * Get the size of an array entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#ARRAY}.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param size          the resulting size
     * @return whether the call was successful
     */
    byte clingo_configuration_array_size(Pointer configuration, int key, NativeSizeByReference size);

    /**
     * Get the subkey at the given offset of an array entry.
     * <p>
     * Some array entries, like fore example the solver configuration, can be accessed past there actual size to add subentries.
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#ARRAY}.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param offset        the offset in the array
     * @param subkey        the resulting subkey
     * @return whether the call was successful
     */
    byte clingo_configuration_array_at(Pointer configuration, int key, NativeSize offset, IntByReference subkey);

    // Functions to access maps

    /**
     * Get the number of subkeys of a map entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#MAP}.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param size          the resulting number
     * @return whether the call was successful
     */
    byte clingo_configuration_map_size(Pointer configuration, int key, NativeSizeByReference size);

    /**
     * Query whether the map has a key.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#MAP}.
     * Multiple levels can be looked up by concatenating keys with a period.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param name          the name to lookup the subkey
     * @param result        whether the key is in the map
     * @return whether the call was successful
     */
    byte clingo_configuration_map_has_subkey(Pointer configuration, int key, String name, ByteByReference result);

    /**
     * Get the name associated with the offset-th subkey.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#MAP}.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param offset        the offset of the name
     * @param name          the resulting name
     * @return whether the call was successful
     */
    byte clingo_configuration_map_subkey_name(Pointer configuration, int key, NativeSize offset, String[] name);

    /**
     * Lookup a subkey under the given name.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#MAP}.
     * Multiple levels can be looked up by concatenating keys with a period.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param name          the name to lookup the subkey
     * @param subkey        the resulting subkey
     * @return whether the call was successful
     */
    byte clingo_configuration_map_at(Pointer configuration, int key, String name, IntByReference subkey);

    // Functions to access values

    /**
     * Check whether an entry has a value.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param assigned      whether the entry has a value
     * @return whether the call was successful
     */
    byte clingo_configuration_value_is_assigned(Pointer configuration, int key, ByteByReference assigned);

    /**
     * Get the size of the string value of the given entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param size          the resulting size
     * @return whether the call was successful
     */
    byte clingo_configuration_value_get_size(Pointer configuration, int key, NativeSizeByReference size);

    /**
     * Get the string value of the given entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     * The given size must be larger or equal to size of the value.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param value         the resulting string value
     * @param size          the size of the given char array
     * @return whether the call was successful
     */
    byte clingo_configuration_value_get(Pointer configuration, int key, byte[] value, NativeSize size);

    /**
     * Set the value of an entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     *
     * @param configuration the target configuration
     * @param key           the key
     * @param value         the value to set
     * @return whether the call was successful
     */
    byte clingo_configuration_value_set(Pointer configuration, int key, String value);

    // statistics

    /**
     * Get the root key of the statistics.
     *
     * @param statistics the target statistics
     * @param key        the root key
     * @return whether the call was successful
     */
    byte clingo_statistics_root(Pointer statistics, LongByReference key);

    /**
     * Get the type of a key.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param type       the resulting type
     * @return whether the call was successful
     */
    byte clingo_statistics_type(Pointer statistics, long key, IntByReference type);

    // Functions to access arrays

    /**
     * Get the size of an array entry.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#ARRAY}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param size       the resulting size
     * @return whether the call was successful
     */
    byte clingo_statistics_array_size(Pointer statistics, long key, NativeSizeByReference size);

    /**
     * Get the subkey at the given offset of an array entry.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#ARRAY}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param offset     the offset in the array
     * @param subkey     the resulting subkey
     * @return whether the call was successful
     */
    byte clingo_statistics_array_at(Pointer statistics, long key, NativeSize offset, LongByReference subkey);

    /**
     * Create the subkey at the end of an array entry.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#ARRAY}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param type       the type of the new subkey
     * @param subkey     the resulting subkey
     * @return whether the call was successful
     */
    byte clingo_statistics_array_push(Pointer statistics, long key, int type, LongByReference subkey);

    // Functions to access maps

    /**
     * Get the number of subkeys of a map entry.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#MAP}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param size       the resulting number
     * @return whether the call was successful
     */
    byte clingo_statistics_map_size(Pointer statistics, long key, NativeSizeByReference size);

    /**
     * Test if the given map contains a specific subkey.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#MAP}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param name       name of the subkey
     * @param result     true if the map has a subkey with the given name
     * @return whether the call was successful
     */
    byte clingo_statistics_map_has_subkey(Pointer statistics, long key, String name, ByteByReference result);

    /**
     * Get the name associated with the offset-th subkey.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#MAP}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param offset     the offset of the name
     * @param name       the resulting name
     * @return whether the call was successful
     */
    byte clingo_statistics_map_subkey_name(Pointer statistics, long key, NativeSize offset, String[] name);

    /**
     * Lookup a subkey under the given name.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#MAP}.
     * Multiple levels can be looked up by concatenating keys with a period.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param name       the name to lookup the subkey
     * @param subkey     the resulting subkey
     * @return whether the call was successful
     */
    byte clingo_statistics_map_at(Pointer statistics, long key, String name, LongByReference subkey);

    /**
     * Add a subkey with the given name.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#MAP}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param name       the name of the new subkey
     * @param type       the type of the new subkey
     * @param subkey     the index of the resulting subkey
     * @return whether the call was successful
     */
    byte clingo_statistics_map_add_subkey(Pointer statistics, long key, String name, int type, LongByReference subkey);

    // Functions to inspect and change values

    /**
     * Get the value of the given entry.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#VALUE}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param value      the resulting value
     * @return whether the call was successful
     */
    byte clingo_statistics_value_get(Pointer statistics, long key, DoubleByReference value);

    /**
     * Set the value of the given entry.
     * <p>
     * The {@link org.potassco.clingo.statistics.StatisticsType type} of the entry must be {@link org.potassco.clingo.statistics.StatisticsType#VALUE}.
     *
     * @param statistics the target statistics
     * @param key        the key
     * @param value      the new value
     * @return whether the call was successful
     */
    byte clingo_statistics_value_set(Pointer statistics, long key, double value);

    // MODEL AND SOLVE CONTROL

    //  Functions for Inspecting Models

    /**
     * Get the type of the model.
     *
     * @param model the target
     * @param type  the type of the model
     * @return whether the call was successful
     */
    byte clingo_model_type(Pointer model, IntByReference type);

    /**
     * Get the running number of the model.
     *
     * @param model  the target
     * @param number the number of the model
     * @return whether the call was successful
     */
    byte clingo_model_number(Pointer model, LongByReference number);

    /**
     * Get the number of symbols of the selected types in the model.
     *
     * @param model the target
     * @param show  which symbols to select
     * @param size  the number symbols
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_model_symbols_size(Pointer model, int show, NativeSizeByReference size);

    /**
     * Get the symbols of the selected types in the model.
     * <p>
     * CSP assignments are represented using functions with name "$"
     * where the first argument is the name of the CSP variable and the second one its
     * value.
     *
     * @param model   the target
     * @param show    which symbols to select
     * @param symbols the resulting symbols
     * @param size    the number of selected symbols
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if the size is too small</li>
     * </ul>
     */
    byte clingo_model_symbols(Pointer model, int show, long[] symbols, NativeSize size);

    /**
     * Constant time lookup to test whether an atom is in a model.
     *
     * @param model     the target
     * @param atom      the atom to lookup
     * @param contained whether the atom is contained
     * @return whether the call was successful
     */
    byte clingo_model_contains(Pointer model, long atom, ByteByReference contained);

    /**
     * Check if a program literal is true in a model.
     *
     * @param model   the target
     * @param literal the literal to lookup
     * @param result  whether the literal is true
     * @return whether the call was successful
     */
    byte clingo_model_is_true(Pointer model, int literal, ByteByReference result);

    /**
     * Get the number of cost values of a model.
     *
     * @param model the target
     * @param size  the number of costs
     * @return whether the call was successful
     */
    byte clingo_model_cost_size(Pointer model, NativeSizeByReference size);

    /**
     * Get the cost vector of a model.
     *
     * @param model the target
     * @param costs the resulting costs
     * @param size  the number of costs
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if the size is too small</li>
     * </ul>
     */
    byte clingo_model_cost(Pointer model, LongByReference costs, NativeSize size);

    /**
     * Whether the optimality of a model has been proven.
     *
     * @param model  the target
     * @param proven whether the optimality has been proven
     * @return whether the call was successful
     */
    byte clingo_model_optimality_proven(Pointer model, ByteByReference proven);

    /**
     * Get the id of the solver thread that found the model.
     *
     * @param model the target
     * @param id    the resulting thread id
     * @return whether the call was successful
     */
    byte clingo_model_thread_id(Pointer model, IntByReference id);

    /**
     * Add symbols to the model.
     * <p>
     * These symbols will appear in clingo's output, which means that this
     * function is only meaningful if there is an underlying clingo application.
     * Only models passed to the {@link SolveEventCallback} are extendable.
     *
     * @param model   the target
     * @param symbols the symbols to add
     * @param size    the number of symbols to add
     * @return whether the call was successful
     */
    byte clingo_model_extend(Pointer model, long[] symbols, NativeSize size);

    // Functions for Adding Clauses

    /**
     * Get the associated solve control object of a model.
     * <p>
     * This object allows for adding clauses during model enumeration.
     *
     * @param model   the target
     * @param control the resulting solve control object
     * @return whether the call was successful
     */
    byte clingo_model_context(Pointer model, PointerByReference control);

    /**
     * Get an object to inspect the symbolic atoms.
     *
     * @param control the target
     * @param atoms   the resulting object
     * @return whether the call was successful
     */
    byte clingo_solve_control_symbolic_atoms(Pointer control, PointerByReference atoms);

    /**
     * Add a clause that applies to the current solving step during model
     * enumeration.
     * <p>
     * The {@link Propagator} module provides a more sophisticated
     * interface to add clauses - even on partial assignments.
     *
     * @param control the target
     * @param clause  array of literals representing the clause
     * @param size    the size of the literal array
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if adding the clause fails</li>
     * </ul>
     */
    byte clingo_solve_control_add_clause(Pointer control, int[] clause, NativeSize size);


    // SOLVE RESULT

    /**
     * Get the next solve result.
     * <p>
     * Blocks until the result is ready.
     * When yielding partial solve results can be obtained, i.e.,
     * when a model is ready, the result will be satisfiable but neither the search exhausted nor the optimality proven.
     *
     * @param handle the target
     * @param result the solve result
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if solving fails</li>
     * </ul>
     */
    byte clingo_solve_handle_get(Pointer handle, IntByReference result);

    /**
     * Wait for the specified amount of time to check if the next result is ready.
     * <p>
     * If the time is set to zero, this function can be used to poll if the search is still active.
     * If the time is negative, the function blocks until the search is finished.
     *
     * @param handle  the target
     * @param timeout the maximum time to wait
     * @param result  whether the search has finished
     */
    void clingo_solve_handle_wait(Pointer handle, double timeout, ByteByReference result);

    /**
     * Get the next model (or zero if there are no more models).
     *
     * @param handle the target
     * @param model  the model (it is NULL if there are no more models)
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if solving fails</li>
     * </ul>
     */
    byte clingo_solve_handle_model(Pointer handle, PointerByReference model);

    /**
     * When a problem is unsatisfiable, get a subset of the assumptions that made the problem unsatisfiable.
     * <p>
     * If the program is not unsatisfiable, core is set to NULL and size to zero.
     *
     * @param handle the target
     * @param core   pointer where to store the core
     * @param size   size of the given array
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_solve_handle_core(Pointer handle, PointerByReference core, NativeSizeByReference size);

    /**
     * Discards the last model and starts the search for the next one.
     * <p>
     * If the search has been started asynchronously, this function continues the search in the background.
     * <p>
     * This function does not block.
     *
     * @param handle the target
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if solving fails</li>
     * </ul>
     */
    byte clingo_solve_handle_resume(Pointer handle);

    /**
     * Stop the running search and block until done.
     *
     * @param handle the target
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if solving fails</li>
     * </ul>
     */
    byte clingo_solve_handle_cancel(Pointer handle);

    /**
     * Stops the running search and releases the handle.
     * <p>
     * Blocks until the search is stopped (as if an implicit cancel was called before the handle is released).
     *
     * @param handle the target
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if solving fails</li>
     * </ul>
     */
    byte clingo_solve_handle_close(Pointer handle);

    //  Functions to construct ASTs

    /**
     * Construct an AST of the given type.
     * <p>
     * The arguments corresponding to the given type can be inspected using "g_clingo_ast_constructors.constructors[type]".
     *
     * @param type the type of AST to construct
     * @param ast  the resulting AST
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if one of the arguments is incompatible with the type</li>
     * </ul>
     */
    byte clingo_ast_build(int type, PointerByReference ast, Object... arguments);


    //  Functions to manage life time of ASTs

    /**
     * Increment the reference count of an AST node.
     * <p>
     * All functions that return AST nodes already increment the reference count.
     * The reference count of callback arguments is not incremented.
     *
     * @param ast the target AST
     */
    void clingo_ast_acquire(Pointer ast);

    /**
     * Decrement the reference count of an AST node.
     * <p>
     * The node is deleted if the reference count reaches zero.
     *
     * @param ast the target AST
     */
    void clingo_ast_release(Pointer ast);


    //  Functions to copy ASTs

    /**
     * Deep copy an AST node.
     *
     * @param ast  the AST to copy
     * @param copy the resulting AST
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_copy(Pointer ast, PointerByReference copy);

    /**
     * Create a shallow copy of an AST node.
     *
     * @param ast  the AST to copy
     * @param copy the resulting AST
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_deep_copy(Pointer ast, PointerByReference copy);


    // Functions to compare ASTs

    /**
     * Less than compare two AST nodes.
     *
     * @param a the left-hand-side AST
     * @param b the right-hand-side AST
     * @return the result of the comparison
     */
    byte clingo_ast_less_than(Pointer a, Pointer b);

    /**
     * Equality compare two AST nodes.
     *
     * @param a the left-hand-side AST
     * @param b the right-hand-side AST
     * @return the result of the comparison
     */
    byte clingo_ast_equal(Pointer a, Pointer b);

    /**
     * Compute a hash for an AST node.
     *
     * @param ast the target AST
     * @return the resulting hash code
     */
    NativeSize clingo_ast_hash(Pointer ast);


    // Functions to get convert ASTs to strings

    /**
     * Get the size of the string representation of an AST node.
     *
     * @param ast  the target AST
     * @param size the size of the string representation
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_to_string_size(Pointer ast, NativeSizeByReference size);

    /**
     * Get the string representation of an AST node.
     *
     * @param ast    the target AST
     * @param string the string representation
     * @param size   the size of the string representation
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_to_string(Pointer ast, byte[] string, NativeSize size);


    //  Functions to inspect ASTs

    /**
     * Get the type of an AST node.
     *
     * @param ast  the target AST
     * @param type the resulting type
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_get_type(Pointer ast, IntByReference type);

    /**
     * Check if an AST has the given attribute.
     *
     * @param ast           the target AST
     * @param attribute     the attribute to check
     * @param has_attribute the result
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_has_attribute(Pointer ast, int attribute, ByteByReference has_attribute);

    /**
     * Get the type of the given AST.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param type      the resulting type
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_type(Pointer ast, int attribute, IntByReference type);


    //  Functions to get/set numeric attributes of ASTs

    /**
     * Get the value of an attribute of type "clingo_ast_attribute_type_number".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the resulting value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_number(Pointer ast, int attribute, IntByReference value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_number".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_number(Pointer ast, int attribute, int value);


    // Functions to get/set symbolic attributes of ASTs

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_number".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_symbol(Pointer ast, int attribute, LongByReference value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_symbol".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_symbol(Pointer ast, int attribute, long value);


    // Functions to get/set location attributes of ASTs

    /**
     * Get the value of an attribute of type "clingo_ast_attribute_type_location".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the resulting value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_location(Pointer ast, int attribute, Location value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_location".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_location(Pointer ast, int attribute, Location value);


    // Functions to get/set string attributes of ASTs

    /**
     * Get the value of an attribute of type "clingo_ast_attribute_type_string".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the resulting value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_string(Pointer ast, int attribute, String[] value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_string".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_string(Pointer ast, int attribute, String value);


    // Functions to get/set AST attributes of ASTs

    /**
     * Get the value of an attribute of type "clingo_ast_attribute_type_ast".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the resulting value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_ast(Pointer ast, int attribute, PointerByReference value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_ast".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_ast(Pointer ast, int attribute, Pointer value);


    // Functions to get/set optional AST attributes of ASTs

    /**
     * Get the value of an attribute of type "clingo_ast_attribute_type_optional_ast".
     * <p>
     * The value might be "NULL".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the resulting value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_optional_ast(Pointer ast, int attribute, PointerByReference value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_optional_ast".
     * <p>
     * The value might be "NULL".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_optional_ast(Pointer ast, int attribute, Pointer value);


    // Functions to get/set string array attributes of ASTs

    /**
     * Get the value of an attribute of type "clingo_ast_attribute_type_string_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @param value     the resulting value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_string_at(Pointer ast, int attribute, NativeSize index, String[] value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_string_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_string_at(Pointer ast, int attribute, NativeSize index, String value);

    /**
     * Remove an element from an attribute of type "clingo_ast_attribute_type_string_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_delete_string_at(Pointer ast, int attribute, NativeSize index);

    /**
     * Get the size of an attribute of type "clingo_ast_attribute_type_string_array".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param size      the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_size_string_array(Pointer ast, int attribute, NativeSizeByReference size);

    /**
     * Insert a value into an attribute of type "clingo_ast_attribute_type_string_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_attribute_insert_string_at(Pointer ast, int attribute, NativeSize index, String value);

    // Functions to get/set AST array attributes of ASTs

    /**
     * Get the value of an attribute of type "clingo_ast_attribute_type_ast_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @param value     the resulting value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_get_ast_at(Pointer ast, int attribute, NativeSize index, PointerByReference value);

    /**
     * Set the value of an attribute of type "clingo_ast_attribute_type_ast_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_attribute_set_ast_at(Pointer ast, int attribute, NativeSize index, Pointer value);

    /**
     * Remove an element from an attribute of type "clingo_ast_attribute_type_ast_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_delete_ast_at(Pointer ast, int attribute, NativeSize index);

    /**
     * Get the size of an attribute of type "clingo_ast_attribute_type_ast_array".
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param size      the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME}</li>
     * </ul>
     */
    byte clingo_ast_attribute_size_ast_array(Pointer ast, int attribute, NativeSizeByReference size);

    /**
     * Insert a value into an attribute of type "clingo_ast_attribute_type_ast_array" at the given index.
     *
     * @param ast       the target AST
     * @param attribute the target attribute
     * @param index     the target index
     * @param value     the value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_attribute_insert_ast_at(Pointer ast, int attribute, NativeSize index, Pointer value);

    //  Functions to construct ASTs from strings

    /**
     * Parse the given program and return an abstract syntax tree for each statement via a callback.
     *
     * @param program       the program in gringo syntax
     * @param callback      the callback reporting statements
     * @param callback_data user data for the callback
     * @param logger        callback to report messages during parsing
     * @param logger_data   user data for the logger
     * @param message_limit the maximum number of times the logger is called
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_parse_string(String program, AstCallback callback, Pointer callback_data, LoggerCallback logger, Pointer logger_data, int message_limit);

    /**
     * Parse the programs in the given list of files and return an abstract syntax tree for each statement via a callback.
     * <p>
     * The function follows clingo's handling of files on the command line.
     * Filename "-" is treated as "STDIN" and if an empty list is given, then the parser will read from "STDIN".
     *
     * @param files         the beginning of the file name array
     * @param size          the number of file names
     * @param callback      the callback reporting statements
     * @param callback_data user data for the callback
     * @param logger        callback to report messages during parsing
     * @param logger_data   user data for the logger
     * @param message_limit the maximum number of times the logger is called
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_parse_files(String[] files, NativeSize size, AstCallback callback, Pointer callback_data, LoggerCallback logger, Pointer logger_data, int message_limit);

    // Functions to add ASTs to logic programs

    /**
     * Begin building a program.
     *
     * @param builder the target program builder
     * @return whether the call was successful
     */
    byte clingo_program_builder_begin(Pointer builder);

    /**
     * End building a program.
     *
     * @param builder the target program builder
     * @return whether the call was successful
     */
    byte clingo_program_builder_end(Pointer builder);

    /**
     * Adds a statement to the program.
     *
     * @param builder the target program builder
     * @param ast     the AST node to add
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     * {@link Clingo#clingo_program_builder_begin} must be called before adding statements and {@link Clingo#clingo_program_builder_end} must be called after all statements have been added.
     */
    byte clingo_program_builder_add(Pointer builder, Pointer ast);

    // Functions to unpool ASts

    /**
     * Unpool the given AST.
     *
     * @param ast           the target AST
     * @param unpool_type   what to unpool
     * @param callback      the callback to report ASTs
     * @param callback_data user data for the callback
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_ast_unpool(Pointer ast, int unpool_type, AstCallback callback, Pointer callback_data);

    // ground program observer

    /**
     * Create a new control object.
     * <p>
     * A control object has to be freed using {@link Clingo#clingo_control_free}.
     * <p>
     * Only gringo options (without <code>--output</code>) and clasp's options are supported as arguments,
     * except basic options such as <code>--help</code>.
     * Furthermore, a control object is blocked while a search call is active;
     * you must not call any member function during search.
     * <p>
     * If the logger is NULL, messages are printed to stderr.
     *
     * @param arguments      C string array of command line arguments
     * @param arguments_size size of the arguments array
     * @param logger         callback functions for warnings and info messages
     * @param logger_data    user data for the logger callback
     * @param message_limit  maximum number of times the logger callback is called
     * @param control        resulting control object
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if argument parsing fails</li>
     * </ul>
     */
    byte clingo_control_new(String[] arguments, NativeSize arguments_size, LoggerCallback logger, Pointer logger_data, int message_limit, PointerByReference control);

    /**
     * Free a control object created with {@link Clingo#clingo_control_new}.
     *
     * @param control the target
     */
    void clingo_control_free(Pointer control);

    // Grounding Functions

    /**
     * Extend the logic program with a program in a file.
     *
     * @param control the target
     * @param file    path to the file
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if parsing or checking fails</li>
     * </ul>
     */
    byte clingo_control_load(Pointer control, String file);

    /**
     * Extend the logic program with the given non-ground logic program in string form.
     * <p>
     * This function puts the given program into a block of form: <tt>#program name(parameters).</tt>
     * <p>
     * After extending the logic program, the corresponding program parts are typically grounded with {@link Clingo#clingo_control_ground}.
     *
     * @param control         the target
     * @param name            name of the program block
     * @param parameters      string array of parameters of the program block
     * @param parameters_size number of parameters
     * @param program         string representation of the program
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if parsing fails</li>
     * </ul>
     */
    byte clingo_control_add(Pointer control, String name, String[] parameters, NativeSize parameters_size, String program);

    /**
     * Ground the selected {@link ProgramPart parts} of the current (non-ground) logic program.
     * <p>
     * After grounding, logic programs can be solved with {@link Clingo#clingo_control_solve}.
     * <p>
     * Parts of a logic program without an explicit <tt>#program</tt>
     * specification are by default put into a program called `base` without
     * arguments.
     *
     * @param control              the target
     * @param parts                array of parts to ground
     * @param parts_size           size of the parts array
     * @param ground_callback      callback to implement external functions
     * @param ground_callback_data user data for ground_callback
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * <li>error code of ground callback</li>
     * </ul>
     */
    byte clingo_control_ground(Pointer control, Structure[] parts, NativeSize parts_size, GroundCallback ground_callback, Pointer ground_callback_data);

    // Solving Functions

    /**
     * Solve the currently {@link Clingo#clingo_control_ground grounded} logic program enumerating its models.
     * <p>
     * See the {@link org.potassco.clingo.solving.SolveHandle} module for more information.
     *
     * @param control          the target
     * @param mode             configures the search mode
     * @param assumptions      array of assumptions to solve under
     * @param assumptions_size number of assumptions
     * @param notify           the event handler to register
     * @param data             the user data for the event handler
     * @param handle           handle to the current search to enumerate models
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if solving could not be started</li>
     * </ul>
     */
    byte clingo_control_solve(Pointer control, int mode, int[] assumptions, NativeSize assumptions_size, SolveEventCallback notify, Pointer data, PointerByReference handle);

    /**
     * Clean up the domains of the grounding component using the solving
     * component's top level assignment.
     * <p>
     * This function removes atoms from domains that are false and marks atoms as
     * facts that are true.  With multi-shot solving, this can result in smaller
     * groundings because less rules have to be instantiated and more
     * simplifications can be applied.
     * <p>
     * It is typically not necessary to call this function manually because
     * automatic cleanups at the right time are enabled by default.
     *
     * @param control the target
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_control_cleanup(Pointer control);

    /**
     * Assign a truth value to an external atom.
     * <p>
     * If a negative literal is passed, the corresponding atom is assigned the
     * inverted truth value.
     * <p>
     * If the atom does not exist or is not external, this is a noop.
     *
     * @param control the target
     * @param literal literal to assign
     * @param value   the truth value
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_control_assign_external(Pointer control, int literal, int value);

    /**
     * Release an external atom.
     * <p>
     * If a negative literal is passed, the corresponding atom is released.
     * <p>
     * After this call, an external atom is no longer external and subject to
     * program simplifications.  If the atom does not exist or is not external,
     * this is a noop.
     *
     * @param control the target
     * @param literal literal to release
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_control_release_external(Pointer control, int literal);

    /**
     * Register a custom propagator with the control object.
     * <p>
     * If the sequential flag is set to true, the propagator is called
     * sequentially when solving with multiple threads.
     * <p>
     * See the {@link Propagator} module for more information.
     *
     * @param control    the target
     * @param propagator the propagator
     * @param data       user data passed to the propagator functions
     * @param sequential whether the propagator should be called sequentially
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_control_register_propagator(Pointer control, Propagator propagator, Pointer data, byte sequential);

    /**
     * Check if the solver has determined that the internal program representation is conflicting.
     * <p>
     * If this function returns true, solve calls will return immediately with an unsatisfiable solve result.
     * Note that conflicts first have to be detected, e.g. -
     * initial unit propagation results in an empty clause,
     * or later if an empty clause is resolved during solving.
     * Hence, the function might return false even if the problem is unsatisfiable.
     *
     * @param control the target
     * @return whether the program representation is conflicting
     */
    byte clingo_control_is_conflicting(Pointer control);

    /**
     * Get a statistics object to inspect solver statistics.
     * <p>
     * Statistics are updated after a solve call.
     * <p>
     * See the {@link org.potassco.clingo.statistics.Statistics} module for more information.
     * <p>
     * The level of detail of the statistics depends on the stats option
     * (which can be set using {@link Configuration} module or passed as an option when {@link Clingo#clingo_control_new grounded creating the control object}).
     * The default level zero only provides basic statistics,
     * level one provides extended and accumulated statistics,
     * and level two provides per-thread statistics.
     * Furthermore, the statistics object is best accessed right after solving.
     * Otherwise, not all of its entries have valid values.
     *
     * @param control    the target
     * @param statistics the statistics object
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_control_statistics(Pointer control, PointerByReference statistics);

    /**
     * Interrupt the active solve call (or the following solve call right at the beginning).
     *
     * @param control the target
     */
    void clingo_control_interrupt(Pointer control);

    /**
     * Get low-level access to clasp.
     * <p>
     * This function is intended for experimental use only and not part of the stable API.
     * <p>
     * This function may return a <code>nullptr</code>.
     * Otherwise, the returned pointer can be casted to a ClaspFacade pointer.
     *
     * @param control the target
     * @param clasp   pointer to the ClaspFacade object (may be <code>nullptr</code>)
     * @return whether the call was successful
     */
    byte clingo_control_clasp_facade(Pointer control, PointerByReference clasp);

    // Configuration Functions

    /**
     * Get a configuration object to change the solver configuration.
     * <p>
     * See the {@link Configuration} module for more information.
     *
     * @param control       the target
     * @param configuration the configuration object
     * @return whether the call was successful
     */
    byte clingo_control_configuration(Pointer control, PointerByReference configuration);

    /**
     * Configure how learnt constraints are handled during enumeration.
     * <p>
     * If the enumeration assumption is enabled, then all information learnt from
     * the solver's various enumeration modes is removed after a solve call. This
     * includes enumeration of cautious or brave consequences, enumeration of
     * answer sets with or without projection, or finding optimal models, as well
     * as clauses added with {@link Clingo#clingo_solve_control_add_clause}.
     * <p>
     * For practical purposes, this option is only interesting for single-shot solving
     * or before the last solve call to squeeze out a tiny bit of performance.
     * Initially, the enumeration assumption is enabled.
     *
     * @param control the target
     * @param enable  whether to enable the assumption
     * @return whether the call was successful
     */
    byte clingo_control_set_enable_enumeration_assumption(Pointer control, byte enable);

    /**
     * Check whether the enumeration assumption is enabled.
     * <p>
     * See {@link Clingo#clingo_control_set_enable_enumeration_assumption}.
     *
     * @param control the target
     * @return whether using the enumeration assumption is enabled
     */
    byte clingo_control_get_enable_enumeration_assumption(Pointer control);

    /**
     * Enable automatic cleanup after solving.
     * <p>
     * Cleanup is enabled by default.
     *
     * @param control the target
     * @param enable  whether to enable cleanups
     * @return whether the call was successful
     */
    byte clingo_control_set_enable_cleanup(Pointer control, byte enable);

    /**
     * Check whether automatic cleanup is enabled.
     * <p>
     * See {@link Clingo#clingo_control_set_enable_cleanup}.
     *
     * @param control the target
     */
    byte clingo_control_get_enable_cleanup(Pointer control);

    //  Program Inspection Functions

    /**
     * Return the symbol for a constant definition of form: <tt>#const name = symbol</tt>.
     *
     * @param control the target
     * @param name    the name of the constant
     * @param symbol  the resulting symbol
     * @return whether the call was successful
     */
    byte clingo_control_get_const(Pointer control, String name, LongByReference symbol);

    /**
     * Check if there is a constant definition for the given constant.
     *
     * @param control the target
     * @param name    the name of the constant
     * @param exists  whether a matching constant definition exists
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#RUNTIME} if constant definition does not exist</li>
     * </ul>
     */
    byte clingo_control_has_const(Pointer control, String name, ByteByReference exists);

    /**
     * Get an object to inspect symbolic atoms (the relevant Herbrand base) used
     * for grounding.
     * <p>
     * See the {@link SymbolicAtoms} module for more information.
     *
     * @param control the target
     * @param atoms   the symbolic atoms object
     * @return whether the call was successful
     */
    byte clingo_control_symbolic_atoms(Pointer control, PointerByReference atoms);

    /**
     * Get an object to inspect theory atoms that occur in the grounding.
     * <p>
     * See the {@link org.potassco.clingo.theory.TheoryAtoms} module for more information.
     *
     * @param control the target
     * @param atoms   the theory atoms object
     * @return whether the call was successful
     */
    byte clingo_control_theory_atoms(Pointer control, PointerByReference atoms);

    /**
     * Register a program observer with the control object.
     *
     * @param control  the target
     * @param observer the observer to register
     * @param replace  just pass the grounding to the observer but not the solver
     * @param data     user data passed to the observer functions
     * @return whether the call was successful
     */
    byte clingo_control_register_observer(Pointer control, Observer observer, byte replace, Pointer data);

    // Program Modification Functions

    /**
     * Get an object to add ground directives to the program.
     * <p>
     * See the {@link org.potassco.clingo.ast.ProgramBuilder} module for more information.
     *
     * @param control the target
     * @param backend the backend object
     * @return whether the call was successful; might set one of the following error codes:
     * <ul>
     * <li>{@link ErrorCode#BAD_ALLOC}</li>
     * </ul>
     */
    byte clingo_control_backend(Pointer control, PointerByReference backend);

    /**
     * Get an object to add non-ground directives to the program.
     * <p>
     * See the {@link org.potassco.clingo.ast.ProgramBuilder} module for more information.
     *
     * @param control the target
     * @param builder the program builder object
     * @return whether the call was successful
     */
    byte clingo_control_program_builder(Pointer control, PointerByReference builder);

    /**
     * Add an option that is processed with a custom parser.
     * <p>
     * Note that the parser also has to take care of storing the semantic value of
     * the option somewhere.
     * <p>
     * Parameter option specifies the name(s) of the option.
     * For example, "ping,p" adds the short option "-p" and its long form "--ping".
     * It is also possible to associate an option with a help level by adding ",@l" to the option specification.
     * Options with a level greater than zero are only shown if the argument to help is greater or equal to l.
     *
     * @param options     object to register the option with
     * @param group       options are grouped into sections as given by this string
     * @param option      specifies the command line option
     * @param description the description of the option
     * @param parse       callback to parse the value of the option
     * @param data        user data for the callback
     * @param multi       whether the option can appear multiple times on the command-line
     * @param argument    optional string to change the value name in the generated help output
     * @return whether the call was successful
     */
    byte clingo_options_add(Pointer options, String group, String option, String description, ParseCallback parse, Pointer data, byte multi, String argument);

    /**
     * Add an option that is a simple flag.
     * <p>
     * This function is similar to {@link Clingo#clingo_options_add} but simpler because it only supports flags, which do not have values.
     * If a flag is passed via the command-line the parameter target is set to true.
     *
     * @param options     object to register the option with
     * @param group       options are grouped into sections as given by this string
     * @param option      specifies the command line option
     * @param description the description of the option
     * @param target      boolean set to true if the flag is given on the command-line
     * @return whether the call was successful
     */
    byte clingo_options_add_flag(Pointer options, String group, String option, String description, ByteByReference target);

    /**
     * Run clingo with a customized main function (similar to python and lua embedding).
     *
     * @param application struct with callbacks to override default clingo functionality
     * @param arguments   command line arguments
     * @param size        number of arguments
     * @param data        user data to pass to callbacks in application
     * @return exit code to return from main function
     */
    int clingo_main(Application application, String[] arguments, NativeSize size, Pointer data);

    /**
     * Add a custom scripting language to clingo.
     *
     * @param name   the name of the scripting language
     * @param script struct with functions implementing the language
     * @param data   user data to pass to callbacks in the script
     * @return whether the call was successful
     */
    byte clingo_register_script(String name, Script script, Pointer data);

    /**
     * Get the version of the registered scripting language.
     *
     * @param name the name of the scripting language
     * @return the version
     */
    String clingo_script_version(String name);
}
