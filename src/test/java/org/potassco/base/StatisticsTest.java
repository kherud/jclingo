package org.potassco.base;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;

public class StatisticsTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/statistics_8c-example.html
	 */
	@Test
	public void test() {
		Set<String> expected = new HashSet<>();
		expected.add("a");
		expected.add("b");
		int expectedModels = 2;
		
		String name = "base";
//		String[] arguments = {"0"};
		String[] arguments = {"-n", "0"};
//		String[] arguments = null;
		String program = "a :- not b. b :- not a.";
		Clingo clingo = new Clingo();
		Pointer control = clingo.control(arguments);
		Pointer conf = clingo.controlConfiguration(control);
		int rootKey = clingo.configurationRoot(conf);
		
		// configure to enumerate all models
		int subKey = clingo.configurationMapAt(conf, rootKey, "solve.models");
		clingo.configurationValueSet(conf, subKey, "0");
		
		int confSub = clingo.configurationMapAt(conf, rootKey, "stats");
		clingo.configurationValueSet(conf, confSub, "1");
		clingo.controlAdd(control, name, arguments, program);
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		clingo.controlGround(control, parts, new Size(1), null, null);
		SolveEventCallback eventHandler = null;
		Pointer handle = clingo.controlSolve(control, SolveMode.YIELD, null, 0, eventHandler , null);
		boolean modelExists = true;
		int m = 0;
		while (modelExists) {
			clingo.solveHandleResume(handle);
			Pointer model = clingo.solveHandleModel(handle);
			if (model != null) {
				// print_model
//				System.out.println("Model:");
				long numAtoms = clingo.modelSymbolsSize(model, ShowType.SHOWN);
				long[] atoms = clingo.modelSymbols(model, ShowType.SHOWN, numAtoms);
				for (int i = 0; i < atoms.length; i++) {
					long n = clingo.symbolToStringSize(atoms[i]);
					String str = clingo.symbolToString(atoms[i], n);
//					System.out.println(str);
					assertTrue(expected.contains(str.trim()));
				}
				m++;
			} else {
				modelExists = false;
			}
		}
		assertEquals(expectedModels, m);
	}

}
