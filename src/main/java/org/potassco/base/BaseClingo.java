package org.potassco.base;

import java.util.List;

import org.potassco.api.ClingoException;
import org.potassco.cpp.clingo_h;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.ErrorCode;
import org.potassco.enums.ExternalType;
import org.potassco.enums.HeuristicType;
import org.potassco.enums.ModelType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;
import org.potassco.enums.StatisticsType;
import org.potassco.enums.SymbolType;
import org.potassco.enums.TermType;
import org.potassco.enums.TruthValue;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.GroundCallbackT;
import org.potassco.jna.OptionParseCallbackT;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SizeByReference;
import org.potassco.jna.SolveEventCallback;
import org.potassco.jna.SymbolByReference;

import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * MIT License</br>
 * Copyright 2021 Josef Schneeberger</br>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:</br>
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.</br>
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * <p>
 * BaseClingo is a singleton and holds the reference to the clingoLibrary.
 * 
 * @author Josef Schneeberger
 *
 */
public class BaseClingo {

	private static ClingoLibrary clingoLibrary;

    static {
        clingoLibrary = ClingoLibrary.INSTANCE;
    }
    
	/**
	 * This class should not be instantiated.
	 */
	private BaseClingo() {}
	
	/*
	 * ******* Version *******
	 */

	public static String version() {
		IntByReference major = new IntByReference();
		IntByReference minor = new IntByReference();
		IntByReference patch = new IntByReference();
		clingoLibrary.clingo_version(major, minor, patch);
		return major.getValue() + "." + minor.getValue() + "." + patch.getValue();
	}

	/*
	 * ***** Error message and code handling *****
	 */

	/**
	 * Convert error code into string.
	 * 
	 * @param code
	 * @return the error string
	 */
	public static String errorString(int code) {
		return clingoLibrary.clingo_error_string(code);
	}

	/**
	 * @return the last error code set by a clingo API call.
	 */
	public static int errorCode() {
		return clingoLibrary.clingo_error_code();
	}

	/**
	 * @return the last error message set if an API call fails.
	 */
	public static String errorMessage() {
		return clingoLibrary.clingo_error_message();
	}

	/**
	 * Set a custom error code and message in the active thread.
	 * 
	 * @param code    code the error code
	 * @param message message the error message
	 */
	public static void setError(int code, String message) {
		clingoLibrary.clingo_set_error(code, message);
	}

	/**
	 * @return the last error code set if an API call fails.
	 */
	public static int getError() {
//    	return ErrorCode.fromValue(clingoLibrary.clingo_error_code());
		// Since errors may be user defined, we just return an int and not an ErrorCode
		return clingoLibrary.clingo_error_code();
	}

	/**
	 * Convert warning code into string.
	 * 
	 * @param code
	 * @return the error string
	 */
	public static String warningString(int code) {
		return clingoLibrary.clingo_warning_string(code);
	}

	/*
	 * ******************* Signature Functions *******************
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
	public static Pointer signatureCreate(String name, int arity, boolean positive) throws ClingoException {
		PointerByReference sigPointer = new PointerByReference();
		int pos = positive ? 1 : 0;
		int success = clingoLibrary.clingo_signature_create(name, arity, pos, sigPointer);
		if (ErrorCode.fromValue(success) == ErrorCode.BAD_ALLOC) {
			throw new ClingoException();
		}
		return sigPointer.getValue();
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
	public static String signatureName(Pointer signature) {
		return clingoLibrary.clingo_signature_name(signature);
	}

	/**
	 * Get the arity of a signature. {@link clingo_h#clingo_signature_arity}
	 * 
	 * @param signature [in] signature the target signature
	 * @return the arity of the signature
	 */
	public static int signatureArity(Pointer signature) {
		return clingoLibrary.clingo_signature_arity(signature);
	}

	/**
	 * Whether the signature is positive (is not classically negated).
	 * 
	 * @param signature the target signature
	 * @return
	 */
	public static boolean signatureIsPositive(Pointer signature) {
		return clingoLibrary.clingo_signature_is_positive(signature) == 1;
	}

	/**
	 * Whether the signature is negative (is classically negated).
	 * 
	 * @param signature the target signature
	 * @return
	 */
	public static boolean signatureIsNegative(Pointer signature) {
		return clingoLibrary.clingo_signature_is_negative(signature) == 1;
	}

	/**
	 * Check if two signatures are equal.
	 * 
	 * @param a first signature
	 * @param b second signature
	 * @return
	 */
	public static boolean signatureIsEqualTo(Pointer a, Pointer b) {
		return clingoLibrary.clingo_signature_is_equal_to(a, b) == 1;
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
	public static boolean signatureIsLessThan(Pointer a, Pointer b) {
		return clingoLibrary.clingo_signature_is_less_than(a, b) == 1;
	}

	/**
	 * Calculate a hash code of a signature.
	 * 
	 * @param signature the target signature
	 * @return
	 */
	public static int signatureHash(Pointer signature) {
		Size hash = clingoLibrary.clingo_signature_hash(signature);
		return hash.intValue();
	}

	/*
	 * *****************************
	 * Symbol Construction Functions
	 * *****************************
	 */

	/**
	 * Construct a symbol representing a number.
	 * 
	 * @param number
	 * @return a reference to the symbol
	 */
	public static long symbolCreateNumber(int number) {
		SymbolByReference sbr = new SymbolByReference();
		clingoLibrary.clingo_symbol_create_number(number, sbr);
		return sbr.getValue();
	}

	/**
	 * Construct a symbol representing \#sup.
	 * 
	 * @return a reference to the symbol
	 */
	public static long symbolCreateSupremum() {
		SymbolByReference pointer = new SymbolByReference();
		clingoLibrary.clingo_symbol_create_supremum(pointer);
		return pointer.getValue();
	}

	/**
	 * Construct a symbol representing \#inf.
	 * 
	 * @return a reference to the symbol
	 */
	public static long symbolCreateInfimum() {
		SymbolByReference pointer = new SymbolByReference();
		clingoLibrary.clingo_symbol_create_supremum(pointer);
		return pointer.getValue();
	}

	/**
	 * Construct a symbol representing a string.
	 * 
	 * @return a reference to the symbol
	 */
	public static long symbolCreateString(String string) {
		SymbolByReference symb = new SymbolByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_create_string(string, symb);
		return symb.getValue();
	}

	/**
	 * Construct a symbol representing an id.
	 * <p>
	 * 
	 * @note This is just a shortcut for clingo_symbol_create_function() with empty
	 *       arguments.
	 * @param name     the name
	 * @param positive whether the symbol has a classical negation sign
	 * @return a reference to the symbol
	 */
	public static long symbolCreateId(String name, boolean positive) {
		SymbolByReference symb = new SymbolByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_create_id(name, (byte) (positive ? 1 : 0), symb);
		return symb.getValue();
	}

	/**
	 * Construct a symbol representing a function or tuple.
	 * <p>
	 * 
	 * @note To create tuples, the empty string has to be used as name.
	 * @param name      the name of the function
	 * @param arguments the arguments of the function
	 * @param positive  whether the symbol has a classical negation sign
	 * @return a reference to the symbol
	 */
	public static long symbolCreateFunction(String name, List<Long> arguments, boolean positive) {
		SymbolByReference symb = new SymbolByReference();
		int argSize = arguments.size();
		Size argumentsSize = new Size(argSize);
		SymbolByReference[] args = new SymbolByReference[argSize];
		int i = 0;
		for (long s : arguments) {
			args[i] = new SymbolByReference();
			args[i].setValue(s);
			i++;
		}
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_create_function(name, args, argumentsSize, (byte) (positive ? 1 : 0),
				symb);
		return symb.getValue();
	}

	// Symbol Inspection Functions

	/**
	 * Get the number of a symbol.
	 * 
	 * @param symbol reference to a symbol
	 * @return the resulting number
	 */
	public static int symbolNumber(long symbol) {
		IntByReference ibr = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_number(symbol, ibr);
		return ibr.getValue();
	}

	/**
	 * Get the name of a symbol.
	 * 
	 * @param symbol reference to a symbol
	 * @return the resulting name
	 */
	public static String symbolName(long symbol) {
		String[] pointer = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_name(symbol, pointer);
		String v = pointer[0];
		return v;
	}

	/**
	 * Get the string of a symbol.
	 * <p>
	 * 
	 * @note The string is internalized and valid for the duration of the process.
	 * @param symbol reference to a symbol
	 * @return the resulting string
	 */
	public static String symbolString(long symbol) {
		// https://stackoverflow.com/questions/29162569/jna-passing-string-by-reference-to-dll-but-non-return
		String[] r1 = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_string(symbol, r1);
		return r1[0];
	}

	/**
	 * Check if a function is positive (does not have a sign).
	 * 
	 * @param symbol reference to a symbol
	 * @return true if positive
	 */
	public static boolean symbolIsPositive(long symbol) {
		ByteByReference p_positive = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_is_positive(symbol, p_positive);
		byte v = p_positive.getValue();
		return v == 1;
	}

	/**
	 * Check if a function is negative (has a sign).
	 * 
	 * @param symbol reference to a symbol
	 * @return true if negative
	 */
	public static boolean symbolIsNegative(long symbol) {
		ByteByReference p_positive = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_is_negative(symbol, p_positive);
		byte v = p_positive.getValue();
		return v == 1;
	}

	/**
	 * Get the arguments of a symbol.
	 * 
	 * @param symbol the target symbol
	 * @return the resulting arguments as an array of symbol references
	 */
	public static long[] symbolArguments(long symbol) {
		PointerByReference p_p_arguments = new PointerByReference();
		SizeByReference p_arguments_size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_arguments(symbol, p_p_arguments, p_arguments_size);
		long size = p_arguments_size.getValue();
		Pointer p = p_p_arguments.getValue();
		return p.getLongArray(0, Math.toIntExact(size));
	}

	/**
	 * Get the type of a symbol.
	 * 
	 * @param symbol [in] symbol the target symbol
	 * @return the type of the symbol
	 */
	public static SymbolType symbolType(long symbol) {
		int t = clingoLibrary.clingo_symbol_type(symbol);
		return SymbolType.fromValue(t);
	}

	/**
	 * We provide just on function: {@link #symbolToString(long)}
	 * Keep for futer releases of the clingo API!
	 * @param symbol
	 * @return
	 */
	@SuppressWarnings("unused")
	private long symbolToStringSize(long symbol) {
		SizeByReference size = new SizeByReference();
		byte success = clingoLibrary.clingo_symbol_to_string_size(symbol, size);
		return size.getValue();
	}

	/**
	 * Get the string representation of a symbol.
	 * @param symbol the target symbol
	 * @return the resulting string
	 */
	public static String symbolToString(long symbol) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success1 = clingoLibrary.clingo_symbol_to_string_size(symbol, size);
		long s = size.getValue();
		byte[] str = new byte[Math.toIntExact(s)];
		@SuppressWarnings("unused")
		byte success2 = clingoLibrary.clingo_symbol_to_string(symbol, str, s);
		return new String(str);
	}

	// Symbol Comparison Functions

	/**
	 * @param a first symbol
	 * @param b second symbol
	 * @return true if two symbols are equal.
	 */
	public static boolean symbolIsEqualTo(long a, long b) {
		byte success = clingoLibrary.clingo_symbol_is_equal_to(a, b);
		return success == 1;
	}

	/**
	 * Check if a symbol is less than another symbol.
	 * <p>
	 * Symbols are first compared by type. If the types are equal, the values are
	 * compared (where strings are compared using strcmp). Functions are first
	 * compared by signature and then lexicographically by arguments.
	 * 
	 * @param a first symbol
	 * @param b second symbol
	 * @return
	 */
	public static boolean symbolIsLessThan(long a, long b) {
		byte success = clingoLibrary.clingo_symbol_is_less_than(a, b);
		return success == 1;
	}

	/**
	 * Calculate a hash code of a symbol.
	 * 
	 * @param symbol symbol the target symbol
	 * @return the hash code of the symbol
	 */
	public static int symbolHash(long symbol) {
		Size hash = clingoLibrary.clingo_symbol_hash(symbol);
		return hash.intValue();
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
	public static String addString(String string) {
		String[] x = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_add_string(string, x);
		return x[0];
	}

	/**
	 * Parse a term in string form.
	 * <p>
	 * The result of this function is a symbol. The input term can contain
	 * unevaluated functions, which are evaluated during parsing.
	 * 
	 * @param string the string to parse
	 * @return the resulting symbol
	 */
	public static long parseTerm(String string) {
		// TODO: logger
		Pointer logger = null;
		PointerByReference loggerData = null;
		int message = 0;
		LongByReference symbol = new LongByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_parse_term(string, logger, loggerData, message, symbol);
		return symbol.getValue();
	}

	/*
	 * ************** Symbolic atoms **************
	 */

	/**
	 * Get the number of different atoms occurring in a logic program.
	 * 
	 * @return the number of atoms
	 */
	public static long symbolicAtomsSize(Pointer atoms) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_size(atoms, size);
		return size.getValue();
	}

	/**
	 * Get a forward iterator to the beginning of the sequence of all symbolic atoms
	 * optionally restricted to a given signature.
	 * 
	 * @param atoms     the target
	 * @param signature optional signature
	 * @return the resulting iterator
	 */
	public static Pointer symbolicAtomsBegin(Pointer atoms, Pointer signature) {
		PointerByReference iterator = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_begin(atoms, signature, iterator);
		return iterator.getValue();
	}

	/**
	 * Iterator pointing to the end of the sequence of symbolic atoms.
	 * 
	 * @param atoms the target
	 * @return the resulting iterator
	 */
	public static Pointer symbolicAtomsEnd(Pointer atoms) {
		PointerByReference iterator = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_end(atoms, iterator);
		return iterator.getValue();
	}

	/**
	 * Find a symbolic atom given its symbolic representation.
	 * 
	 * @param atoms  the target
	 * @param symbol the symbol to lookup
	 * @return iterator pointing to the symbolic atom or to the end
	 */
	public static Pointer symbolicAtomsFind(Pointer atoms, long symbol) {
		PointerByReference iterator = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_find(atoms, symbol, iterator);
		return iterator.getValue();
	}

	/**
	 * Check if two iterators point to the same element (or end of the sequence).
	 * 
	 * @param atoms     the target
	 * @param iteratorA the first iterator
	 * @param iteratorB the second iterator
	 * @return whether the two iterators are equal
	 */
	public static boolean symbolicAtomsIteratorIsEqualTo(Pointer atoms, Pointer iteratorA, Pointer iteratorB) {
		ByteByReference equal = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_iterator_is_equal_to(atoms, iteratorA, iteratorB, equal);
		return equal.getValue() == 1;
	}

	/**
	 * Get the symbolic representation of an atom.
	 * 
	 * @param atoms    the target
	 * @param iterator iterator to the atom
	 * @return the resulting symbol
	 */
	public static long symbolicAtomsSymbol(Pointer atoms, Pointer iterator) {
		LongByReference p_symbol = new LongByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_symbol(atoms, iterator, p_symbol);
		return p_symbol.getValue();
	}

	/**
	 * Check whether an atom is a fact.
	 * 
	 * @note This does not determine if an atom is a cautious consequence. The
	 *       grounding or solving component's simplifications can only detect this
	 *       in some cases.
	 * @param atoms    the target
	 * @param iterator iterator to the atom
	 * @return fact whether the atom is a fact
	 */
	public static boolean symbolicAtomsIsFact(Pointer atoms, Pointer iterator) {
		ByteByReference p_fact = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_is_fact(atoms, iterator, p_fact);
		return p_fact.getValue() == 1;
	}

	/**
	 * Check whether an atom is external.
	 * 
	 * An atom is external if it has been defined using an external directive and
	 * has not been released or defined by a rule.
	 * 
	 * @param atoms    the target
	 * @param iterator iterator to the atom
	 * @return whether the atom is a external
	 */
	public static long symbolicAtomsIsExternal(Pointer atoms, Pointer iterator) {
		ByteByReference p_external = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_is_external(atoms, iterator, p_external);
		return p_external.getValue();
	}

	/**
	 * Returns the (numeric) aspif literal corresponding to the given symbolic atom.
	 * 
	 * Such a literal can be mapped to a solver literal (see the \ref Propagator
	 * module) or be used in rules in aspif format (see the \ref ProgramBuilder
	 * module).
	 * 
	 * @param atoms    the target
	 * @param iterator iterator to the atom
	 * @return the associated literal
	 */
	public static Pointer symbolicAtomsLiteral(Pointer atoms, Pointer iterator) {
		PointerByReference p_literal = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_literal(atoms, iterator, p_literal);
		return p_literal.getValue();
	}

	/**
	 * Get the number of different predicate signatures used in the program.
	 * 
	 * @param atoms the target
	 * @return the number of signatures
	 */
	public static long symbolicAtomsSignaturesSize(Pointer atoms) {
		SizeByReference p_size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_signatures_size(atoms, p_size);
		return p_size.getValue();
	}

	/**
	 * Get the predicate signatures occurring in a logic program.
	 * 
	 * @param atoms the target
	 * @param size  the number of signatures
	 * @return the resulting signatures
	 */
	public static Pointer symbolicAtomsSignatures(Pointer atoms, long size) {
		PointerByReference p_signatures = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_signatures(atoms, p_signatures, size);
		return p_signatures.getValue();
	}

	/**
	 * Get an iterator to the next element in the sequence of symbolic atoms.
	 * 
	 * @param atoms    the target
	 * @param iterator the current iterator
	 * @return the succeeding iterator
	 */
	public static Pointer symbolicAtomsNext(Pointer atoms, Pointer iterator) {
		PointerByReference p_next = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_next(atoms, iterator, p_next);
		return p_next.getValue();
	}

	/**
	 * Check whether the given iterator points to some element with the sequence of
	 * symbolic atoms or to the end of the sequence.
	 * 
	 * @param atoms    the target
	 * @param iterator the iterator
	 * @return whether the iterator points to some element within the sequence
	 */
	public static byte symbolicAtomsIsValid(Pointer atoms, Pointer iterator) {
		ByteByReference p_valid = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_is_valid(atoms, iterator, p_valid);
		return p_valid.getValue();
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
	public static TermType theoryAtomsTermType(Pointer atoms, int term) {
		IntByReference type = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_term_type(atoms, term, type);
		return TermType.fromValue(type.getValue());
	}

	/**
	 * Get the number of the given numeric theory term.
	 * 
	 * @param atoms container where the term is stored
	 * @param term  id of the term
	 * @return the resulting number
	 */
	public static int theoryAtomsTermNumber(Pointer atoms, int term) {
		IntByReference number = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_term_number(atoms, term, number);
		return number.getValue();
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
	public static String theoryAtomsTermName(Pointer atoms, int term) {
		String[] name = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_term_name(atoms, term, name);
		return name[0];
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
	public static long[] theoryAtomsTermArguments(Pointer atoms, int term) {
		PointerByReference arguments = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_term_arguments(atoms, term, arguments, size);
		Pointer v = arguments.getValue();
		int s = Math.toIntExact(size.getValue());
		if (v != null && s > 0) {
			return v.getLongArray(0, s);
		} else {
			return null;
		}
	}

	/**
	 * We provide just one function {@link #theoryAtomsTermToString(Pointer, int, long)}.
	 * Keep for future API releases.
	 * <p>
	 * Get the size of the string representation of the given theory term (including
	 * the terminating 0).
	 * 
	 * @param atoms container where the term is stored
	 * @param term  id of the term
	 * @return the resulting size
	 */
	@SuppressWarnings("unused")
	private long theoryAtomsTermToStringSize(Pointer atoms, int term) {
		SizeByReference size = new SizeByReference();
		byte success = clingoLibrary.clingo_theory_atoms_term_to_string_size(atoms, term, size);
		return size.getValue();
	}

	/**
	 * Get the string representation of the given theory term.
	 * 
	 * @param atoms container where the term is stored
	 * @param term id of the term
	 * @return the resulting string
	 */
	public static String theoryAtomsTermToString(Pointer atoms, int term) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success1 = clingoLibrary.clingo_theory_atoms_term_to_string_size(atoms, term, size);
		long s = size.getValue();
		byte[] str = new byte[Math.toIntExact(s)];
		@SuppressWarnings("unused")
		byte success2 = clingoLibrary.clingo_theory_atoms_term_to_string(atoms, term, str, s);
		return new String(str);
	}

	// Theory Element Inspection

	/**
	 * Get the tuple (array of theory terms) of the given theory element.
	 * 
	 * @param atoms   container where the element is stored
	 * @param element id of the element
	 * @return the resulting array of term ids
	 */
	public static long[] theoryAtomsElementTuple(Pointer atoms, int element) {
		PointerByReference tuple = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_tuple(atoms, element, tuple, size);
		int s = Math.toIntExact(size.getValue());
		return tuple.getValue().getLongArray(0, s);
	}

	/**
	 * Get the condition (array of aspif literals) of the given theory element.
	 * 
	 * @param atoms   container where the element is stored
	 * @param element id of the element
	 * @return the resulting array of aspif literals
	 */
	public static long[] theoryAtomsElementCondition(Pointer atoms, int element) {
		PointerByReference condition = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_condition(atoms, element, condition, size);
		int s = Math.toIntExact(size.getValue());
		if (s < 1) {
			long result[] = {};
			return result;
		} else {
			return condition.getValue().getLongArray(0, s);
		}
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
	public static int theoryAtomsElementConditionId(Pointer atoms, int element) {
		IntByReference condition = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_condition_id(atoms, element, condition);
		return condition.getValue();
	}

	/**
	 * We provide just one function {@link #theoryAtomsTermToString(Pointer, int, long)}.
	 * Keep for future API releases.
	 * <p>
	 * Get the size of the string representation of the given theory element
	 * (including the terminating 0).
	 * 
	 * @param atoms   container where the element is stored
	 * @param element id of the element
	 * @return the resulting size
	 */
	@SuppressWarnings("unused")
	private long theoryAtomsElementToStringSize(Pointer atoms, int element) {
		SizeByReference size = new SizeByReference();
		byte success = clingoLibrary.clingo_theory_atoms_element_to_string_size(atoms, element, size);
		return size.getValue();
	}

	/**
	 * Get the string representation of the given theory element.
	 * 
	 * @param atoms container where the element is stored
	 * @param element id of the element
	 * @return the resulting string
	 */
	public static String theoryAtomsElementToString(Pointer atoms, int element) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success1 = clingoLibrary.clingo_theory_atoms_element_to_string_size(atoms, element, size);
		long s = size.getValue();
		byte[] str = new byte[Math.toIntExact(s)];
		@SuppressWarnings("unused")
		byte success2 = clingoLibrary.clingo_theory_atoms_element_to_string(atoms, element, str, s);
		return new String(str);
	}

	// Theory Atom Inspection

	/**
	 * Get the total number of theory atoms.
	 * 
	 * @param atoms the target
	 * @return the resulting number
	 */
	public static long theoryAtomsSize(Pointer atoms) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_size(atoms, size);
		return size.getValue();
	}

	/**
	 * Get the theory term associated with the theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting term id
	 */
	public static long theoryAtomsAtomTerm(Pointer atoms, int atom) {
		IntByReference term = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_term(atoms, atom, term);
		return term.getValue();
	}

	/**
	 * Get the theory elements associated with the theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting array of elements
	 */
	public static long[] theoryAtomsAtomElements(Pointer atoms, int atom) {
		PointerByReference elements = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_elements(atoms, atom, elements, size);
		int s = Math.toIntExact(size.getValue());
		return elements.getValue().getLongArray(0, s);
	}

	/**
	 * Whether the theory atom has a guard.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return whether the theory atom has a guard
	 */
	public static byte theoryAtomsAtomHasGuard(Pointer atoms, int atom) {
		ByteByReference hasGuard = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_has_guard(atoms, atom, hasGuard);
		return hasGuard.getValue();
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
	public static long[] theoryAtomsAtomGuard(Pointer atoms, int atom) {
		PointerByReference connective = new PointerByReference();
		IntByReference term = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_guard(atoms, atom, connective, term);
		int s = term.getValue();
		return connective.getValue().getLongArray(0, s);
	}

	/**
	 * Get the aspif literal associated with the given theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting literal
	 */
	public static int theoryAtomsAtomLiteral(Pointer atoms, int atom) {
		IntByReference literal = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_literal(atoms, atom, literal);
		return literal.getValue();
	}

	/**
	 * We provide just one function {@link #theoryAtomsTermToString(Pointer, int, long)}.
	 * Keep for future API releases.
	 * <p>
	 * Get the size of the string representation of the given theory atom (including
	 * the terminating 0).
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom  id of the atom
	 * @return the resulting size
	 */
	@SuppressWarnings("unused")
	private long theoryAtomsAtomToStringSize(Pointer atoms, int atom) {
		SizeByReference size = new SizeByReference();
		byte success = clingoLibrary.clingo_theory_atoms_atom_to_string_size(atoms, atom, size);
		return size.getValue();
	}

	/**
	 * Get the string representation of the given theory atom.
	 * 
	 * @param atoms container where the atom is stored
	 * @param atom id of the atom
	 * @return the resulting size
	 */
	public static String theoryAtomsAtomToString(Pointer atoms, int atom) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success1 = clingoLibrary.clingo_theory_atoms_atom_to_string_size(atoms, atom, size);
		long s = size.getValue();
		byte[] str = new byte[Math.toIntExact(s)];
		@SuppressWarnings("unused")
		byte success2 = clingoLibrary.clingo_theory_atoms_atom_to_string(atoms, atom, str, s);
		return new String(str);
	}

	/*
	 * ********** propagator **********
	 */

	/*
	 * ********** backend **********
	 */

	/**
	 * Prepare the backend for usage.
	 * 
	 * @param p_backend the target
	 */
	public static void backendBegin(Pointer p_backend) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_begin(p_backend);
	}

	/**
	 * Finalize the backend after using it.
	 * 
	 * @param p_backend the target
	 */
	public static void backendEnd(Pointer p_backend) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_end(p_backend);
	}

	/**
	 * Add a rule to the program.
	 * 
	 * @param backend  the target backend
	 * @param choice   determines if the head is a choice or a disjunction
	 * @param head     the head atoms
	 * @param headSize the number of atoms in the head
	 * @param body     body literals
	 * @param bodySize the number of literals in the body
	 */
	// TODO: Remove size parameters
	public static void backendRule(Pointer backend, byte choice, int head, long headSize, int body, long bodySize) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_rule(backend, choice, head, headSize, body, bodySize);
	}

	/**
	 * Add a weight rule to the program.
	 * <p>
	 * 
	 * @attention All weights and the lower bound must be positive.
	 * @param backend    the target backend
	 * @param choice     determines if the head is a choice or a disjunction
	 * @param head       the head atoms
	 * @param headSize   the number of atoms in the head
	 * @param lowerBound the lower bound of the weight rule
	 * @param body       the weighted body literals
	 * @param bodySize   the number of weighted literals in the body
	 */
	public static void backendWeightRule(Pointer backend, byte choice, int head, long headSize, int lowerBound, int body,
			long bodySize) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_weight_rule(backend, choice, head, headSize, lowerBound, body,
				bodySize);
	}

	/**
	 * Add a minimize constraint (or weak constraint) to the program.
	 * 
	 * @param backend  the target backend
	 * @param priority the priority of the constraint
	 * @param literals the weighted literals whose sum to minimize
	 * @param size     the number of weighted literals
	 */
	public static void backendWeightMinimize(Pointer backend, int priority, int literals, long size) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_minimize(backend, priority, literals, size);
	}

	/**
	 * Add a projection directive.
	 * 
	 * @param backend the target backend
	 * @param atoms   the atoms to project on
	 * @param size    the number of atoms
	 */
	public static void backendWeightProject(Pointer backend, int atoms, long size) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_project(backend, atoms, size);
	}

	/**
	 * Add an external statement.
	 * 
	 * @param backend the target backend
	 * @param atom    the external atom
	 * @param type    the type of the external statement
	 */
	public static void backendWeightExternal(Pointer backend, int atom, ExternalType type) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_external(backend, atom, type.getValue());
	}

	/**
	 * Add an assumption directive.
	 * 
	 * @param backend  the target backend
	 * @param literals the literals to assume (positive literals are true and
	 *                 negative literals false for the next solve call)
	 * @param size     the number of atoms
	 */
	public static void backendWeightAssume(Pointer backend, int literals, long size) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_assume(backend, literals, size);
	}

	/**
	 * Add an heuristic directive.
	 * 
	 * @param backend   the target backend
	 * @param atom      the target atom
	 * @param type      the type of the heuristic modification
	 * @param bias      the heuristic bias
	 * @param priority  the heuristic priority
	 * @param condition the condition under which to apply the heuristic
	 *                  modification
	 * @param size      the number of atoms in the condition
	 */
	public static void backendWeightHeuristic(Pointer backend, int atom, HeuristicType type, int bias, int priority,
			int condition, long size) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_heuristic(backend, atom, type.getValue(), bias, priority, condition,
				size);
	}

	/**
	 * Add an edge directive.
	 * 
	 * @param backend   the target backend
	 * @param nodeU     the start vertex of the edge
	 * @param nodeV     the end vertex of the edge
	 * @param condition the condition under which the edge is part of the graph
	 * @param size      the number of atoms in the condition
	 */
	public static void backendWeightAcycEdge(Pointer backend, int nodeU, int nodeV, int condition, long size) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_acyc_edge(backend, nodeU, nodeV, condition, size);
	}

	/**
	 * Get a fresh atom to be used in aspif directives.
	 * 
	 * @param backend the target backend
	 * @param symbol  optional symbol to associate the atom with
	 * @return the resulting atom
	 */
	public static int backendWeightAddAtom(Pointer backend, int symbol) {
		IntByReference atom = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_backend_add_atom(backend, symbol, atom);
		return atom.getValue();
	}

	/*
	 * ************* configuration *************
	 */

	/**
	 * Get the root key of the configuration.
	 * 
	 * @param configuration the target configuration
	 * @return the root key
	 */
	public static int configurationRoot(Pointer configuration) {
		IntByReference key = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_root(configuration, key);
		return key.getValue();
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
	public static ConfigurationType configurationType(Pointer configuration, int key) {
		IntByReference type = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_type(configuration, key, type);
		return ConfigurationType.fromValue(type.getValue());
	}

	/**
	 * Get the description of an entry.
	 * 
	 * @param configuration the target configuration
	 * @param key           the key
	 * @return
	 * @return the resulting type
	 */
	public static String configurationDescription(Pointer configuration, int key) {
		String[] description = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_description(configuration, key, description);
		return description[0];
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
	public static long configurationArraySize(Pointer configuration, int key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_array_size(configuration, key, size);
		return size.getValue();
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
	public static long configurationArrayAt(Pointer configuration, int key, long offset) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_array_at(configuration, key, offset, subkey);
		return subkey.getValue();
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
	public static long configurationMapSize(Pointer configuration, int key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_size(configuration, key, size);
		return size.getValue();
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
	public static long configurationMapHasSubkey(Pointer configuration, int key, String name) {
		ByteByReference result = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_has_subkey(configuration, key, name, result);
		return result.getValue();
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
	public static String configurationMapSubkeyName(Pointer configuration, int key, long offset) {
		String[] name = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_subkey_name(configuration, key, offset, name);
		return name[0];
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
	public static int configurationMapAt(Pointer configuration, int key, String name) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_at(configuration, key, name, subkey);
		return subkey.getValue();
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
	public static byte configurationValueIsAssigned(Pointer configuration, int key) {
		ByteByReference assigned = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_is_assigned(configuration, key, assigned);
		return assigned.getValue();
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
	public static long configurationValueGetSize(Pointer configuration, int key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_get_size(configuration, key, size);
		return size.getValue();
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
	public static String configurationValueGet(Pointer configuration, int key, long size) {
		byte[] value = new byte[Math.toIntExact(size)];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_get(configuration, key, value, size);
		return new String(value);
	}

	/**
	 * Set the value of an entry.
	 * 
	 * @param configuration the target configuration
	 * @param key           the key
	 * @param value         the value to set
	 */
	public static void configurationValueSet(Pointer configuration, int key, String value) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_set(configuration, key, value);
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
	public static long statisticsRoot(Pointer statistics) {
		IntByReference key = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_root(statistics, key);
		return key.getValue();
	}

	/**
	 * Get the type of a key.
	 * 
	 * @param[in] statistics the target statistics
	 * @param[in] key the key
	 * @return the resulting type
	 */
	public static StatisticsType statisticsType(Pointer statistics, long key) {
		IntByReference type = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_type(statistics, key, type);
		return StatisticsType.fromValue(type.getValue());
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
	public static long clingoStatisticsArraySize(Pointer statistics, long key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_array_size(statistics, key, size);
		return size.getValue();
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

	public static int statisticsArrayAt(Pointer statistics, long key, long offset) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_array_at(statistics, key, offset, subkey);
		return subkey.getValue();
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
	public static int statisticsArrayPush(Pointer statistics, long key, int type) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_array_push(statistics, key, type, subkey);
		return subkey.getValue();
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
	public static long statisticsMapSize(Pointer statistics, long key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_size(statistics, key, size);
		return size.getValue();
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
	public static byte statisticsMapHasSubkey(Pointer statistics, long key, String name) {
		ByteByReference result = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_has_subkey(statistics, key, name, result);
		return result.getValue();
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
	public static String statisticsMapSubkeyName(Pointer statistics, long key, long offset) {
		String[] name = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_subkey_name(statistics, key, offset, name);
		return name[0];
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
	public static int statisticsMapAt(Pointer statistics, long key, String name) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_at(statistics, key, name, subkey);
		return subkey.getValue();
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
	public static int statisticsMapAddSubkey(Pointer statistics, long key, String name, int type) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_add_subkey(statistics, key, name, type, subkey);
		return subkey.getValue();
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
	public static double statisticsValueGet(Pointer statistics, long key) {
		DoubleByReference value = new DoubleByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_value_get(statistics, key, value);
		return value.getValue();
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
	public static void statisticsValueSet(Pointer statistics, long key, double value) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_value_set(statistics, key, value);
	}

	/*
	 * ************************ model and solve control ************************
	 */

	/* Functions for Inspecting Models */

	/**
	 * Get the type of the model.
	 * 
	 * @param model the target
	 * @return the type of the model
	 */
	public static ModelType modelType(Pointer model) {
		IntByReference type = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_type(model, type);
		return ModelType.fromValue(type.getValue());
	}

	/**
	 * Get the running number of the model.
	 * 
	 * @param model the target
	 * @return the number of the model
	 */
	public static long modelNumber(Pointer model) {
		LongByReference number = new LongByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_number(model, number);
		return number.getValue();
	}

	/**
	 * Get the number of symbols of the selected types in the model.
	 * 
	 * @param model the target
	 * @param show  which symbols to select - {@link ShowType}
	 * @return the number symbols
	 */
	public static long modelSymbolsSize(Pointer model, ShowType show) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_symbols_size(model, show.getValue(), size);
		return size.getValue();
	}

	/**
	 * Get the symbols of the selected types in the model.
	 * <p>
	 * 
	 * @note CSP assignments are represented using functions with name "$" where the
	 *       first argument is the name of the CSP variable and the second one its
	 *       value.
	 * @param model [in] model the target
	 * @param show  [in] show which symbols to select. Of {@link ShowType}
	 * @param size  [in] size the number of selected symbols
	 * @return the resulting symbols as an array[size] of symbol references
	 * @see clingo_model_symbols_size()
	 */
	public static long[] modelSymbols(Pointer model, ShowType show, long size) {
		long[] symbols = new long[Math.toIntExact(size)];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_symbols(model, show.getValue(), symbols, size);
		return symbols;
	}

	/**
	 * Constant time lookup to test whether an atom is in a model.
	 *
	 * @param[in] model the target
	 * @param[in] atom the atom to lookup
	 * @param[out] contained whether the atom is contained
	 * @return whether the call was successful
	 */
	public static byte modelContains(Pointer model, long atom) {
		ByteByReference contained = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_contains(model, atom, contained);
		return contained.getValue();
	}

	/**
	 * Check if a program literal is true in a model.
	 *
	 * @param[in] model the target
	 * @param[in] literal the literal to lookup
	 * @param[out] result whether the literal is true
	 * @return whether the call was successful
	 */
	public static byte modelIsTrue(Pointer model, long literal) {
		ByteByReference result = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_is_true(model, literal, result);
		return result.getValue();
	}

	/**
	 * Get the number of cost values of a model.
	 *
	 * @param[in] model the target
	 * @param[out] size the number of costs
	 * @return whether the call was successful
	 */
	public static long modelCostSize(Pointer model) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_cost_size(model, size);
		return size.getValue();
	}

	/**
	 * Get the cost vector of a model.
	 *
	 * @param[in] model the target
	 * @param[out] costs the resulting costs
	 * @param[in] size the number of costs
	 * @return whether the call was successful; might set one of the following error
	 *         codes: - ::clingo_error_bad_alloc - ::clingo_error_runtime if the
	 *         size is too small
	 *
	 * @see clingo_model_cost_size()
	 * @see clingo_model_optimality_proven()
	 */
	public static int modelCost(Pointer model, long size) {
		IntByReference costs = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_cost(model, costs, size);
		return costs.getValue();
	}

	/**
	 * Whether the optimality of a model has been proven.
	 *
	 * @param[in] model the target
	 * @param[out] proven whether the optimality has been proven
	 * @return whether the call was successful
	 *
	 * @see clingo_model_cost()
	 */
	public static byte modelOptimalityProven(Pointer model) {
		ByteByReference proven = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_optimality_proven(model, proven);
		return proven.getValue();
	}

	/**
	 * Get the id of the solver thread that found the model.
	 *
	 * @param[in] model the target
	 * @param[out] id the resulting thread id
	 * @return whether the call was successful
	 */
	public static int modelThreadId(Pointer model) {
		IntByReference id = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_thread_id(model, id);
		return id.getValue();
	}

	/**
	 * Add symbols to the model.
	 *
	 * These symbols will appear in clingo's output, which means that this function
	 * is only meaningful if there is an underlying clingo application. Only models
	 * passed to the ::clingo_solve_event_callback_t are extendable.
	 *
	 * @param[in] model the target
	 * @param[in] symbols the symbols to add
	 * @param[in] size the number of symbols to add
	 * @return whether the call was successful
	 */
	public static void modelExtend(Pointer model, long symbols, long size) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_extend(model, symbols, size);
	}

	/* Functions for Adding Clauses */

	/**
	 * Get the associated solve control object of a model.
	 *
	 * This object allows for adding clauses during model enumeration.
	 * 
	 * @param[in] model the target
	 * @param[out] control the resulting solve control object
	 * @return whether the call was successful
	 */
	public static Pointer modelContext(Pointer model) {
		PointerByReference control = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_context(model, control);
		return control.getValue();
	}

	/**
	 * Get an object to inspect the symbolic atoms.
	 *
	 * @param[in] control the target
	 * @param[out] atoms the resulting object
	 * @return whether the call was successful
	 */
	public static Pointer solveControlSymbolicAtoms(Pointer control) {
		PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_control_symbolic_atoms(control, atoms);
		return atoms.getValue();
	}

	/**
	 * Add a clause that applies to the current solving step during model
	 * enumeration.
	 *
	 * @note The @ref Propagator module provides a more sophisticated interface to
	 *       add clauses - even on partial assignments.
	 *
	 * @param[in] control the target
	 * @param[in] clause array of literals representing the clause
	 * @param[in] size the size of the literal array
	 * @return whether the call was successful; might set one of the following error
	 *         codes: - ::clingo_error_bad_alloc - ::clingo_error_runtime if adding
	 *         the clause fails
	 */
	public static void solveControlAddClause(Pointer control, Pointer clause, long size) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_control_add_clause(control, clause, size);
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
	public static int solveHandleGet(Pointer handle) {
		IntByReference res = new IntByReference();
		@SuppressWarnings("unused")
		boolean success = clingoLibrary.clingo_solve_handle_get(handle, res);
		return res.getValue();
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
	public static boolean solveHandleWait(Pointer handle, double timeout) {
		ByteByReference result = new ByteByReference();
		clingoLibrary.clingo_solve_handle_wait(handle, timeout, result);
		return result.getValue() == 1;
	}

	/**
	 * Get the next model (or zero if there are no more models).
	 * 
	 * @param handle the target
	 * @return the model (it is NULL if there are no more models)
	 */
	public static Pointer solveHandleModel(Pointer handle) {
		PointerByReference model = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_handle_model(handle, model);
		return model.getValue();
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
	public static long[] solveHandleCore(Pointer handle) {
		PointerByReference core = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_handle_core(handle, core, size);
		int s = Math.toIntExact(size.getValue());
		return core.getValue().getLongArray(0, s);
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
	public static void solveHandleResume(Pointer handle) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_handle_resume(handle);
	}

	/**
	 * Stop the running search and block until done.
	 * 
	 * @param handle the target
	 */
	public static void solveHandleCancel(Pointer handle) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_handle_cancel(handle);
	}

	/**
	 * Stops the running search and releases the handle.
	 *
	 * Blocks until the search is stopped (as if an implicit cancel was called
	 * before the handle is released).
	 * 
	 * @param handle the target
	 */
	public static void solveHandleClose(Pointer handle) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_handle_close(handle);
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
	 * ******* control *******
	 */

	// clingo_part_t
	// clingo_ground_callback_t
	// clingo_control_t

	public static Pointer control(String[] arguments, Pointer logger, Pointer loggerData, int messageLimit) {
		int argumentsLength;
		StringArray args;
		if (arguments == null) {
			argumentsLength = 0;
			args = null;
		} else {
			argumentsLength = arguments.length;
			args = new StringArray(arguments);
		}
		PointerByReference ctrl = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_new(args, argumentsLength, logger, loggerData, messageLimit, ctrl);
		return ctrl.getValue();
	}

	/**
	 * Free a control object created with {@link BaseClingo#controlNew(String[])}.
	 * 
	 * @param control the target
	 */
	// TODO: make invisible / remove if close is working properly
	public static void controlFree(Pointer control) {
		clingoLibrary.clingo_control_free(control);
	}

	/**
	 * Extend the logic program with a program in a file.
	 * 
	 * @param control the target
	 * @param file    path to the file
	 */
	public static void controlLoad(Pointer control, String file) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_load(control, file);
	}

	/**
	 * Extend the logic program with the given non-ground logic program in string
	 * form.
	 * <p>
	 * This function puts the given program into a block of form:
	 * <tt>\#program name(parameters).</tt>
	 * <p>
	 * After extending the logic program, the corresponding program parts are
	 * typically grounded with ::clingo_control_ground.
	 * 
	 * @param name           name of the program block
	 * @param parameters     string array of parameters of the program block
	 * @param parametersSize number of parameters
	 * @param program        string representation of the program
	 */
	public static void controlAdd(Pointer control, String name, String[] parameters, String program) {
		if (parameters == null) {
			parameters = new String[0];
		}
		clingoLibrary.clingo_control_add(control, name, parameters, new Size(parameters.length), program);
	}

	/**
	 * Ground the selected @link ::clingo_part parts @endlink of the current
	 * (non-ground) logic program.
	 * <p>
	 * After grounding, logic programs can be solved with ::clingo_control_solve().
	 * <p>
	 * 
	 * @note Parts of a logic program without an explicit <tt>\#program</tt>
	 *       specification are by default put into a program called `base` without
	 *       arguments.
	 * @param parts                array of parts to ground
	 * @param parts_size           size of the parts array
	 * @param ground_callback      callback to implement external functions
	 * @param ground_callback_data user data for ground_callback
	 */
	public static void controlGround(Pointer control, Part[] parts, Size parts_size, GroundCallbackT ground_callback,
			Pointer ground_callback_data) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_ground(control, parts, parts_size, ground_callback,
				ground_callback_data);
	}

	/*
	 * ***************** Solving Functions *****************
	 */

	/**
	 * Solve the currently @link ::clingo_control_ground grounded @endlink logic
	 * program enumerating its models.
	 * <p>
	 * See the @ref Solution module for more information.
	 * 
	 * @param mode            configures the search mode
	 * @param assumptions     array of assumptions to solve under
	 * @param assumptionsSize number of assumptions
	 * @param notify          the event handler to register
	 * @param data            the user data for the event handler
	 * @return handle to the current search to enumerate models
	 */
	public static Pointer controlSolve(Pointer control, SolveMode mode, Pointer assumptions, int assumptionsSize,
			SolveEventCallback notify, Pointer data) {
		PointerByReference handle = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_solve(control, mode.getValue(), assumptions, assumptionsSize,
				notify, data, handle);
		return handle.getValue();
	}

	/**
	 * Clean up the domains of the grounding component using the solving component's
	 * top level assignment.
	 * <p>
	 * This function removes atoms from domains that are false and marks atoms as
	 * facts that are true. With multi-shot solving, this can result in smaller
	 * groundings because less rules have to be instantiated and more
	 * simplifications can be applied.
	 * <p>
	 * 
	 * @note It is typically not necessary to call this function manually because
	 *       automatic cleanups at the right time are enabled by default.
	 *
	 * @see clingo_control_get_enable_cleanup()
	 * @see clingo_control_set_enable_cleanup()
	 */
	public static void controlCleanup(Pointer control) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_cleanup(control);
	}

	/**
	 * Assign a truth value to an external atom.
	 * <p>
	 * If a negative literal is passed, the corresponding atom is assigned the
	 * inverted truth value.
	 * <p>
	 * If the atom does not exist or is not external, this is a noop.
	 * 
	 * @param literal literal to assign
	 * @param value   the truth value
	 */
	public static void controlAssignExternal(Pointer control, int literal, TruthValue value) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_assign_external(control, literal, value.getValue());
	}

	/**
	 * Release an external atom.
	 * <p>
	 * If a negative literal is passed, the corresponding atom is released.
	 * <P>
	 * After this call, an external atom is no longer external and subject to
	 * program simplifications. If the atom does not exist or is not external, this
	 * is a noop.
	 * 
	 * @param literal literal to release
	 */
	public static void controlReleaseExternal(Pointer control, int literal) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_release_external(control, literal);
	}

	/**
	 * Register a custom propagator with the control object.
	 * <p>
	 * If the sequential flag is set to true, the propagator is called sequentially
	 * when solving with multiple threads.
	 * <p>
	 * See the @ref Propagator module for more information.
	 * 
	 * @param propagator the propagator
	 * @param data       user data passed to the propagator functions
	 * @param sequential whether the propagator should be called sequentially
	 */
	public static void controlRegisterPropagator(Pointer control, Pointer propagator, Pointer data, boolean sequential) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_register_propagator(control, propagator, data,
				(byte) (sequential ? 1 : 0));
	}

	/**
	 * Check if the solver has determined that the internal program representation
	 * is conflicting.
	 * <p>
	 * If this function returns true, solve calls will return immediately with an
	 * unsatisfiable solve result. Note that conflicts first have to be detected,
	 * e.g. - initial unit propagation results in an empty clause, or later if an
	 * empty clause is resolved during solving. Hence, the function might return
	 * false even if the problem is unsatisfiable.
	 * 
	 * @return
	 */
	public static boolean controlIsConflicting(Pointer control) {
		byte isConflicting = clingoLibrary.clingo_control_is_conflicting(control);
		return isConflicting == 1;
	}

	/**
	 * Get a statistics object to inspect solver statistics.
	 * <p>
	 * Statistics are updated after a solve call.
	 * <p>
	 * See the @ref Statistics module for more information.
	 * <p>
	 * 
	 * @attention The level of detail of the statistics depends on the stats option
	 *            (which can be set using @ref Configuration module or passed as an
	 *            option when @link clingo_control_new creating the control
	 *            object@endlink). The default level zero only provides basic
	 *            statistics, level one provides extended and accumulated
	 *            statistics, and level two provides per-thread statistics.
	 *            Furthermore, the statistics object is best accessed right after
	 *            solving. Otherwise, not all of its entries have valid values.
	 * @return the statistics object
	 */
	public static Pointer controlStatistics(Pointer control) {
		PointerByReference statistics = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_statistics(control, statistics);
		return statistics.getValue();
	}

	/**
	 * Interrupt the active solve call (or the following solve call right at the
	 * beginning).
	 */
	public static void controlInterrupt(Pointer control) {
		clingoLibrary.clingo_control_interrupt(control);
	}

	/**
	 * Get low-level access to clasp.
	 * <p>
	 * 
	 * @attention This function is intended for experimental use only and not part
	 *            of the stable API.
	 *            <p>
	 *            This function may return a <code>nullptr</code>. Otherwise, the
	 *            returned pointer can be casted to a ClaspFacade pointer.
	 * @return clasp pointer to the ClaspFacade object (may be <code>nullptr</code>)
	 */
	public static Pointer controlClaspFacade(Pointer control) {
		PointerByReference claspFacade = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_clasp_facade(control, claspFacade);
		return claspFacade.getValue();
	}

	/**
	 * Get a configuration object to change the solver configuration.
	 * <p>
	 * See the @ref Configuration module for more information.
	 * 
	 * @return the configuration object
	 */
	public static Pointer controlConfiguration(Pointer control) {
		PointerByReference configuration = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_configuration(control, configuration);
		return configuration.getValue();
	}

	/**
	 * Configure how learnt constraints are handled during enumeration.
	 * <p>
	 * If the enumeration assumption is enabled, then all information learnt from
	 * the solver's various enumeration modes is removed after a solve call. This
	 * includes enumeration of cautious or brave consequences, enumeration of answer
	 * sets with or without projection, or finding optimal models, as well as
	 * clauses added with clingo_solve_control_add_clause().
	 * <p>
	 * 
	 * @attention For practical purposes, this option is only interesting for
	 *            single-shot solving or before the last solve call to squeeze out a
	 *            tiny bit of performance. Initially, the enumeration assumption is
	 *            enabled.
	 * @param enable whether to enable the assumption
	 */
	public static void controlSetEnableEnumerationAssumption(Pointer control, boolean enable) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_set_enable_enumeration_assumption(control, (byte) (enable ? 1 : 0));
	}

	/**
	 * Check whether the enumeration assumption is enabled.
	 * <p>
	 * See ::clingo_control_set_enable_enumeration_assumption().
	 * 
	 * @return
	 */
	public static boolean controlGetEnableEnumerationAssumption(Pointer control) {
		byte enabled = clingoLibrary.clingo_control_get_enable_enumeration_assumption(control);
		return enabled == 1;
	}

	/**
	 * Enable automatic cleanup after solving.
	 * <p>
	 * 
	 * @note Cleanup is enabled by default.
	 *       <p>
	 * @see clingo_control_cleanup()
	 * @see clingo_control_get_enable_cleanup()
	 * @param enable whether to enable cleanups
	 */
	public static void controlSetEnableCleanup(Pointer control, boolean enable) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_set_enable_cleanup(control, (byte) (enable ? 1 : 0));
	}

	/**
	 * Check whether automatic cleanup is enabled.
	 * <p>
	 * See ::clingo_control_set_enable_cleanup().
	 * <p>
	 * 
	 * @see clingo_control_cleanup()
	 * @see clingo_control_set_enable_cleanup()
	 * @return whether automatic cleanup is enabled.
	 */
	public static boolean controlGetEnableCleanup(Pointer control) {
		byte enabled = clingoLibrary.clingo_control_get_enable_cleanup(control);
		return enabled == 1;
	}

	// Program Inspection Functions

	/**
	 * Return the symbol for a constant definition of form:
	 * <tt>\#const name = symbol</tt>.
	 * 
	 * @param name of the constant
	 * @return the resulting symbol
	 */
	public static int controlGetConst(Pointer control, String name) {
		IntByReference symbol = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_get_const(control, name, symbol);
		return symbol.getValue();
	}

	/**
	 * Check if there is a constant definition for the given constant.
	 * <p>
	 * 
	 * @see clingo_control_get_const()
	 * @param name the name of the constant
	 * @return whether a matching constant definition exists
	 */
	public static boolean controlHasConst(Pointer control, String name) {
		ByteByReference exists = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_has_const(control, name, exists);
		return exists.getValue() == 1;
	}

	/**
	 * Get an object to inspect symbolic atoms (the relevant Herbrand base) used for
	 * grounding.
	 * <p>
	 * See the @ref SymbolicAtoms module for more information.
	 * 
	 * @return
	 */
	public static Pointer controlSymbolicAtoms(Pointer control) {
		PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_symbolic_atoms(control, atoms);
		return atoms.getValue();
	}

	/**
	 * Get an object to inspect theory atoms that occur in the grounding.
	 * <p>
	 * See the @ref TheoryAtoms module for more information.
	 * 
	 * @return the theory atoms object
	 */
	public static Pointer controlTheoryAtoms(Pointer control) {
		PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_theory_atoms(control, atoms);
		return atoms.getValue();
	}

	/**
	 * Register a program observer with the control object.
	 * 
	 * @param observer the observer to register
	 * @param replace  just pass the grounding to the observer but not the solver
	 * @param data     user data passed to the observer functions
	 * @return
	 */
	public static void controlRegisterObserver(Pointer control, Pointer observer, boolean replace, Pointer data) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_register_observer(control, observer, (byte) (replace ? 1 : 0),
				data);
	}

	// Program Modification Functions

	/**
	 * Get an object to add ground directives to the program.
	 * <p>
	 * See the @ref ProgramBuilder module for more information.
	 * 
	 * @return the backend object
	 */
	public static Pointer controlBackend(Pointer control) {
		PointerByReference backend = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_backend(control, backend);
		return backend.getValue();
	}

	/**
	 * Get an object to add non-ground directives to the program.
	 * <p>
	 * See the @ref ProgramBuilder module for more information.
	 * 
	 * @return the program builder object
	 */
	public static Pointer controlProgramBuilder(Pointer control) {
		PointerByReference builder = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_program_builder(control, builder);
		return builder.getValue();
	}

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
	 * specification. Options with a level greater than zero are only shown if the
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
	public static void optionsAdd(Pointer options, String group, String option, String description, OptionParseCallbackT parse,
			Pointer data, byte multi, String argument) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_options_add(options, group, option, description, parse, data, multi,
				argument);
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
	public static void optionsAddFlag(Pointer options, String group, String option, String description, boolean target) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_options_add_flag(options, group, option, description,
				(byte) (target ? 1 : 0));
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
	public static int main(Pointer application, String arguments, int size, Pointer data) {
		return clingoLibrary.clingo_main(application, arguments, size, data);
	}

}
