package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.dto.Solution;

import com.sun.jna.Pointer;

public class CallbackTest {

	@Test
	public void test() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. b.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
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
//					solution.addSymbol(symbol);
//				}
//				return true;
//			}
//		};
//		Pointer handle = BaseClingo.controlSolve(control, SolveMode.ASYNC, null, 0, notify, null);
		
	}
}
