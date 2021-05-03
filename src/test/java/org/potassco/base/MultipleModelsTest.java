package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.base.Clingo;
import org.potassco.base.Control;
import org.potassco.enums.ModelType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.SolveEventCallbackT;

import com.sun.jna.Pointer;

public class MultipleModelsTest {

	@Test
	public void test() {
		String name = "base";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control(name, "1 {p(1..3)} 2.");
		control.ground(name);
		Pointer handle = control.solve(SolveMode.YIELD, null, 0, null, null);
		boolean condition = true;
		while (condition) {
			Pointer model = clingo.solveHandleModel(handle);
			if (model != null) {
				long mn = clingo.modelNumber(model);
				System.out.println(mn);
				clingo.solveHandleResume(handle);
			} else {
				condition = false;
			}
		}
	}

}
