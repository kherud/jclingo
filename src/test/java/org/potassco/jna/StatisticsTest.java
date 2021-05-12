package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.enums.StatisticsType;

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
		PartSt[] parts = new PartSt[1];
		parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, new SizeT(1L), null, null);
		SolveEventCallback eventHandler = null;
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), eventHandler, null);
		boolean modelExists = true;
		int m = 0;
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				// print_model
//				System.out.println("ModelSt:");
				long[] atoms = BaseClingo.modelSymbols(model, ShowType.SHOWN);
				for (int i = 0; i < atoms.length; i++) {
					String str = BaseClingo.symbolToString(atoms[i]);
//					System.out.println(str);
					assertTrue(expected.contains(str));
				}
				m++;
			} else {
				modelExists = false;
			}
		}
		assertEquals(expectedModels, m);
	}

	/**
	 * https://potassco.org/clingo/c-api/5.5/statistics_8c-example.html
	 */
	@Test
	public void testStatistics() {
		String name = "base";
		String[] args = {"0"};
		Pointer control = BaseClingo.control(args, null, null, 0);
		Pointer config = BaseClingo.controlConfiguration(control);
		int configRoot = BaseClingo.configurationRoot(config);
		// and set the statistics level to one to get more statistics
		int configSub = BaseClingo.configurationMapAt(config, configRoot, "stats");
		BaseClingo.configurationValueSet(config, configSub, "1");
		BaseClingo.controlAdd(control, name, null, "a :- not b. b :- not a.");
		int solveRet = solve(control);
		// get the statistics object, get the root key, then print the statistics recursively
		Pointer stats = BaseClingo.controlStatistics(control);
		long statsKey = BaseClingo.statisticsRoot(stats);
		checkStatistics(stats, statsKey, 0);
	}

	private void checkStatistics(Pointer stats, long key, int depth) {
		StatisticsType type = BaseClingo.statisticsType(stats, key);
		switch (type) {
		case VALUE: {
			double value = BaseClingo.statisticsValueGet(stats, key);
			System.out.println(prefix(depth) + value);
			break;
		}
		case ARRAY: {
			SizeT size = BaseClingo.statisticsArraySize(stats, key);
			for (int j = 0; j < size.intValue(); j++) {
				long subkey = BaseClingo.statisticsArrayAt(stats, key, new SizeT(j));
				System.out.println(prefix(depth) + j);
		        // recursively print subentry
				checkStatistics(stats, subkey, depth + 1);
			}
			break;
		}
		case MAP: {
			SizeT size = BaseClingo.statisticsMapSize(stats, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.statisticsMapSubkeyName(stats, key, new SizeT(j));
				long subkey = BaseClingo.statisticsMapAt(stats, key, name);
				System.out.println(prefix(depth) + name);
		        // recursively print subentry
				checkStatistics(stats, subkey, depth + 1);
			}
			break;
		}
		case EMPTY: {
			// this case won't occur if the statistics are traversed like this
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}

	private String prefix(int depth) {
		String result = "";
		for (int i = 0; i < depth; i++) {
			result = result + " ";
		}
		return result;
	}

	private int solve(Pointer control) {
		SolveEventCallback eventHandler = new SolveEventCallback() {
			@Override
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				int sum = 0;
				long value;
				switch (SolveEventType.fromValue(type)) {
				case STATISTICS:
					// obtain a pointer to the accumulated statistics
					Pointer stats = event.getPointerArray(0)[1];
					// get the root key which referring to a special modifiable entry
					long root = BaseClingo.statisticsRoot(stats);
					// set some pseudo-random values in an array
					long values = BaseClingo.statisticsMapAddSubkey(stats, root, "values", StatisticsType.ARRAY);
					for (int i = 0; i < 10; ++i) {
						int random = ThreadLocalRandom.current().nextInt(1, 999999999 + 1);
						value = BaseClingo.statisticsArrayPush(stats, values, StatisticsType.VALUE);
						BaseClingo.statisticsValueSet(stats, value, random);
				        sum += random;
					}
					// add the sum and average of the values in a map under key summary
					long summary = BaseClingo.statisticsMapAddSubkey(stats, root, "summary", StatisticsType.MAP);
					value = BaseClingo.statisticsMapAddSubkey(stats, summary, "sum", StatisticsType.VALUE);
					BaseClingo.statisticsValueSet(stats, value, sum);
					value = BaseClingo.statisticsMapAddSubkey(stats, summary, "avg", StatisticsType.VALUE);
					BaseClingo.statisticsValueSet(stats, value, (double)sum/10);
					break;

				case MODEL:
					break;
					
				default:
					break;
				}
				return true;
			}
		};
		// get a solve handle
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), eventHandler, null);
		boolean modelExists = true;
		// loop over all models
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				checkModel(model);
			} else {
				modelExists = false;
			}
		}
		return BaseClingo.solveHandleGet(handle);
	}

	private void checkModel(Pointer model) {
		// TODO Auto-generated method stub
		
	}

}
