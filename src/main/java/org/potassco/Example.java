package org.potassco;

import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.PartT;
import org.potassco.jna.SizeT;
import org.potassco.jna.SizeTByReference;
import org.potassco.jna.SolveEventCallbackT;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class Example {
    public static void main(String[] args) {
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        IntByReference patch = new IntByReference();
        ClingoLibrary.INSTANCE.clingo_version(major, minor, patch);
        System.out.printf("clingo version: %d.%d.%d\n", major.getValue(), minor.getValue(), patch.getValue());

        PointerByReference ctl = new PointerByReference();
        ClingoLibrary.INSTANCE.clingo_control_new(null, new SizeT(0), null, null, 20, ctl);
        // add a program
        ClingoLibrary.INSTANCE.clingo_control_add(ctl.getValue(), "base", null, new SizeT(0), "a. b.");
        // ground it
        PartT[] parts = new PartT [1];
        parts[0] = new PartT();
        parts[0].name = "base";
        parts[0].params = null;
        parts[0].size = new SizeT(0);
        ClingoLibrary.INSTANCE.clingo_control_ground(ctl.getValue(), parts, new SizeT(1), null, null);
        // solve it
        SolveEventCallbackT cb = new SolveEventCallbackT() {
            public boolean call(int type, Pointer event, Pointer goon) {
                if (type == 0) {
                    SizeTByReference num = new SizeTByReference();
                    ClingoLibrary.INSTANCE.clingo_model_symbols_size(event, 2, num);
                    System.out.printf("model: %d\n", num.getValue());
                    long[] symbols = new long [(int)num.getValue()];
                    ClingoLibrary.INSTANCE.clingo_model_symbols(event, 2, symbols, new SizeT(num.getValue()));
                    System.out.print("ANSWER:");
                    for (int i = 0; i < num.getValue(); ++i) {
                        SizeTByReference len = new SizeTByReference();
                        ClingoLibrary.INSTANCE.clingo_symbol_to_string_size(symbols[i], len);
                        byte[] str = new byte[(int)len.getValue()];
                        ClingoLibrary.INSTANCE.clingo_symbol_to_string(symbols[i], str, new SizeT(len.getValue()));
                        System.out.format(" %s", new String(str));
                    }
                    System.out.println();

                }
                return true;
            }
        };
        PointerByReference hnd = new PointerByReference();
        ClingoLibrary.INSTANCE.clingo_control_solve(ctl.getValue(), 0, null, new SizeT(0), cb, null, hnd);
        IntByReference res = new IntByReference();
        ClingoLibrary.INSTANCE.clingo_solve_handle_get(hnd.getValue(), res);
        ClingoLibrary.INSTANCE.clingo_solve_handle_close(hnd.getValue());
        // clean up
        ClingoLibrary.INSTANCE.clingo_control_free(ctl.getValue());
    }
}
