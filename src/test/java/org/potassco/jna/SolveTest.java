package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.potassco.enums.ModelType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;

import com.sun.jna.Pointer;

public class SolveTest {
	private static final Map<Integer, String[]> EXPECTED;
	static {
		String[] sol1 = { "c", "b", "c", "a" }; 
		String[] sol2 = { "a", "a", "", "b" };
		EXPECTED = new HashMap<Integer, String[]>();
		EXPECTED.put(1, sol1);
		EXPECTED.put(2, sol2);
	}
	
	/**
	 * https://potassco.org/clingo/c-api/5.5/model_8c-example.html
	 */
	@Test
	public void testAb1() {
		String name = "base";
		String program = "1 {a; b} 1. #show c : b. #show a/0.";
		String[] arguments = { "0" };
		Pointer control = BaseClingo.control(arguments, null, null, 0);
		BaseClingo.controlAdd(control, name, null, program);
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		solve(control);
        fail("Result differs from origin.");
	}

	private void solve(Pointer control) {
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), null, null);
		boolean modelExits = true;
		int i = 0;
		while (modelExits) {
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				i = i + 1;
				checkModel1(model, i);
				BaseClingo.solveHandleResume(handle);
			} else {
				modelExits = false;
			}
		}
        BaseClingo.solveHandleClose(handle);
        // clean up
        BaseClingo.controlFree(control);
	}

	private void checkModel1(Pointer model, int modelCount) {
		assertEquals(ModelType.STABLE_MODEL, BaseClingo.modelType(model));
		checkModel2(model, modelCount, 0, ShowType.SHOWN);
		checkModel2(model, modelCount, 1, ShowType.ATOMS);
		checkModel2(model, modelCount, 2, ShowType.TERMS);
		checkModel2(model, modelCount, 3, ShowType.COMPLEMENT, ShowType.ATOMS);
	}

	private void checkModel2(Pointer model, int modelCount, int expectedIndex, ShowType... shownType) {
		long modelNumber = BaseClingo.modelNumber(model);
		assertEquals(modelNumber, modelCount);
		Set<String> atomsAsString = new HashSet<String>();
		for (ShowType st : shownType) {
			for (long symbol : BaseClingo.modelSymbols(model, st)) {
				atomsAsString.add(BaseClingo.symbolName(symbol));
			}
		}
		// there is at most one atom in the shown model 
		if (atomsAsString.size() == 1) {
			String a = atomsAsString.iterator().next();
			assertEquals(EXPECTED.get((int) modelNumber)[expectedIndex], a);
		} else if (atomsAsString.size() == 0) {
			assertEquals(EXPECTED.get((int) modelNumber)[expectedIndex], "");
		}
	}

}
