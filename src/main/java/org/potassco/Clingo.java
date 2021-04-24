package org.potassco;

import org.potassco.c4j.SolveResult;
//import org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_model;
//import org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_solve_event_callback_t;
//import org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_solve_handle;
//import org.lorislab.clingo4j.util.EnumValue;
//import org.potassco.c4j.Model;
//import org.potassco.c4j.SolveHandle;
import org.potassco.callback.SolveEventHandler;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.PartT;
import org.potassco.jna.SizeT;
import org.potassco.jna.SizeTByReference;
import org.potassco.jna.SolveEventCallbackT;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

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
        clingoLibrary.clingo_control_new(null, new SizeT(0), null, null, 20, controlPointer);
        // add the program
		clingoLibrary.clingo_control_add(controlPointer.getValue(), name, null, new SizeT(0), logicProgram);
	}

	public String version() {
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        IntByReference patch = new IntByReference();
        clingoLibrary.clingo_version(major, minor, patch);
		return major.getValue() + "." + minor.getValue() + "." + patch.getValue();
	}

	public void add(String name, String logicProgram) {
        this.controlPointer = new PointerByReference();
        clingoLibrary.clingo_control_new(null, new SizeT(0), null, null, 20, controlPointer);
        // add the program
		clingoLibrary.clingo_control_add(controlPointer.getValue(), name, null, new SizeT(0), logicProgram);
	}
	
	public void ground(String name) {
        PartT[] parts = new PartT [1];
        parts[0] = new PartT();
		parts[0].name = name;
        parts[0].params = null;
        parts[0].size = new SizeT(0);
        clingoLibrary.clingo_control_ground(controlPointer.getValue(), parts, new SizeT(1), null, null);
	}

    public SolveHandle solve() throws ClingoException {
        return solve(new SolveEventHandler() {
			
			@Override
			public boolean onModel(Model model) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFinish(SolveResult result) {
				// TODO Auto-generated method stub
				
			}
		}, SolveMode.YIELD);
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
                SolveEventType t = SolveEventType.castIntToEnum(type);
                switch (t) {
                    case MODEL:
//                        Model model = new Model((Pointer<clingo_model>) event);
                    	Model model = new Model(event);
                        boolean tmp = handler.onModel(model);
//                        goon.set(tmp);
                        break;
                    case FINISH:
//                        Pointer<Integer> p_event = (Pointer<Integer>) event;
//                        handler.onFinish(new SolveResult(p_event.get()));
//                        goon.set(true);
                        return true;
                }
                if (type == 0) {
                    SizeTByReference num = new SizeTByReference();
                    clingoLibrary.clingo_model_symbols_size(event, 2, num);
                    solveHandle.setSize((int) num.getValue());
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
                        solveHandle.addSymbol(symbol.trim());
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
		return solveHandle;
		
		/*
        Pointer<clingo_solve_event_callback_t> p_event = null;
        if (handler != null) {
            clingo_solve_event_callback_t event = new clingo_solve_event_callback_t() {
                @Override
                public boolean apply(int type, Pointer<?> event, Pointer<?> data, Pointer<Boolean> goon) {
                    SolveEventType t = EnumValue.valueOfInt(SolveEventType.class, type);
                    switch (t) {
                        case MODEL:
                            Model model = new Model((Pointer<clingo_model>) event);
                            boolean tmp = handler.onModel(model);
                            goon.set(tmp);
                            break;
                        case FINISH:
                            Pointer<Integer> p_event = (Pointer<Integer>) event;
                            handler.onFinish(new SolveResult(p_event.get()));
                            goon.set(true);
                            return true;
                    }
                    return apply(type, Pointer.getPeer(event), Pointer.getPeer(data), Pointer.getPeer(goon));
                }
            };
            p_event = Pointer.allocate(ClingoLibrary.clingo_solve_event_callback_t.class);
            p_event.set(event);
        }

        // get a solve handle        
        final Pointer<Pointer<clingo_solve_handle>> handle = Pointer.allocatePointer(ClingoLibrary.clingo_solve_handle.class);
        handleError(LIB.clingo_control_solve(pointer, mode, null, 0, p_event, null, handle), "Error execute control solve");
        return new SolveHandle(handle.get());
	*/
    }

}
