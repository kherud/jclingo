package org.potassco.base;

import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.SolveEventCallbackT;

import com.sun.jna.Pointer;

public class ClingoHelper {
	private Clingo clingo;

    public ClingoHelper(Clingo clingo) {
		super();
		this.clingo = clingo;
	}

	public Solution solve() throws ClingoException {
        Solution solveHandle = new Solution();
        SolveEventCallbackT cb = new SolveEventCallbackT() {
            public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
                SolveEventType t = SolveEventType.fromValue(type);
                switch (t) {
                    case MODEL:
                    	long size = clingo.modelSymbolsSize(event, ShowType.SHOWN);
                        solveHandle.setSize(size);
                        long[] symbols = clingo.modelSymbols(event, ShowType.SHOWN, size);
                        for (int i = 0; i < size; ++i) {
                            long len = clingo.symbolToStringSize(symbols[i]);
                            String symbol = clingo.symbolToString(symbols[i], len);
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
		Pointer control = clingo.getControl();
        Pointer handle = clingo.controlSolve(control, SolveMode.ASYNC, null, 0, cb, null);
        clingo.solveHandleClose(handle);
        // clean up
        clingo.controlFree(control);
		return solveHandle;
    }

}
