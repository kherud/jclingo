package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;
import org.potassco.util.ConfigurationTree;
import org.potassco.util.PropertyTree;

import com.sun.jna.Pointer;

public class ConfigurationCTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/configuration_8c-example.html
	 */
	@Test
	public void test() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
        // get the configuration object and its root key
        Pointer conf = BaseClingo.controlConfiguration(control);
        int rootKey = BaseClingo.configurationRoot(conf);
        // configure to enumerate all models
        int subKey = BaseClingo.configurationMapAt(conf, rootKey, "solve.models");
        BaseClingo.configurationValueSet(conf, subKey, "0");
        // configure the first solver to use the berkmin heuristic
        subKey = BaseClingo.configurationMapAt(conf, rootKey, "solver");
        subKey = BaseClingo.configurationArrayAt(conf, subKey, new SizeT());
        subKey = BaseClingo.configurationMapAt(conf, subKey, "heuristic");
        BaseClingo.configurationValueSet(conf, subKey, "berkmin");
        // note that the solver entry can be used both as an array and a map
        // if used as a map, this simply sets the configuration of the first solver and
        // is equivalent to the code above
        // add a logic program to the base part
        BaseClingo.controlAdd(control, name, null, "a :- not b. b :- not a.");
        // ground the base part
		BaseClingo.controlGround(control, parts, null, null);
        // solve
		int result = solve(control);
		ConfigurationTree tree = new ConfigurationTree(conf, Math.toIntExact(rootKey));
//		tree.showXml(); // insert to output configuration
		assertEquals("berkmin,0", tree.queryXpathAsString("/ClingoConfiguration/solver/heuristic/text()"));
		assertEquals("0", tree.queryXpathAsString("/ClingoConfiguration/solve/models/text()"));
		// check solve.models
        int smKey = BaseClingo.configurationMapAt(conf, rootKey, "solve.models");
        assertEquals("0", BaseClingo.configurationValueGet(conf, smKey));
        // search for more information
//        SizeT rootSize = BaseClingo.configurationMapSize(conf, rootKey);
//        for (int j = 0; j <= rootSize.intValue(); j++) {
//			System.out.println(BaseClingo.configurationMapSubkeyName(conf, rootKey, new SizeT(j)));
//		}
	}

	private int solve(Pointer control) {
		// get a solve handle
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), null, null);
		// loop over all models
		boolean modelExists = true;
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model == null) {
			    // stop if there are no more models
				modelExists = false;
			} else {
				checkModel(model);
			}
		}
		// close the solve handle
		return BaseClingo.solveHandleGet(handle);
	}

	private void checkModel(Pointer model) {
		// retrieve the symbols in the model
		long[] atoms = BaseClingo.modelSymbols(model, ShowType.SHOWN);
		List<String> atomsAsStrings = new ArrayList<String>();
		for (long a : atoms) {
			atomsAsStrings.add(BaseClingo.symbolToString(a));
		}
		String[] strArray = { "a", "b" };
		
		assertEquals(1, atoms.length);
		assertTrue(Arrays.asList(strArray).contains(atomsAsStrings.get(0)));
	}

}
