package org.potassco;

import java.util.LinkedList;
import java.util.List;

import org.potassco.cpp.clingo_h;
import org.potassco.enums.ErrorCode;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.enums.SymbolType;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SizeByReference;
import org.potassco.jna.SolveEventCallbackT;
import org.potassco.jna.SymbolByReference;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author Josef Schneeberger
 *
 */
public class Clingo {
	private ClingoLibrary clingoLibrary;
	private PointerByReference controlPointer;

	public Clingo() {
		super();
		this.clingoLibrary = ClingoLibrary.INSTANCE;
	}

	public Clingo(String name, String logicProgram) {
		this();
        this.controlPointer = new PointerByReference();
        clingoLibrary.clingo_control_new(null, new Size(0), null, null, 20, controlPointer);
        // add the program
		clingoLibrary.clingo_control_add(controlPointer.getValue(), name, null, new Size(0), logicProgram);
	}

	public Pointer getControl() {
		return this.controlPointer.getValue();
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
     * @param code [in] code the error code
     * @param message [in] message the error message
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

	long symbolCreateNumber(int number) {
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

	long symbolCreateString(String string) {
		SymbolByReference symb = new SymbolByReference();
		byte success = clingoLibrary.clingo_symbol_create_string(string, symb);
		return symb.getValue();
	}

	long symbolCreateId(String name, boolean positive) {
		SymbolByReference symb = new SymbolByReference();
		byte success = clingoLibrary.clingo_symbol_create_id(name, (byte) (positive ? 1 : 0), symb);
		return symb.getValue();
	}

	long symbolCreateFunction(String name, List<Long> arguments, boolean positive) {
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
		byte success = clingoLibrary.clingo_symbol_create_function(name, args, argumentsSize, (byte) (positive ? 1 : 0), symb);
		return symb.getValue();
	}

	public int symbolNumber(long symbol) {
		IntByReference ibr = new IntByReference();
		byte success = clingoLibrary.clingo_symbol_number(symbol, ibr);
		return ibr.getValue();
	}

	public String symbolName(long symbol) {
		String[] pointer = new String[1];
		byte success = clingoLibrary.clingo_symbol_name(symbol, pointer);
		String v = pointer[0];
		return v;
	}
	
	public String symbolString(long symbol) {
		// https://stackoverflow.com/questions/29162569/jna-passing-string-by-reference-to-dll-but-non-return
		String[] r1 = new String[1];
		byte success = clingoLibrary.clingo_symbol_string(symbol, r1);
		return r1[0];
	}

	public boolean symbolIsPositive(long symbol) {
		ByteByReference p_positive = new ByteByReference();
		byte success = clingoLibrary.clingo_symbol_is_positive(symbol, p_positive);
		byte v = p_positive.getValue();
		return v == 1;
	}

	public boolean symbolIsNegative(long symbol) {
		ByteByReference p_positive = new ByteByReference();
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
     *
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
     *
     * This functions takes a string as input and returns an equal unique string
     * that is (at the moment) not freed until the program is closed.
     * @param string the string to internalize
     * @return the internalized string
     */
    public String addString(String string) {
		String[] x = new String[1];
    	byte success = clingoLibrary.clingo_add_string(string, x);
		return x[0];
    }

    /**
     * Parse a term in string form.
     *
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
		byte success = clingoLibrary.clingo_symbolic_atoms_is_valid(atoms, iterator, p_valid);
		return p_valid.getValue();
 	}
 	
    /**
     * Get an object to inspect symbolic atoms (the relevant Herbrand base) used for grounding.
     * 
     * See the @ref SymbolicAtoms module for more information.
     * @param control
     * @return
     */
    public Pointer controlSymbolicAtoms(Pointer control) {
		PointerByReference atoms = new PointerByReference();
		byte success = clingoLibrary.clingo_control_symbolic_atoms(control, atoms);
		return atoms.getValue();
    }

	/* *******
	 * Solving
	 * ******* */
    
    /**
     * Get the number of symbols of the selected types in the model.
     * @param model the target
     * @param show which symbols to select
     * @return the number symbols
     */
    public long modelSymbolsSize(Pointer model, int show) {
    	SizeByReference size = new SizeByReference();
		byte success = clingoLibrary.clingo_model_symbols_size(model, show, size);
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
		byte success = clingoLibrary.clingo_model_symbols(model, show.getValue(), symbols, size);
		return symbols;
    }

	/**
	 * @param name
	 * @param logicProgram
	 * {@link clingo_h#clingo_control_add}
	 */
	public void add(String name, String logicProgram) {
        this.controlPointer = new PointerByReference();
        clingoLibrary.clingo_control_new(null, new Size(0), null, null, 20, controlPointer);
        // add the program
		clingoLibrary.clingo_control_add(controlPointer.getValue(), name, null, new Size(0), logicProgram);
	}
	
	/**
	 * @param name
	 * {@link clingo_h#clingo_control_ground}
	 */
	public void ground(String name) {
        Part[] parts = new Part [1];
        parts[0] = new Part();
		parts[0].name = name;
        parts[0].params = null;
        parts[0].size = new Size(0);
        clingoLibrary.clingo_control_ground(controlPointer.getValue(), parts, new Size(1), null, null);
	}

    /**
     * @return
     * @throws ClingoException
     * 
	 * {@link clingo_h#clingo_control_solve}
     */
    public SolveHandle solve() throws ClingoException {
        return solve(new SolveEventHandler(), SolveMode.YIELD);
    }

    public SolveHandle solve(SolveEventHandler handler, SolveMode... modes) throws ClingoException {

//        int mode = 0;
//        if (modes != null && modes.length > 0) {
//            for (int i=0; i<modes.length; i++) {
//                mode |= modes[i].getValue();
//            }
//        }

        SolveHandle solveHandle = new SolveHandle();
        SolveEventCallbackT cb = new SolveEventCallbackT() {
            public boolean call(int type, Pointer event, Pointer goon) {
                SolveEventType t = SolveEventType.fromValue(type);
                switch (t) {
                    case MODEL:
                    	long size = modelSymbolsSize(event, 2);
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
        PointerByReference hnd = new PointerByReference();
        clingoLibrary.clingo_control_solve(controlPointer.getValue(), 0, null, new Size(0), cb, null, hnd);
        IntByReference res = new IntByReference();
        clingoLibrary.clingo_solve_handle_get(hnd.getValue(), res);
        clingoLibrary.clingo_solve_handle_close(hnd.getValue());
        // clean up
        clingoLibrary.clingo_control_free(controlPointer.getValue());
		return solveHandle;
    }

}
