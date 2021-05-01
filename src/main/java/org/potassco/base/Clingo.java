package org.potassco.base;

import java.util.LinkedList;
import java.util.List;

import org.potassco.cpp.clingo_h;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.ErrorCode;
import org.potassco.enums.ModelType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.StatisticsType;
import org.potassco.enums.SymbolType;
import org.potassco.enums.TermType;
import org.potassco.enums.TruthValue;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.OptionParseCallbackT;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SizeByReference;
import org.potassco.jna.SolveEventCallbackT;
import org.potassco.jna.SymbolByReference;

import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author Josef Schneeberger
 *
 */
public class Clingo {
	private ClingoLibrary clingoLibrary;
	private Pointer control;

	public Clingo() {
		super();
		this.clingoLibrary = ClingoLibrary.INSTANCE;
	}

	public Clingo(String name, String logicProgram) {
		this();
        this.control = controlNew(null);
        // add the program
        controlAdd(control, name, logicProgram);
	}

	public Pointer getControl() {
		return this.control;
	}

	/* *******
	 * Version
	 * ******* */
	
	public String version() {
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        IntByReference patch = new IntByReference();
        clingoLibrary.clingo_version(major, minor, patch);
		return major.getValue() + "." + minor.getValue() + "." + patch.getValue();
	}

	/* *******************************
	 * Error message and code handling
	 * ******************************* */
	
    /**
     * Convert error code into string.
     * @param code
     * @return the error string
     */
    public String errorString(int code) {
        return clingoLibrary.clingo_error_string(code);
    }

    /**
     * @return the last error code set by a clingo API call.
     */
    public int errorCode() {
        return clingoLibrary.clingo_error_code();
    }
    
    /**
     * @return the last error message set if an API call fails.
     */
    public String errorMessage() {
        return clingoLibrary.clingo_error_message();
    }

    /**
     * Set a custom error code and message in the active thread.
     * @param code  code the error code
     * @param message  message the error message
     */
    public void setError(int code, String message) {
        clingoLibrary.clingo_set_error(code, message);
    }

    /**
     * @return the last error code set if an API call fails.
     */
    public ErrorCode getError() {
    	return ErrorCode.fromValue(clingoLibrary.clingo_error_code());
    }

    /**
     * Convert warning code into string.
     * @param code
     * @return the error string
     */
    public String warningString(int code) {
        return clingoLibrary.clingo_warning_string(code);
    }

	/* *******************
	 * Signature Functions
	 * ******************* */

	/**
	 * Create a new signature.
	 *
	 * @param[in] name name of the signature
	 * @param[in] arity arity of the signature
	 * @param[in] positive false if the signature has a classical negation sign
	 * @param[out] signature the resulting signature
	 * @return whether the call was successful; might set one of the following error codes:
	 * - ::clingo_error_bad_alloc
	 * {@link clingo_h#clingo_signature_create}
	 * @return
	 * @throws ClingoException 
	 */
	public Pointer signatureCreate(String name, int arity, boolean positive) throws ClingoException {
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
	 * {@link clingo_h#clingo_signature_name}
	 * @param signature [in] signature the target signature
	 * @return the name of the signature
	 */
	public String signatureName(Pointer signature) {
		return clingoLibrary.clingo_signature_name(signature);
	}
	
	/**
	 * Get the arity of a signature.
	 * {@link clingo_h#clingo_signature_arity}
	 * @param signature [in] signature the target signature
	 * @return the arity of the signature
	 */
	public int signatureArity(Pointer signature) {
		return clingoLibrary.clingo_signature_arity(signature);
	}

	public boolean signatureIsPositive(Pointer signature) {
		return clingoLibrary.clingo_signature_is_positive(signature) == 1;
	}

	public boolean signatureIsNegative(Pointer signature) {
		return clingoLibrary.clingo_signature_is_negative(signature) == 1;
	}

	public boolean signatureIsEqualTo(Pointer a, Pointer b) {
		return clingoLibrary.clingo_signature_is_equal_to(a, b) == 1;
	}
	
	public boolean signatureIsLessThan(Pointer a, Pointer b) {
		return clingoLibrary.clingo_signature_is_less_than(a, b) == 1;
	}
	
	public int signatureHash(Pointer signature) {
		Size hash = clingoLibrary.clingo_signature_hash(signature);
		return hash.intValue();
	}

	/* *****************************
	 * Symbol Construction Functions
	 * ***************************** */

	public long symbolCreateNumber(int number) {
		SymbolByReference sbr = new SymbolByReference();
		clingoLibrary.clingo_symbol_create_number(number, sbr);
		return sbr.getValue();
	}

	long symbolCreateSupremum() {
		SymbolByReference pointer = new SymbolByReference();
		clingoLibrary.clingo_symbol_create_supremum(pointer);
		return pointer.getValue();
	}

	long symbolCreateInfimum() {
		SymbolByReference pointer = new SymbolByReference();
		clingoLibrary.clingo_symbol_create_supremum(pointer);
		return pointer.getValue();
	}

	public long symbolCreateString(String string) {
		SymbolByReference symb = new SymbolByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_create_string(string, symb);
		return symb.getValue();
	}

	public long symbolCreateId(String name, boolean positive) {
		SymbolByReference symb = new SymbolByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_create_id(name, (byte) (positive ? 1 : 0), symb);
		return symb.getValue();
	}

	public long symbolCreateFunction(String name, List<Long> arguments, boolean positive) {
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
		byte success = clingoLibrary.clingo_symbol_create_function(name, args, argumentsSize, (byte) (positive ? 1 : 0), symb);
		return symb.getValue();
	}

	public int symbolNumber(long symbol) {
		IntByReference ibr = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_number(symbol, ibr);
		return ibr.getValue();
	}

	public String symbolName(long symbol) {
		String[] pointer = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_name(symbol, pointer);
		String v = pointer[0];
		return v;
	}
	
	public String symbolString(long symbol) {
		// https://stackoverflow.com/questions/29162569/jna-passing-string-by-reference-to-dll-but-non-return
		String[] r1 = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_string(symbol, r1);
		return r1[0];
	}

	public boolean symbolIsPositive(long symbol) {
		ByteByReference p_positive = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_is_positive(symbol, p_positive);
		byte v = p_positive.getValue();
		return v == 1;
	}

	public boolean symbolIsNegative(long symbol) {
		ByteByReference p_positive = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_is_negative(symbol, p_positive);
		byte v = p_positive.getValue();
		return v == 1;
	}
	
	/**
	 * Infunctional
	 * TODO: Output parameter p_p_arguments not yet accessed.
	 * @param symbol [in]
	 * @param arguments [out]
	 * @param size [out]
	 */
	public void symbolArguments(long symbol, List<Long> arguments, Long size) {
		if (arguments == null) {
			arguments = new LinkedList<Long>();
		}
		PointerByReference p_p_arguments = new PointerByReference();
		SizeByReference p_arguments_size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_arguments(symbol, p_p_arguments, p_arguments_size);
		size = p_arguments_size.getValue();
		Pointer p = p_p_arguments.getPointer();
//		long[] adrs = p.getLongArray(8, 2);
	}
	
    /**
     * Get the type of a symbol.
     * @param symbol [in] symbol the target symbol
     * @return the type of the symbol
     */
    public SymbolType symbolType(long symbol) {
    	int t = clingoLibrary.clingo_symbol_type(symbol);
    	return SymbolType.fromValue(t);
    }
    
    public long symbolToStringSize(long symbol) {
    	SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		boolean success = clingoLibrary.clingo_symbol_to_string_size(symbol, size);
		return size.getValue();
    }
    
    /**
     * @param symbol [in] symbol the target symbol
     * @param size [in] size the size of the string
     * @return the resulting string
     */
    public String symbolToString(long symbol, long size) {
		byte[] str = new byte[Math.toIntExact(size)];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbol_to_string(symbol, str, size);
		return new String(str);
    }
    
   /**
     * @param a first symbol
     * @param b second symbol
     * @return true if two symbols are equal.
     */
    public boolean symbolIsEqualTo(long a, long b) {
    	byte success = clingoLibrary.clingo_symbol_is_equal_to(a, b);
    	return success == 1;
    }
    
    /**
     * Check if a symbol is less than another symbol.
     * <p>
     * Symbols are first compared by type.  If the types are equal, the values are
     * compared (where strings are compared using strcmp).  Functions are first
     * compared by signature and then lexicographically by arguments.
     * @param a first symbol
     * @param b second symbol
     * @return
     */
    public boolean symbolIsLessThan(long a, long b) {
    	byte success = clingoLibrary.clingo_symbol_is_less_than(a, b);
    	return success == 1;
    }
    
    /**
     * Calculate a hash code of a symbol.
     * @param symbol symbol the target symbol
     * @return the hash code of the symbol
     */
    public int symbolHash(long symbol) {
		Size hash = clingoLibrary.clingo_symbol_hash(symbol);
		return hash.intValue();
    }

    /**
     * Internalize a string.
     * <p>
     * This functions takes a string as input and returns an equal unique string
     * that is (at the moment) not freed until the program is closed.
     * @param string the string to internalize
     * @return the internalized string
     */
    public String addString(String string) {
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
     * @param string the string to parse
     * @return the resulting symbol
     */
    public long parseTerm(String string) {
		// TODO: logger
		Pointer logger = null;
		PointerByReference loggerData = null;
		int message = 0;
    	LongByReference symbol = new LongByReference();
	@SuppressWarnings("unused")
    	byte success = clingoLibrary.clingo_parse_term(string, logger, loggerData, message, symbol);
		return symbol.getValue();
    }

	/* **************
	 * Symbolic atoms
	 * ************** */
	
    /**
     * Get the number of different atoms occurring in a logic program.
     * @return the number of atoms
     */
    public long symbolicAtomsSize(Pointer atoms) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_size(atoms, size);
		return size.getValue();
    }

    /**
     * Get a forward iterator to the beginning of the sequence of all symbolic
     * atoms optionally restricted to a given signature.
     * @param atoms the target
     * @param signature optional signature
     * @return the resulting iterator
     */
    public Pointer symbolicAtomsBegin(Pointer atoms, Pointer signature) {
		PointerByReference iterator = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_begin(atoms, signature, iterator);
		return iterator.getValue();
    }
    
 	/**
 	 * Iterator pointing to the end of the sequence of symbolic atoms.
 	 * @param atoms the target
 	 * @return the resulting iterator
 	 */
 	public Pointer symbolicAtomsEnd(Pointer atoms) {
		PointerByReference iterator = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_end(atoms, iterator);
		return iterator.getValue();
 	}

 	/**
 	 * Find a symbolic atom given its symbolic representation.
 	 * @param atoms the target
 	 * @param symbol the symbol to lookup
 	 * @return iterator pointing to the symbolic atom or to the end
 	 */
 	public Pointer symbolicAtomsFind(Pointer atoms, long symbol) {
		PointerByReference iterator = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_find(atoms, symbol, iterator);
		return iterator.getValue();
 	}
 	
 	/**
 	 * Check if two iterators point to the same element (or end of the sequence).
 	 * @param atoms the target
 	 * @param iteratorA the first iterator
 	 * @param iteratorB the second iterator
 	 * @return whether the two iterators are equal
 	 */
 	public boolean symbolicAtomsIteratorIsEqualTo(Pointer atoms, Pointer iteratorA, Pointer iteratorB) {
 		ByteByReference equal = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_iterator_is_equal_to(atoms, iteratorA, iteratorB, equal);
		return equal.getValue() == 1;
 	}

 	/**
 	 * Get the symbolic representation of an atom.
 	 * @param atoms the target
 	 * @param iterator iterator to the atom
 	 * @return the resulting symbol
 	 */
 	public long symbolicAtomsSymbol(Pointer atoms, Pointer iterator) {
		LongByReference p_symbol = new LongByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_symbol(atoms, iterator, p_symbol);
		return p_symbol.getValue();
 	}
 	
 	/**
 	 * Check whether an atom is a fact.
 	 * 
 	 * @note This does not determine if an atom is a cautious consequence. The
 	 * grounding or solving component's simplifications can only detect this in
 	 * some cases.
 	 * @param atoms the target
 	 * @param iterator iterator to the atom
 	 * @return fact whether the atom is a fact
 	 */
 	public boolean symbolicAtomsIsFact(Pointer atoms, Pointer iterator) {
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
 	 * @param atoms the target
 	 * @param iterator iterator to the atom
 	 * @return whether the atom is a external
 	 */
 	public long symbolicAtomsIsExternal(Pointer atoms, Pointer iterator) {
		ByteByReference p_external = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_is_external(atoms, iterator, p_external);
		return p_external.getValue();
 	}

 	/**
 	 * Returns the (numeric) aspif literal corresponding to the given symbolic atom.
 	 * 
 	 * Such a literal can be mapped to a solver literal (see the \ref Propagator
 	 * module) or be used in rules in aspif format (see the \ref ProgramBuilder module).
 	 * @param atoms the target
 	 * @param iterator iterator to the atom
 	 * @return the associated literal
 	 */
 	public Pointer symbolicAtomsLiteral(Pointer atoms, Pointer iterator) {
 		PointerByReference p_literal = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_literal(atoms, iterator, p_literal);
		return p_literal.getValue();
 	}

 	/**
 	 * Get the number of different predicate signatures used in the program.
 	 * @param atoms the target
 	 * @return the number of signatures
 	 */
 	public long symbolicAtomsSignaturesSize(Pointer atoms) {
 		SizeByReference p_size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_signatures_size(atoms, p_size);
		return p_size.getValue();
 	}

 	/**
 	 * Get the predicate signatures occurring in a logic program.
 	 * @param atoms the target
 	 * @param size the number of signatures
 	 * @return the resulting signatures
 	 */
 	public Pointer symbolicAtomsSignatures(Pointer atoms, long size) {
 		PointerByReference p_signatures = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_signatures(atoms, p_signatures, size);
		return p_signatures.getValue();
 	}

 	/**
 	 * Get an iterator to the next element in the sequence of symbolic atoms.
 	 * @param atoms the target
 	 * @param iterator the current iterator
 	 * @return the succeeding iterator
 	 */
 	public Pointer symbolicAtomsNext(Pointer atoms, Pointer iterator) {
 		PointerByReference p_next = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_next(atoms, iterator, p_next);
		return p_next.getValue();
 	}

 	/**
 	 * Check whether the given iterator points to some element with the sequence
 	 * of symbolic atoms or to the end of the sequence.
 	 * @param atoms the target
 	 * @param iterator the iterator
 	 * @return whether the iterator points to some element within the sequence
 	 */
 	public byte symbolicAtomsIsValid(Pointer atoms, Pointer iterator) {
 		ByteByReference p_valid = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_symbolic_atoms_is_valid(atoms, iterator, p_valid);
		return p_valid.getValue();
 	}
 	
	/* ************
	 * theory atoms
	 * ************ */

    /**
     * Get the type of the given theory term.
     * @param atoms container where the term is stored
     * @param term id of the term
     * @return the resulting type
     */
    public TermType theoryAtomsTermType(Pointer atoms, int term) {
    	IntByReference type = new IntByReference();
	@SuppressWarnings("unused")
    	byte success = clingoLibrary.clingo_theory_atoms_term_type(atoms, term, type);
		return TermType.fromValue(type.getValue());
    }

    /**
     * Get the number of the given numeric theory term.
     * @param atoms container where the term is stored
     * @param term id of the term
     * @return the resulting number
     */
    public int theoryAtomsTermNumber(Pointer atoms, int term) {
    	IntByReference number = new IntByReference();
	@SuppressWarnings("unused")
    	byte success = clingoLibrary.clingo_theory_atoms_term_number(atoms, term, number);
		return number.getValue();
    }

    /**
     * Get the name of the given constant or function theory term.
     * <p>
     * @note The lifetime of the string is tied to the current solve step.
     * <p>
     * @pre The term must be of type ::clingo_theory_term_type_function or ::clingo_theory_term_type_symbol.
     * @param atoms container where the term is stored
     * @param term id of the term
     * @return the resulting name
     */
    public String theoryAtomsTermName(Pointer atoms, int term) {
		String[] name = new String[1];
		@SuppressWarnings("unused")
    	byte success = clingoLibrary.clingo_theory_atoms_term_name(atoms, term, name);
		return name[0];
    }

    /**
     * Get the arguments of the given function theory term.
     * <p>
     * @pre The term must be of type ::clingo_theory_term_type_function.
     * @param atoms container where the term is stored
     * @param term id of the term
     * @return the resulting arguments in form of an array of term ids
     */
    public int[] theoryAtomsTermArguments(Pointer atoms, int term) {
    	PointerByReference arguments = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_term_arguments(atoms, term, arguments, size);
		int[] result = new int[Math.toIntExact(size.getValue())];
		for (int i = 0; i < result.length; i++) {
			Pointer p = arguments.getPointer();
			result[i] = p.getInt(8); // TODO ???
		}
		return result;
    }

    /**
     * Get the size of the string representation of the given theory term (including the terminating 0).
     * @param atoms container where the term is stored
     * @param term id of the term
     * @return the resulting size
     */
    public long theoryAtomsTermToStringSize(Pointer atoms, int term) {
    	SizeByReference size = new SizeByReference();
	@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_term_to_string_size(atoms, term, size );
		return size.getValue();
    }

    /**
     * Get the string representation of the given theory term.
     * @param atoms container where the term is stored
     * @param term id of the term
     * @param size the size of the string. The caller has to know the length of the string to return.
     * @return the resulting string
     */
    public String theoryAtomsTermToString(Pointer atoms, int term, long size) {
		byte[] str = new byte[Math.toIntExact(size)];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_term_to_string(atoms, term, str, size);
		return new String(str);
    }

    /**
     * Get the tuple (array of theory terms) of the given theory element.
     * @param atoms container where the element is stored
     * @param element id of the element
     * @return
     */
    //! @param[out] tuple the resulting array of term ids
    //! @param[out] size the number of term ids
    public Pointer theoryAtomsElementTuple(Pointer atoms, int element) {
		PointerByReference tuple = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_tuple(atoms, element, tuple, size);
		long s = size.getValue(); // TODO return size
		return tuple.getValue();
    }

    /**
     * Get the condition (array of aspif literals) of the given theory element.
     * @param atoms container where the element is stored
     * @param element id of the element
     * @return
     */
    //! @param[out] condition the resulting array of aspif literals
    //! @param[out] size the number of term literals
    public Pointer theoryAtomsElementCondition(Pointer atoms, int element) {
		PointerByReference condition = new PointerByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_condition(atoms, element, condition, size);
		long s = size.getValue(); // TODO return size
		return condition.getValue();
    }

    /**
     * Get the id of the condition of the given theory element.
     * <p>
     * @note
     * This id can be mapped to a solver literal using clingo_propagate_init_solver_literal().
     * This id is not (necessarily) an aspif literal; to get aspif literals use clingo_theory_atoms_element_condition().
     * @param atoms container where the element is stored
     * @param element id of the element
     * @return the resulting condition id
     */
    public int theoryAtomsElementConditionId(Pointer atoms, int element) {
    	IntByReference condition = new IntByReference();
	@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_condition_id(atoms, element, condition);
		return condition.getValue();
    }

    /**
     * Get the size of the string representation of the given theory element (including the terminating 0).
     * @param atoms container where the element is stored
     * @param element id of the element
     * @return the resulting size
     */
    public long theoryAtomsElementToStringSize(Pointer atoms, int element) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_to_string_size(atoms, element, size);
		return size.getValue();
    }

    /**
     * Get the string representation of the given theory element.
     * @param atoms container where the element is stored
     * @param element id of the element
     * @param size the size of the string. The caller hast to provide the length of the requested string. 
     * @return the resulting string
     */
    public String theoryAtomsElementToString(Pointer atoms, int element, long size) {
		byte[] str = new byte[Math.toIntExact(size)];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_element_to_string(atoms, element, str, size);
		return new String(str);
    }
    
    //! Theory Atom Inspection

    /**
     * Get the total number of theory atoms.
     * @param atoms the target
     * @return the resulting number
     */
    public long theoryAtomsSize(Pointer atoms) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_size(atoms, size);
		return size.getValue();
    }

    /**
     * Get the theory term associated with the theory atom.
     * @param atoms container where the atom is stored
     * @param atom id of the atom
     * @return the resulting term id
     */
    public long theoryAtomsAtomTerm(Pointer atoms, int atom) {
		IntByReference term = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_term(atoms, atom, term);
		return term.getValue();
    }

    /**
     * Get the theory elements associated with the theory atom.
     * @param atoms container where the atom is stored
     * @param atom id of the atom
     * @return
     */
    //! @param[out] elements the resulting array of elements
    //! @param[out] size the number of elements
    public long theoryAtomsAtomElements(Pointer atoms, int atom) {
		IntByReference elements = new IntByReference();
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_elements(atoms, atom, elements, size);
		return elements.getValue();
    }

    /**
     * Whether the theory atom has a guard.
     * @param atoms container where the atom is stored
     * @param atom id of the atom
     * @return whether the theory atom has a guard
     */
    public byte theoryAtomsAtomHasGuard(Pointer atoms, int atom) {
		ByteByReference hasGuard = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_has_guard(atoms, atom, hasGuard);
		return hasGuard.getValue();
    }

    /**
     * Get the guard consisting of a theory operator and a theory term of the given theory atom.
     * <p>
     * @note The lifetime of the string is tied to the current solve step.
     * @param atoms container where the atom is stored
     * @param atom id of the atom
     * @return
     */
    //! @param[out] connective the resulting theory operator
    //! @param[out] term the resulting term
    public void theoryAtomsAtomGuard(Pointer atoms, int atom) {
		byte[] connective = null;
		int term = 0;
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_guard(atoms, atom, connective, term);
//		return elements.getValue();
    }

    /**
     * Get the aspif literal associated with the given theory atom.
     * @param atoms container where the atom is stored
     * @param atom id of the atom
     * @return the resulting literal
     */
    public int theoryAtomsAtomLiteral(Pointer atoms, int atom) {
		IntByReference literal = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_literal(atoms, atom, literal);
		return literal.getValue();
    }

    /**
     * Get the size of the string representation of the given theory atom (including the terminating 0).
     * @param atoms container where the atom is stored
     * @param atom id of the atom
     * @return the resulting size
     */
    public long theoryAtomsAtomToStringSize(Pointer atoms, int atom) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_to_string_size(atoms, atom, size);
		return size.getValue();
    }

    /**
     * Get the string representation of the given theory atom.
     * @param atoms container where the atom is stored
     * @param atom id of the atom
     * @param size the size of the string. The caller hast to provide the size of the expected string.
     * @return the resulting size
     */
    public String theoryAtomsAtomToString(Pointer atoms, int atom, long size) {
		byte[] str = new byte[Math.toIntExact(size)];;
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_theory_atoms_atom_to_string(atoms, atom, str, size);
		return new String(str);
    }

	/* **********
	 * propagator
	 * ********** */

	/* **********
	 * backend
	 * ********** */

	/* *************
	 * configuration
	 * ************* */

    /**
     * Get the root key of the configuration.
     * @param configuration the target configuration
     * @return the root key
     */
    public int configurationRoot(Pointer configuration) {
		IntByReference key = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_root(configuration, key);
		return key.getValue();
    }

    /**
     * Get the type of a key.
     * <p>
     * @note The type is bitset, an entry can have multiple (but at least one) type.
     * @param configuration the target configuration
     * @param key the key
     * @return the resulting type
     */
    public ConfigurationType configurationType(Pointer configuration, int key) {
		IntByReference type = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_type(configuration, key, type);
		return ConfigurationType.fromValue(type.getValue());
    }

    /**
     * Get the description of an entry.e.
     * @param configuration the target configuration
     * @param key the key
     * @return 
     * @return the resulting type
     */
    public String configurationDescription(Pointer configuration, int key) {
		String[] description = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_description(configuration, key, description);
		return description[0];
    }

	/* Functions to access arrays */

    /**
     * Get the size of an array entry.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_array.
     * @param configuration the target configuration
     * @param key the key
     * @return the resulting size
     */
    public long configurationArraySize(Pointer configuration, int key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_array_size(configuration, key, size );
		return size.getValue();
    }

    /**
     * Get the subkey at the given offset of an array entry.
     * <p>
     * @note Some array entries, like fore example the solver configuration, can be accessed past there actual size to add subentries.
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_array.
     * @param configuration the target configuration
     * @param key the key
     * @param offset the offset in the array
     * @return the resulting subkey
     */
    public long configurationArrayAt(Pointer configuration, int key, long offset) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_array_at(configuration, key, offset, subkey);
		return subkey.getValue();
    }

    /* Functions to access maps */

    /**
     * Get the number of subkeys of a map entry.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_map.
     * @param configuration the target configuration
     * @param key the key
     * @return the resulting size
     */
    public long configurationMapSize(Pointer configuration, int key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_size(configuration, key, size);
		return size.getValue();
    }

    /**
     * Query whether the map has a key.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_map.
	 * @note Multiple levels can be looked up by concatenating keys with a period.
     * @param configuration the target configuration
     * @param key the key
     * @param  name the name to lookup the subkey
     * @return the resulting subkey
     */
    public long configurationMapHasSubkey(Pointer configuration, int key, String name) {
		ByteByReference result = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_has_subkey(configuration, key, name, result);
		return result.getValue();
    }

    /**
     * Get the name associated with the offset-th subkey.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_map.
     * @param[in] configuration the target configuration
     * @param configuration the target configuration
     * @param key the key
     * @param offset the offset of the name
     * @return the resulting name
     */
    public String configurationMapSubkeyName(Pointer configuration, int key, long offset) {
		String[] name = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_subkey_name(configuration, key, offset, name);
		return name[0];
    }

    /**
     * Lookup a subkey under the given name.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_map.
     * @note Multiple levels can be looked up by concatenating keys with a period.
     * @param configuration the target configuration
     * @param key the key
     * @param name the name to lookup the subkey
     * @return the resulting subkey
     */
    public int configurationMapAt(Pointer configuration, int key, String name) {
		IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_map_at(configuration, key, name, subkey );
		return subkey.getValue();
    }

	/* Functions to access values */

    /**
     * Check whether a entry has a value.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_value.
     * @param configuration the target configuration
     * @param key the key
     * @return whether the entry has a value
     */
    public byte configurationValueIsAssigned(Pointer configuration, int key) {
		ByteByReference assigned = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_is_assigned(configuration, key, assigned);
		return assigned.getValue();
    }

    /**
     * Get the size of the string value of the given entry.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_value.
     * @param configuration the target configuration
     * @param key the key
     * @return the resulting size
     */
    public long configurationValueGetSize(Pointer configuration, int key) {
		SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_get_size(configuration, key, size);
		return size.getValue();
    }

    /**
     * Get the string value of the given entry.
     * <p>
     * @pre The @link clingo_configuration_type() type@endlink of the entry must be @ref ::clingo_configuration_type_value.
     * @pre The given size must be larger or equal to size of the value.
     * @param configuration the target configuration
     * @param key the key
     * @param size the size of the given char array
     * @return the resulting string value
     */
    public String configurationValueGet(Pointer configuration, int key, long size) {
		byte[] value = new byte[Math.toIntExact(size)];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_get(configuration, key, value , size);
		return new String(value);
    }

    /**
     * Set the value of an entry.
     * @param configuration the target configuration
     * @param key the key
     * @param value the value to set
     */
    public void configurationValueSet(Pointer configuration, int key, String value) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_configuration_value_set(configuration, key, value);
    }

	/* **********
	 * statistics
	 * ********** */
    
//    StatisticsType

    /**
     * Get the root key of the statistics.
     *
     * @param[in] statistics the target statistics
     * @param[out] key the root key
     * @return whether the call was successful
     */
    public long statisticsRoot(Pointer statistics) {
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
     * @param[out] type the resulting type
     * @return whether the call was success
     */
    public StatisticsType statisticsType(Pointer statistics, long key) {
    	IntByReference type = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_type(statistics, key, type);
		return StatisticsType.fromValue(type.getValue());
    }
    
    /**
     * @name Functions to access arrays
     * @{

     * Get the size of an array entry.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_array.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[out] size the resulting size
     * @return whether the call was success
     */
    public long clingoStatisticsArraySize(Pointer statistics, long key) {
    	SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_array_size(statistics, key, size);
		return size.getValue();
    }
    /**
     * Get the subkey at the given offset of an array entry.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_array.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[in] offset the offset in the array
     * @param[out] subkey the resulting subkey
     * @return whether the call was success
     */
    public int statisticsArrayAt(Pointer statistics, long key, long offset) {
    	IntByReference subkey = new IntByReference();
    	@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_array_at(statistics, key, offset, subkey);
    	return subkey.getValue();
    }
    /**
     * Create the subkey at the end of an array entry.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_array.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[in] type the type of the new subkey
     * @param[out] subkey the resulting subkey
     * @return whether the call was success
     */
    public int statisticsArrayPush(Pointer statistics, long key, int type) {
    	IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_array_push(statistics, key, type, subkey);
		return subkey.getValue();
    }
    /**
     * @name Functions to access maps

     * Get the number of subkeys of a map entry.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_map.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[out] size the resulting number
     * @return whether the call was success
     */
    public long statisticsMapSize(Pointer statistics, long key) {
    	SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_size(statistics, key, size);
		return size.getValue();
    }
    /**
     * Test if the given map contains a specific subkey.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_map.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[in] name name of the subkey
     * @param[out] result true if the map has a subkey with the given name
     * @return whether the call was success
     */
    public byte statisticsMapHas_subkey(Pointer statistics, long key, String name) {
        ByteByReference result = new ByteByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_has_subkey(statistics, key, name, result);
		return result.getValue();
  }
    /**
     * Get the name associated with the offset-th subkey.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_map.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[in] offset the offset of the name
     * @param[out] name the resulting name
     * @return whether the call was success
     */
    public String statisticsMapSubkey_name(Pointer statistics, long key, long offset) {
    	String[] name = new String[1];
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_subkey_name(statistics, key, offset, name);
		return name[0];
    }
    
    /**
     * Lookup a subkey under the given name.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_map.
     * @note Multiple levels can be looked up by concatenating keys with a period.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[in] name the name to lookup the subkey
     * @param[out] subkey the resulting subkey
     * @return whether the call was success
     */
    public int statisticsMapAt(Pointer statistics, long key, String name) {
        IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_at(statistics, key, name, subkey);
		return subkey.getValue();
    }
    
    /**
     * Add a subkey with the given name.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_map.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[in] name the name of the new subkey
     * @param[in] type the type of the new subkey
     * @param[out] subkey the index of the resulting subkey
     * @return whether the call was success
     */
    public int statisticsMapAddSubkey(Pointer statistics, long key, String name, int type) {
    	IntByReference subkey = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_map_add_subkey(statistics, key, name, type, subkey);
		return subkey.getValue();
    }
    
    /**
     * Get the value of the given entry.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_value.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[out] value the resulting value
     * @return whether the call was successful
     */
    public double statisticsValueGet(Pointer statistics, long key) {
        DoubleByReference value = new DoubleByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_value_get(statistics, key, value);
		return value.getValue();
    }
    
    /**
     * Set the value of the given entry.
     *
     * @pre The @link clingo_statistics_type() type@endlink of the entry must be @ref ::clingo_statistics_type_value.
     * @param[in] statistics the target statistics
     * @param[in] key the key
     * @param[out] value the new value
     * @return whether the call was success
     */
    public void statisticsValueSet(Pointer statistics, long key, double value) {
        @SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_statistics_value_set(statistics, key, value);
    }

	/* **********
	 *  model and solve control
	 * ********** */

    /* Functions for Inspecting Models */

    /**
     * Get the type of the model.
     * @param model the target
     * @return the type of the model
     */
    public ModelType modelType(Pointer model) {
    	IntByReference type = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_type(model, type);
		return ModelType.fromValue(type.getValue());
    }
    
    /**
     * Get the running number of the model.
     * @param model the target
     * @return the number of the model
     */
    public int modelNumber(Pointer model) {
    	IntByReference number = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_number(model, number);
		return number.getValue();
    }
    
    /**
     * Get the number of symbols of the selected types in the model.
     * @param model the target
     * @param show which symbols to select - {@link ShowType}
     * @return the number symbols
     */
    public long modelSymbolsSize(Pointer model, ShowType show) {
    	SizeByReference size = new SizeByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_symbols_size(model, show.getValue(), size);
		return size.getValue();
    }

    /**
     * Get the symbols of the selected types in the model.
     * <p>
     * @note CSP assignments are represented using functions with name "$"
     * where the first argument is the name of the CSP variable and the second one its
     * value.
     * @param model [in] model the target
     * @param show [in] show which symbols to select. Of {@link ShowType}
     * @param size [in] size the number of selected symbols
     * @return the resulting symbols as an array[size] of symbol references
     * @see clingo_model_symbols_size()
     */
    public long[] modelSymbols(Pointer model, ShowType show, long size) {
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
    public byte clingo_model_contains(Pointer model, long atom) {
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
    public byte clingo_model_is_true(Pointer model, long literal) {
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
    public long clingo_model_cost_size(Pointer model) {
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
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - ::clingo_error_runtime if the size is too small
     *
     * @see clingo_model_cost_size()
     * @see clingo_model_optimality_proven()
     */
    public int clingo_model_cost(Pointer model, long size) {
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
    public byte clingo_model_optimality_proven(Pointer model) {
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
    public int clingo_model_thread_id(Pointer model) {
        IntByReference id = new IntByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_thread_id(model, id);
		return id.getValue();
    }
    /**
     * Add symbols to the model.
     *
     * These symbols will appear in clingo's output, which means that this
     * function is only meaningful if there is an underlying clingo application.
     * Only models passed to the ::clingo_solve_event_callback_t are extendable.
     *
     * @param[in] model the target
     * @param[in] symbols the symbols to add
     * @param[in] size the number of symbols to add
     * @return whether the call was successful
     */
    public void clingo_model_extend(Pointer model, long symbols, long size) {
        @SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_model_extend(model, symbols, size);
    }
    
    /* Functions for Adding Clauses */
    
    /**
     * Get the associated solve control object of a model.
     *
     * This object allows for adding clauses during model enumeration.
     * @param[in] model the target
     * @param[out] control the resulting solve control object
     * @return whether the call was successful
     */
    public Pointer clingo_model_context(Pointer model) {
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
    public Pointer clingo_solve_control_symbolic_atoms(Pointer control) {
        PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
        byte success = clingoLibrary.clingo_solve_control_symbolic_atoms(control, atoms);
		return atoms.getValue();
    }
    /**
     * Add a clause that applies to the current solving step during model
     * enumeration.
     *
     * @note The @ref Propagator module provides a more sophisticated
     * interface to add clauses - even on partial assignments.
     *
     * @param[in] control the target
     * @param[in] clause array of literals representing the clause
     * @param[in] size the size of the literal array
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - ::clingo_error_runtime if adding the clause fails
     */
    public void solveControlAddClause(Pointer control, Pointer clause, long size) {
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
	 * Blocks until the result is ready.
	 * When yielding partial solve results can be obtained, i.e.,
	 * when a model is ready, the result will be satisfiable but
	 * neither the search exhausted nor the optimality proven.
	 * @param handle the target
	 * @return the solve result
	 */
	public int solveHandleGet(Pointer handle) {
		IntByReference res = new IntByReference();
		@SuppressWarnings("unused")
		boolean success = clingoLibrary.clingo_solve_handle_get(handle, res);
		return res.getValue();
	}

  	/**
  	 * Get the next model (or zero if there are no more models).
  	 * @param handle the target
  	 * @return the model (it is NULL if there are no more models)
  	 */
  	public Pointer solveHandleModel(Pointer handle) {
        PointerByReference model = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_solve_handle_model(handle, model);
		return model.getValue();
  	}

	/**
	 * Stops the running search and releases the handle.
    //!
    //! Blocks until the search is stopped (as if an implicit cancel was called before the handle is released).
    //!
    //! @param[in] handle
    //! @return whether the call was successful; might set one of the following error codes:
	 * @param handle the target
	 */
	private void solveHandleClose(Pointer handle) {
        byte success = clingoLibrary.clingo_solve_handle_close(handle);
	}

	/* **********
	 * ast v2
	 * ********** */

	/* **********
	 * 
	 * ********** */ 

	/* *******
	 * ground program observer
	 * ******* */
    
    // clingo_ground_program_observer_t
    
	/* *******
	 * control
	 * ******* */
    
    // clingo_part_t
    // clingo_ground_callback_t
    // clingo_control_t
    
	/**
	 * Create a new control object.
	 * <p>
	 * A control object has to be freed using clingo_control_free().
	 * TODO: This will be done in the Control class.
	 * @param arguments array of command line arguments
	 * @return resulting control object
	 */
	public Pointer controlNew(String[] arguments) {
		PointerByReference parray = null;
		int argumentsLength = 0;
		if (arguments != null) {
			// https://github.com/nativelibs4java/nativelibs4java/issues/476
			StringArray sarray = new StringArray(arguments);
			parray = new PointerByReference();
			parray.setPointer(sarray);
			argumentsLength = arguments.length;
		}
		// TODO
		Pointer logger = null;
		Pointer loggerData = null;
		int messageLimit = 0;
		PointerByReference control = new PointerByReference();
		@SuppressWarnings("unused")
//        clingoLibrary.clingo_control_new(null, 0, null, null, 20, controlPointer);
		boolean success = clingoLibrary.clingo_control_new(parray, argumentsLength, logger, loggerData, messageLimit, control);
		return control.getValue();
	}

	/**
	 * Free a control object created with {@link Clingo#controlNew(String[])}.
	 * @param control the target
	 */
	private void controlFree(Pointer control) {
        clingoLibrary.clingo_control_free(control);
	}

	/**
	 * @param control the target
	 * @param name
	 * @param logicProgram
	 */
	public void controlAdd(Pointer control, String name, String logicProgram) {
		clingoLibrary.clingo_control_add(control, name, null, new Size(0), logicProgram);
	}

    /**
	 * @param name
	 * {@link clingo_h#clingo_control_ground}
	 */
	public void ground(String name) {
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
        clingoLibrary.clingo_control_ground(control, parts, new Size(1), null, null);
	}
	
    /**
     * @param control the target
     * @param parts array of parts to ground
     * @param parts_size size of the parts array
     * @param ground_callback callback to implement external functions
     * @param ground_callback_data user data for ground_callback
     */
    public void controlGround(Pointer control, Part[] parts, Size parts_size, Pointer ground_callback, Pointer ground_callback_data) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_ground(control, parts, parts_size, ground_callback, ground_callback_data);
    }

	/* *****************
	 * Solving Functions
	 * ***************** */

	/**
	 * Solve the currently @link ::clingo_control_ground grounded @endlink logic program enumerating its models.
	 * <p>
	 * See the @ref SolveHandle module for more information.

	 * @param control the target
	 * @param mode configures the search mode
	 * @param assumptions array of assumptions to solve under
	 * @param assumptionsSize number of assumptions
	 * @param notify the event handler to register
	 * @param data the user data for the event handler
	 * @return 
	 * @return  handle to the current search to enumerate models
	 */
	public Pointer controlSolve(Pointer control, int mode, Pointer assumptions,
			int assumptionsSize, SolveEventCallbackT notify, Pointer data) {
        PointerByReference handle = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_solve(control, mode, assumptions, assumptionsSize, notify, data, handle);
		return handle.getValue();
	}
	
	/**
	 * Clean up the domains of the grounding component using the solving
     * component's top level assignment.
     * <p>
     * This function removes atoms from domains that are false and marks atoms as
     * facts that are true.  With multi-shot solving, this can result in smaller
     * groundings because less rules have to be instantiated and more
     * simplifications can be applied.
     * <p>
     * @note It is typically not necessary to call this function manually because
     * automatic cleanups at the right time are enabled by default.
     *
     * @see clingo_control_get_enable_cleanup()
     * @see clingo_control_set_enable_cleanup()
	 * @param control the target
	 */
	public void controlCleanup(Pointer control) {
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
	 * @param control the target
	 * @param literal literal to assign
	 * @param value the truth value
	 */
	public void controlAssignExternal(Pointer control, int literal, TruthValue value) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_assign_external(control, literal, value.getValue());
	}

	/**
	 * Release an external atom.
     * <p>
     * If a negative literal is passed, the corresponding atom is released.
     * <P>
     * After this call, an external atom is no longer external and subject to
     * program simplifications.  If the atom does not exist or is not external,
     * this is a noop.
	 * @param control the target
	 * @param literal literal to release
	 */
	public void controlReleaseExternal(Pointer control, int literal) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_release_external(control, literal);
	}

	/**
	 * Register a custom propagator with the control object.
     * <p>
     * If the sequential flag is set to true, the propagator is called
     * sequentially when solving with multiple threads.
     * <p>
     * See the @ref Propagator module for more information.
	 * @param control the target
	 * @param propagator the propagator
	 * @param data user data passed to the propagator functions
	 * @param sequential whether the propagator should be called sequentially
	 */
	public void controlRegisterPropagator(Pointer control, Pointer propagator, Pointer data, boolean sequential) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_register_propagator(control, propagator, data, (byte) (sequential ? 1 : 0));
	}

	/**
	 * Check if the solver has determined that the internal program representation is conflicting.
	 * <p>
	 * If this function returns true, solve calls will return immediately with an unsatisfiable solve result.
     * Note that conflicts first have to be detected, e.g. -
     * initial unit propagation results in an empty clause,
     * or later if an empty clause is resolved during solving.
     * Hence, the function might return false even if the problem is unsatisfiable.
	 * @param control the target
	 */
	public void controlIsConflicting(Pointer control) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_is_conflicting(control);
	}

    /**
     * Get a statistics object to inspect solver statistics.
     * <p>
     * Statistics are updated after a solve call.
     * <p>
     * See the @ref Statistics module for more information.
     * <p>
     * @attention
     * The level of detail of the statistics depends on the stats option
     * (which can be set using @ref Configuration module or passed as an option when @link clingo_control_new creating the control object@endlink).
     * The default level zero only provides basic statistics,
     * level one provides extended and accumulated statistics,
     * and level two provides per-thread statistics.
     * Furthermore, the statistics object is best accessed right after solving.
     * Otherwise, not all of its entries have valid values.
     * @param control the target
     * @return the statistics object
     */
    public Pointer controlStatistics(Pointer control) {
    	PointerByReference statistics = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_statistics(control, statistics);
		return statistics.getValue();
    }

    /**
     * Interrupt the active solve call (or the following solve call right at the beginning).
     * @param control the target
     */
    public void controlInterrupt(Pointer control) {
		clingoLibrary.clingo_control_interrupt(control);
    }

    /**
     * Get low-level access to clasp.
     * <p>
     * @attention
     * This function is intended for experimental use only and not part of the stable API.
     * <p>
     * This function may return a <code>nullptr</code>.
     * Otherwise, the returned pointer can be casted to a ClaspFacade pointer.
     * @param control the target
     * @return clasp pointer to the ClaspFacade object (may be <code>nullptr</code>)
     */
    public Pointer controlClaspFacade(Pointer control) {
    	PointerByReference claspFacade = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_clasp_facade(control, claspFacade);
		return claspFacade.getValue();
    }

    /**
     * Get a configuration object to change the solver configuration.
     * <p>
     * See the @ref Configuration module for more information.
     * @param control the target
     * @return the configuration object
     */
    public Pointer controlConfiguration(Pointer control) {
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
     * includes enumeration of cautious or brave consequences, enumeration of
     * answer sets with or without projection, or finding optimal models, as well
     * as clauses added with clingo_solve_control_add_clause().
     * <p>
     * @attention For practical purposes, this option is only interesting for single-shot solving
     * or before the last solve call to squeeze out a tiny bit of performance.
     * Initially, the enumeration assumption is enabled.
     * @param control the target
     * @param enable whether to enable the assumption
     */
    public void controlSetEnableEnumerationAssumption(Pointer control, boolean enable) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_set_enable_enumeration_assumption(control, (byte) (enable ? 1 : 0));
    }

    /**
     * Check whether the enumeration assumption is enabled.
     * <p>
     * See ::clingo_control_set_enable_enumeration_assumption().
     * @param control the target
     * @return 
     */
    public boolean controlGetEnableEnumerationAssumption(Pointer control) {
		byte enabled = clingoLibrary.clingo_control_get_enable_enumeration_assumption(control);
		return enabled == 1;
    }

    /**
     * Enable automatic cleanup after solving.
     * <p>
     * @note Cleanup is enabled by default.
     * <p>
     * @see clingo_control_cleanup()
     * @see clingo_control_get_enable_cleanup()
     * @param control the target
     * @param enable whether to enable cleanups
    */
    public void controlSetEnableCleanup(Pointer control, boolean enable) {
 		@SuppressWarnings("unused")
 		byte success = clingoLibrary.clingo_control_set_enable_cleanup(control, (byte) (enable ? 1 : 0));
    }

    /**
     * Check whether automatic cleanup is enabled.
     * <p>
     * See ::clingo_control_set_enable_cleanup().
     * <p>
     * @see clingo_control_cleanup()
     * @see clingo_control_set_enable_cleanup()
     * @param control the target
     * @return whether automatic cleanup is enabled.
     */
    public boolean controlGetEnableCleanup(Pointer control) {
 		@SuppressWarnings("unused")
 		byte enabled = clingoLibrary.clingo_control_get_enable_cleanup(control);
 		return enabled == 1;
    }
    
    // Program Inspection Functions

    /**
     * Return the symbol for a constant definition of form: <tt>\#const name = symbol</tt>.
     * @param control the target
     * @param name of the constant
     * @return the resulting symbol
     */
    public int controlGetConst(Pointer control, String name) {
 		IntByReference symbol = new IntByReference();
		@SuppressWarnings("unused")
 		byte success = clingoLibrary.clingo_control_get_const(control, name, symbol);
		return symbol.getValue();
    }

    /**
     * Check if there is a constant definition for the given constant.
     * <p>
     * @see clingo_control_get_const()
     * @param control the target
     * @param name the name of the constant
     * @return whether a matching constant definition exists
     */
    public boolean controlHasConst(Pointer control, String name) {
		ByteByReference exists = new ByteByReference();
		@SuppressWarnings("unused")
 		byte success = clingoLibrary.clingo_control_has_const(control, name, exists );
		return exists.getValue() == 1;
    }

    /**
     * Get an object to inspect symbolic atoms (the relevant Herbrand base) used for grounding.
     * <p>
     * See the @ref SymbolicAtoms module for more information.
     * @param control
     * @return
     */
    public Pointer controlSymbolicAtoms(Pointer control) {
		PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_symbolic_atoms(control, atoms);
		return atoms.getValue();
    }

    /**
     * Get an object to inspect theory atoms that occur in the grounding.
     * <p>
     * See the @ref TheoryAtoms module for more information.
     * @param control the target
     * @return the theory atoms object
     */
    public Pointer controlTheoryAtoms(Pointer control) {
    	PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_theory_atoms(control, atoms);
		return atoms.getValue();
    }

    /**
     * Register a program observer with the control object.
     * @param control the target
     * @param observer the observer to register
     * @param replace just pass the grounding to the observer but not the solver
     * @param data user data passed to the observer functions
     * @return
     */
    public void controlRegisterObserver(Pointer control, Pointer observer, boolean replace, Pointer data) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_register_observer(control, observer, (byte) (replace ? 1 : 0), data);
    }
    
    // Program Modification Functions
    
    /**
     * Get an object to add ground directives to the program.
     * <p>
     * See the @ref ProgramBuilder module for more information.
     * @param control the target
     * @return the backend object
     */
    public Pointer controlBackend(Pointer control) {
    	PointerByReference backend = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_backend(control, backend);
		return backend.getValue();
    }

    /**
     * Get an object to add non-ground directives to the program.
     * <p>
     * See the @ref ProgramBuilder module for more information.
     * @param control the target
     * @return the program builder object
     */
    public Pointer controlProgramBuilder(Pointer control) {
    	PointerByReference builder = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_program_builder(control, builder);
		return builder.getValue();
    }

    // extending clingo
    
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
     * Parameter option specifies the name(s) of the option.
     * For example, "ping,p" adds the short option "-p" and its long form "--ping".
     * It is also possible to associate an option with a help level by adding ",@l" to the option specification.
     * Options with a level greater than zero are only shown if the argument to help is greater or equal to l.
     *
     * @param options object to register the option with
     * @param group options are grouped into sections as given by this string
     * @param option specifies the command line option
     * @param description the description of the option
     * @param parse callback to parse the value of the option
     * @param data user data for the callback
     * @param multi whether the option can appear multiple times on the command-line
     * @param argument optional string to change the value name in the generated help output
     * @return
     */
    public void optionsAdd(Pointer options, String group, String option, String description,
    		OptionParseCallbackT parse, Pointer data, byte multi, String argument) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_options_add(options, group, option, description, parse, data, multi, argument);
    }

    /**
     * Add an option that is a simple flag.
     * <p>
     * This function is similar to @ref clingo_options_add() but simpler because it only supports flags, which do not have values.
     * If a flag is passed via the command-line the parameter target is set to true.
     *
     * @param options object to register the option with
     * @param group options are grouped into sections as given by this string
     * @param option specifies the command line option
     * @param description the description of the option
     * @param target target boolean set to true if the flag is given on the command-line
     */
    public void optionsAddFlag(Pointer options, String group, String option, String description, boolean target) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_options_add_flag(options, group, option, description, (byte) (target ? 1 : 0));
    }

    /**
     * Run clingo with a customized main function (similar to python and lua embedding).
     *
     * @param[in] application struct with callbacks to override default clingo functionality
     * @param[in] arguments command line arguments
     * @param[in] size number of arguments
     * @param[in] data user data to pass to callbacks in application
     * @return exit code to return from main function
     */
    public int main(Pointer application, String arguments, int size, Pointer data) {
		return clingoLibrary.clingo_main(application, arguments, size, data);
    }

    public SolveHandle solve() throws ClingoException {
        SolveHandle solveHandle = new SolveHandle();
        SolveEventCallbackT cb = new SolveEventCallbackT() {
            public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
                SolveEventType t = SolveEventType.fromValue(type);
                switch (t) {
                    case MODEL:
                    	long size = modelSymbolsSize(event, ShowType.SHOWN);
                        solveHandle.setSize(size);
                        long[] symbols = modelSymbols(event, ShowType.SHOWN, size);
                        for (int i = 0; i < size; ++i) {
                            long len = symbolToStringSize(symbols[i]);
                            String symbol = symbolToString(symbols[i], len);
                            solveHandle.addSymbol(symbol.trim());
                        }
                        break;
                    case STATISTICS:
                        break;
                    case UNSAT:
                        break;
                    case FINISH:
//                        Pointer<Integer> p_event = (Pointer<Integer>) event;
//                        handler.onFinish(new SolveResult(p_event.get()));
//                        goon.set(true);
                        return true;
                }
                return true;
            }
        };
        Pointer hnd = controlSolve(control, 0, null, 0, cb, null);
        solveHandleClose(hnd);
        // clean up
        controlFree(control);
		return solveHandle;
    }

}