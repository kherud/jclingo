package org.potassco;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.potassco.base.Clingo;
import org.potassco.callback.SolveEventCallback;
import org.potassco.dto.Solution;
import org.potassco.enums.ModelType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;

import com.sun.jna.Pointer;

public class SolveTest1 {

	/**
	 * https://potassco.org/clingo/c-api/5.5/model_8c-example.html
	 */
	@Test
	public void testAb() {
		String name = "base";
		Clingo clingo = new Clingo(name, "1 {a; b} 1. #show c : b. #show a/0.");
		clingo.ground(name);
		Pointer control = clingo.getControl();
		Pointer handle = clingo.controlSolve(control, SolveMode.YIELD, null, 0, null, null);
		boolean modelExits = true;
		while (modelExits) {
			clingo.solveHandleResume(handle);
			Pointer model = clingo.solveHandleModel(handle);
			if (model == null) {
				modelExits = false;
			} else {
				assertEquals(ModelType.STABLE_MODEL, clingo.modelType(model));
				int modelNumber = clingo.modelNumber(model);
				System.out.println("Stable model: " + modelNumber);
				assertEquals(1, modelNumber);
				Set<String> s1 = new HashSet<String>();
				s1.add("c");
				assertEquals(s1, testSolution(clingo, model, ShowType.SHOWN));
				Set<String> s2 = new HashSet<String>();
				s2.add("b");
				assertEquals(s2, testSolution(clingo, model, ShowType.ATOMS));
				Set<String> s3 = new HashSet<String>();
				s3.add("c");
				assertEquals(s3, testSolution(clingo, model, ShowType.TERMS));
				Set<String> s4 = new HashSet<String>();
				assertEquals(s4, testSolution(clingo, model, ShowType.COMPLEMENT));
			}
		}
        clingo.solveHandleClose(handle);
        // clean up
        clingo.controlFree(control);
        fail("Result differs from origin.");
	}

	private Set<String> testSolution(Clingo clingo, Pointer model, ShowType shownType) {
		Set<String> result = new HashSet<String>();
		long size = clingo.modelSymbolsSize(model, shownType);
		long[] symbols = clingo.modelSymbols(model, shownType, size);
		for (long s : symbols) {
			result.add(clingo.symbolName(s));
		}
		System.out.println(Arrays.toString(result.toArray()));
		return result;
	}

// in callback
//	@Override
//	public boolean callback(Pointer model, Pointer data, Pointer goon) {
//    	long size = clingo.modelSymbolsSize(event, ShowType.SHOWN);
//    	solution .setSize(size);
//        long[] symbols = clingo.modelSymbols(event, ShowType.SHOWN, size);
//        for (int i = 0; i < size; ++i) {
//            long len = clingo.symbolToStringSize(symbols[i]);
//            String symbol = clingo.symbolToString(symbols[i], len);
//            solution.addSymbol(symbol.trim());
//        }
//		return true;
//	}
	
	/**
	 * https://github.com/potassco/clingo/blob/master/libpyclingo/clingo/tests/test_solving.py
	 */
	@Test
	public void testSolveAsync() {
		fail("Not yet implemented.");
	}
}
