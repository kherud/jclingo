package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.SolveMode;

import com.sun.jna.Pointer;

public class MultipleModelsTest {

	@Test
	public void test() {
		String name = "base";
		String program = "1 {p(1..3)} 2.";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control();
		control.controlNew(null);
		control.add(name, null, 0L, program);
		control.ground(name);
		Pointer handle = control.solve(SolveMode.YIELD, null, 0, null, null);
		boolean modelExits = true;
		while (modelExits) {
			Pointer model = clingo.solveHandleModel(handle);
			if (model != null) {
				long mn = clingo.modelNumber(model);
				System.out.println(mn);
				clingo.solveHandleResume(handle);
			} else {
				modelExits = false;
			}
		}
	}

}
