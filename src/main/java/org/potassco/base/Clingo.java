package org.potassco.base;

import org.potassco.cpp.clingo_h;
import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;

public class Clingo extends BaseClingo {

	/**
	 * @param name
	 * {@link clingo_h#clingo_control_ground}
	 * @param control 
	 */
	public void ground(Pointer control, String name) {
	    Part[] parts = new Part[1];
	    parts[0] = new Part(name, null, new Size(0));
	    controlGround(control, parts, new Size(1), null, null);
	}

	public Solution solve(Pointer control) throws ClingoException {
			BaseClingo clingo = new BaseClingo();
	        Solution solution = new Solution();
	        SolveEventCallback cb = new SolveEventCallback() {
	            public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
	                SolveEventType t = SolveEventType.fromValue(type);
	                switch (t) {
	                    case MODEL:
	                    	long size = clingo.modelSymbolsSize(event, ShowType.SHOWN);
	                        solution.setSize(size);
	                        long[] symbols = clingo.modelSymbols(event, ShowType.SHOWN, size);
	                        for (int i = 0; i < size; ++i) {
	                            long len = clingo.symbolToStringSize(symbols[i]);
	                            String symbol = clingo.symbolToString(symbols[i], len);
	                            solution.addSymbol(symbol.trim());
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
	        Pointer handle = controlSolve(control, SolveMode.ASYNC, null, 0, cb, null);
	        clingo.solveHandleClose(handle);
	        // clean up
	        controlFree(control);
			return solution;
	    }

}
