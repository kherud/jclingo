package org.potassco;

import java.nio.charset.StandardCharsets;

import org.potassco.cpp.clingo_h;
import org.potassco.enums.ErrorCode;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.Part;
import org.potassco.jna.Signature;
import org.potassco.jna.Size;
import org.potassco.jna.SizeByReference;
import org.potassco.jna.SolveEventCallbackT;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
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

	/* *******
	 * Signature Functions
	 * ******* */

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

	/* *******
	 * Solving
	 * ******* */
	
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

    public SolveHandle solve(SolveEventHandler handler, SolveMode ... modes) throws ClingoException {

        int mode = 0;
        if (modes != null && modes.length > 0) {
            for (int i=0; i<modes.length; i++) {
                mode |= modes[i].getValue();
            }
        }

        SolveHandle solveHandle = new SolveHandle();
        SolveEventCallbackT cb = new SolveEventCallbackT() {
            public boolean call(int type, Pointer event, Pointer goon) {
                SolveEventType t = SolveEventType.fromValue(type);
                switch (t) {
                    case MODEL:
                        SizeByReference sizeRef = new SizeByReference();
                        clingoLibrary.clingo_model_symbols_size(event, 2, sizeRef);
                        solveHandle.setSize((int) sizeRef.getValue());
                        long[] symbols = new long [(int)sizeRef.getValue()];
                        clingoLibrary.clingo_model_symbols(event, 2, symbols, new Size(sizeRef.getValue()));
                        for (int i = 0; i < sizeRef.getValue(); ++i) {
                            SizeByReference len = new SizeByReference();
                            clingoLibrary.clingo_symbol_to_string_size(symbols[i], len);
                            byte[] str = new byte[(int)len.getValue()];
                            clingoLibrary.clingo_symbol_to_string(symbols[i], str, new Size(len.getValue()));
                            String symbol = new String(str);
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
