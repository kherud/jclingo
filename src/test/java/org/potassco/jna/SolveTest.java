package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.potassco.enums.ModelType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.BaseClingo;
import org.potassco.jna.Part;
import org.potassco.jna.Size;

import com.sun.jna.Pointer;

public class SolveTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/model_8c-example.html
	 */
	@Test
	public void testAb1() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "1 {a; b} 1. #show c : b. #show a/0.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, 0, null, null);
		boolean modelExits = true;
		while (modelExits) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model == null) {
				modelExits = false;
			} else {
				assertEquals(ModelType.STABLE_MODEL, BaseClingo.modelType(model));
				long modelNumber = BaseClingo.modelNumber(model);
				System.out.println("Stable model: " + modelNumber);
				assertEquals(1, modelNumber);
				Set<String> s1 = new HashSet<String>();
				s1.add("c");
				assertEquals(s1, checkModel(model, ShowType.SHOWN));
				Set<String> s2 = new HashSet<String>();
				s2.add("b");
				assertEquals(s2, checkModel(model, ShowType.ATOMS));
				Set<String> s3 = new HashSet<String>();
				s3.add("c");
				assertEquals(s3, checkModel(model, ShowType.TERMS));
				Set<String> s4 = new HashSet<String>();
				assertEquals(s4, checkModel(model, ShowType.COMPLEMENT));
			}
		}
        BaseClingo.solveHandleClose(handle);
        // clean up
        BaseClingo.controlFree(control);
        fail("Result differs from origin.");
	}

	/**
	 * https://potassco.org/clingo/c-api/5.5/model_8c-example.html
	 */
	@Test
	public void testAb2() {
		String name = "base";
		String program = "1 {a; b} 1. #show c : b. #show a/0.";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, program);
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
        BaseClingo.controlGround(control, parts, new Size(1), null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, 0, null, null);
		boolean modelExits = true;
		while (modelExits) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model == null) {
				modelExits = false;
			} else {
				assertEquals(ModelType.STABLE_MODEL, BaseClingo.modelType(model));
				long modelNumber = BaseClingo.modelNumber(model);
				System.out.println("Stable model: " + modelNumber);
				assertEquals(1, modelNumber);
				Set<String> s1 = new HashSet<String>();
				s1.add("c");
				assertEquals(s1, checkModel(model, ShowType.SHOWN));
				Set<String> s2 = new HashSet<String>();
				s2.add("b");
				assertEquals(s2, checkModel(model, ShowType.ATOMS));
				Set<String> s3 = new HashSet<String>();
				s3.add("c");
				assertEquals(s3, checkModel(model, ShowType.TERMS));
				Set<String> s4 = new HashSet<String>();
				assertEquals(s4, checkModel(model, ShowType.COMPLEMENT));
			}
		}
        BaseClingo.solveHandleClose(handle);
        // clean up
        BaseClingo.controlFree(control);
        fail("Result differs from origin.");
	}

	private Set<String> checkModel(Pointer model, ShowType shownType) {
		Set<String> result = new HashSet<String>();
		long size = BaseClingo.modelSymbolsSize(model, shownType);
		long[] symbols = BaseClingo.modelSymbols(model, shownType, size);
		for (long s : symbols) {
			result.add(BaseClingo.symbolName(s));
		}
		System.out.println(Arrays.toString(result.toArray()));
		return result;
	}

// in callback
//	@Override
//	public boolean callback(Pointer model, Pointer data, Pointer goon) {
//    	long size = BaseClingo.modelSymbolsSize(event, ShowType.SHOWN);
//    	solution .setSize(size);
//        long[] symbols = BaseClingo.modelSymbols(event, ShowType.SHOWN, size);
//        for (int i = 0; i < size; ++i) {
//            long len = BaseClingo.symbolToStringSize(symbols[i]);
//            String symbol = BaseClingo.symbolToString(symbols[i], len);
//            solution.addSymbol(symbol.trim());
//        }
//		return true;
//	}
	
	/**
	 * https://github.com/potassco/clingo/blob/master/libpyclingo/clingo/tests/test_solving.py
	 */
//	@Test
//	public void testSolveAsync() {
//		fail("Not yet implemented.");
//	}
}
