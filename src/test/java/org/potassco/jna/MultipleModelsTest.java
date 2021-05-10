package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.SolveMode;

import com.sun.jna.Pointer;

public class MultipleModelsTest {

	@Test
	public void test() {
		String name = "base";
		String program = "1 {p(1..3)} 2.";
		String[] arguments = { "0" }; // enumerate all models
		Pointer control = BaseClingo.control(arguments, null, null, 0);
		BaseClingo.controlAdd(control, name, null, program);
		PartSt[] parts = new PartSt[1];
		parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, 1L, null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, 0, null, null);
		boolean modelExits = true;
		int i = 0;
		while (modelExits) {
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				long mn = BaseClingo.modelNumber(model);
				BaseClingo.solveHandleResume(handle);
				i++;
			} else {
				modelExits = false;
			}
		}
		assertEquals(6, i);
	}

}
