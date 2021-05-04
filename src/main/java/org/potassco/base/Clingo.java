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
import com.sun.jna.StringArray;
import com.sun.jna.ptr.PointerByReference;

public class Clingo extends BaseClingo {

	/**
	 * Create a new control object.
	 * <p>
	 * A control object has to be freed using clingo_control_free(). TODO: This will
	 * be done in the Control class.
	 * 
	 * @param arguments array of command line arguments
	 * @return resulting control object
	 */
	public Pointer control(String[] arguments) {
		int argumentsLength;
		StringArray args;
		if (arguments == null) {
			argumentsLength = 0;
			args = null;
		} else {
			argumentsLength = arguments.length;
			args = new StringArray(arguments);
		}
		Pointer logger = null;
		Pointer loggerData = null;
		int messageLimit = 20;
		// clingoLibrary.clingo_control_new(null, 0, null, null, 20, controlPointer);
		PointerByReference ctrl = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_new(args, argumentsLength, logger, loggerData, messageLimit, ctrl);
		return ctrl.getValue();
	}

	/**
	 * @param name    {@link clingo_h#clingo_control_ground}
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
					// Pointer<Integer> p_event = (Pointer<Integer>) event;
					// handler.onFinish(new SolveResult(p_event.get()));
					// goon.set(true);
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
