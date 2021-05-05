package org.potassco.base;

import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.GroundCallbackT;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;

/**
 * Intended to hold the control pointer and to free
 * the according memory in clingo.
 * clingo_control_new
 * clingo_control_free
 * 
 * @author Josef Schneeberger
 *
 */
public class Control implements AutoCloseable {
	private String name;
	private String logicProgram;
	private Pointer control;

	/**
	 * may not be called - retrieves no control pointer
	 */
	private Control() {
		super();
		this.name = "base";
	}

	public Control(String[] arguments) {
		this();
		Pointer logger = null;
		Pointer loggerData = null;
		int messageLimit = 20;
		this.control = BaseClingo.control(arguments, logger, loggerData, messageLimit);
	}

	public Control(String name, String[] arguments, String logicProgram) {
		this(arguments);
		this.name = name;
		this.logicProgram = logicProgram;
	}

	@Override
	public void close() throws Exception {
		BaseClingo.controlFree(this.control);
        System.out.println("Freed clingo control");
	}

	public void add(String name, String[] parameters, String string) {
		BaseClingo.controlAdd(this.control, name, null, string);
	}

	public void ground() {
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
        BaseClingo.controlGround(this.control, parts, new Size(1), null, null);
	}

	public void ground(GroundCallbackT groundCallbackT) {
		// TODO Auto-generated method stub
		
	}

	public Solution solve() {
		Solution solution = new Solution();
		BaseClingo.controlSolve(this.control, SolveMode.YIELD, null, 0, new SolveEventCallback() {
			
			@Override
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				// TODO Auto-generated method stub
				return false;
			}
		}, null);
		return solution;
	}

	public Solution solve2() throws ClingoException {
		Solution solution = new Solution();
		SolveEventCallback cb = new SolveEventCallback() {
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				SolveEventType t = SolveEventType.fromValue(type);
				switch (t) {
				case MODEL:
					long size = BaseClingo.modelSymbolsSize(event, ShowType.SHOWN);
					solution.setSize(size);
					long[] symbols = BaseClingo.modelSymbols(event, ShowType.SHOWN, size);
					for (int i = 0; i < size; ++i) {
						String symbol = BaseClingo.symbolToString(symbols[i]);
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
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.ASYNC, null, 0, cb, null);
		BaseClingo.solveHandleClose(handle);
		// clean up
		BaseClingo.controlFree(control);
		return solution;
	}

}
