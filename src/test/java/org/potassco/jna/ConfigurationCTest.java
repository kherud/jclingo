package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveMode;
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
		PropertyTree tree = new PropertyTree("ClingoConfiguration");
		checkConfiguration(conf, Math.toIntExact(rootKey), 0, tree);
		tree.showXml(); // insert to output configuration
		assertEquals("berkmin,0", tree.queryXpathAsString("/ClingoConfiguration/solver/heuristic/text()"));
        int smKey = BaseClingo.configurationMapAt(conf, rootKey, "solve.models");
        assertEquals("0", BaseClingo.configurationValueGet(conf, smKey));
	}

	// TODO: solve.models not contained in conf tree
	private void checkConfiguration(Pointer conf, int key, int depth, PropertyTree tree) {
		int type = BaseClingo.configurationType(conf, key);
		switch (type) {
		case 1: {
			String value = BaseClingo.configurationValueGet(conf, key);
			tree.addValue(value, depth);
			break;
		}
		case 2: {
			SizeT size = BaseClingo.configurationArraySize(conf, key);
			for (int j = 0; j < size.intValue(); j++) {
				long subkey = BaseClingo.configurationArrayAt(conf, key, new SizeT(j));
				String desc = BaseClingo.configurationDescription(conf, key);
				tree.addIndex(j, desc, depth);
		        // recursively print subentry
				checkConfiguration(conf, Math.toIntExact(subkey), depth + 1, tree);
			}
			break;
		}
		case 4: {
			SizeT size = BaseClingo.configurationMapSize(conf, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.configurationMapSubkeyName(conf, key, new SizeT(j));
				long subkey = BaseClingo.configurationMapAt(conf, key, name);
				String desc = BaseClingo.configurationDescription(conf, key);
				tree.addNode(name, desc, depth);
		        // recursively print subentry
				checkConfiguration(conf, Math.toIntExact(subkey), depth + 1, tree);
			}
			break;
		}
		case 5: {
			SizeT size = BaseClingo.configurationMapSize(conf, key);
			String v1 = BaseClingo.configurationValueGet(conf, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.configurationMapSubkeyName(conf, key, new SizeT(j));
				long subkey = BaseClingo.configurationMapAt(conf, key, name);
				String desc = BaseClingo.configurationDescription(conf, key);
				String v2 = BaseClingo.configurationValueGet(conf, Math.toIntExact(subkey));
				tree.addNode(name, desc, depth);
		        // recursively print subentry
				checkConfiguration(conf, Math.toIntExact(subkey), depth + 1, tree);
			}
			break;
		}
		case 6: {
//			SizeT as = BaseClingo.configurationArraySize(conf, key);
			SizeT size = BaseClingo.configurationMapSize(conf, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.configurationMapSubkeyName(conf, key, new SizeT(j));
				int arraySubkey = BaseClingo.configurationArrayAt(conf, key, new SizeT(j));
				long subkey = BaseClingo.configurationMapAt(conf, key, name);
				String desc = BaseClingo.configurationDescription(conf, key);
				String value = BaseClingo.configurationValueGet(conf, Math.toIntExact(subkey));
				
				int arraySkCt = BaseClingo.configurationType(conf, arraySubkey);
//				checkConfiguration(conf, arraySubkey, depth + 1, tree);
				
				int mapSkCt = BaseClingo.configurationType(conf, Math.toIntExact(subkey));
				
				tree.addNodeValue(name, desc, value, depth);
		        // recursively print subentry
				checkConfiguration(conf, Math.toIntExact(subkey), depth + 1, tree);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
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
