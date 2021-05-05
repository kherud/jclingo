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

public class BaseStatisticsTest {

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
		String[] arguments = { "-n", "0" };
//		String[] arguments = null;
		String program = "a :- not b. b :- not a.";
		Pointer control = BaseClingo.control(arguments, null, null, 0);
		Pointer conf = BaseClingo.controlConfiguration(control);
		int rootKey = BaseClingo.configurationRoot(conf);

// configure to enumerate all models
//		int subKey = BaseClingo.configurationMapAt(conf, rootKey, "solve.models");
//		BaseClingo.configurationValueSet(conf, subKey, "0");

		int confSub = BaseClingo.configurationMapAt(conf, rootKey, "stats");
		BaseClingo.configurationValueSet(conf, confSub, "1");
		BaseClingo.controlAdd(control, name, null, program);
		Part[] parts = new Part[1];
		parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
		SolveEventCallback eventHandler = null;
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, 0, eventHandler, null);
		boolean modelExists = true;
		int m = 0;
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				// print_model
//				System.out.println("Model:");
				long numAtoms = BaseClingo.modelSymbolsSize(model, ShowType.SHOWN);
				long[] atoms = BaseClingo.modelSymbols(model, ShowType.SHOWN, numAtoms);
				for (int i = 0; i < atoms.length; i++) {
					String str = BaseClingo.symbolToString(atoms[i]);
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
