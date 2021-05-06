package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.Part;
import org.potassco.jna.Size;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;

public class BaseCallbackTest {

	@Test
	public void test() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. b.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
		Solution solution = new Solution();
//		ClingoLibrary.clingo_solve_event_callback_t notify = new ClingoLibrary.clingo_solve_event_callback_t() {
//			
//			@Override
//			public boolean call(int type, Pointer event, Pointer data, ByteByReference goon) {
//				long size = BaseClingo.modelSymbolsSize(event, ShowType.SHOWN);
//				solution.setSize(size);
//				long[] symbols = BaseClingo.modelSymbols(event, ShowType.SHOWN, size);
//				for (int i = 0; i < size; ++i) {
//					String symbol = BaseClingo.symbolToString(symbols[i]);
//					solution.addSymbol(symbol.trim());
//				}
//				return true;
//			}
//		};
//		Pointer handle = BaseClingo.controlSolve(control, SolveMode.ASYNC, null, 0, notify, null);
		
	}
}
