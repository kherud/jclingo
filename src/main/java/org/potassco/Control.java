package org.potassco;

import org.potassco.dto.Solution;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.PartT;
import org.potassco.jna.SizeT;
import org.potassco.jna.SizeTByReference;
import org.potassco.jna.SolveEventCallbackT;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class Control {
	private ClingoLibrary clingoLibrary;
	private PointerByReference controlPointer;

	public Control(String name, String data, ClingoLibrary clingoLibrary) {
		super();
		this.clingoLibrary = clingoLibrary;
        this.controlPointer = new PointerByReference();
        clingoLibrary.clingo_control_new(null, new SizeT(0), null, null, 20, controlPointer);
        // add the program
		clingoLibrary.clingo_control_add(controlPointer.getValue(), name, null, new SizeT(0), data);
	}

	public void ground(String name) {
        PartT[] parts = new PartT [1];
        parts[0] = new PartT();
		parts[0].name = name;
        parts[0].params = null;
        parts[0].size = new SizeT(0);
        clingoLibrary.clingo_control_ground(controlPointer.getValue(), parts, new SizeT(1), null, null);
	}

	public Solution solve() {
    	Solution solution = new Solution();
        SolveEventCallbackT cb = new SolveEventCallbackT() {
            public boolean call(int type, Pointer event, Pointer goon) {
                if (type == 0) {
                    SizeTByReference num = new SizeTByReference();
                    clingoLibrary.clingo_model_symbols_size(event, 2, num);
                    solution.setSize((int) num.getValue());
//                    System.out.printf("model: %d\n", num.getValue());
                    long[] symbols = new long [(int)num.getValue()];
                    clingoLibrary.clingo_model_symbols(event, 2, symbols, new SizeT(num.getValue()));
//                    System.out.print("ANSWER:");
                    for (int i = 0; i < num.getValue(); ++i) {
                        SizeTByReference len = new SizeTByReference();
                        clingoLibrary.clingo_symbol_to_string_size(symbols[i], len);
                        byte[] str = new byte[(int)len.getValue()];
                        clingoLibrary.clingo_symbol_to_string(symbols[i], str, new SizeT(len.getValue()));
//                        System.out.format(" %s", new String(str));
                        String symbol = new String(str);
						solution.addSymbol(symbol.trim());
                    }
                    System.out.println();

                }
                return true;
            }
        };
        PointerByReference hnd = new PointerByReference();
        clingoLibrary.clingo_control_solve(controlPointer.getValue(), 0, null, new SizeT(0), cb, null, hnd);
        IntByReference res = new IntByReference();
        clingoLibrary.clingo_solve_handle_get(hnd.getValue(), res);
        clingoLibrary.clingo_solve_handle_close(hnd.getValue());
        // clean up
        clingoLibrary.clingo_control_free(controlPointer.getValue());
		return solution;
	}

}
