package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.SolveMode;

import com.sun.jna.Pointer;

public class StatisticsTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/statistics_8c-example.html
	 */
	@Test
	public void test() {
		String name = "base";
		String[] arguments = null;
		String program = "a :- not b. b :- not a.";
		BaseClingo clingo = BaseClingo.getInstance();
		Pointer control = clingo.control(arguments);
		Pointer conf = clingo.controlConfiguration(control);
		int root = clingo.configurationRoot(conf);
		int confSub = clingo.configurationMapAt(conf, root, "stats");
		clingo.configurationValueSet(conf, confSub, "1");
		clingo.controlAdd(control, name, arguments, program);
//		clingo.controlGround(control, name);
//		Pointer handle = control.solve(SolveMode.YIELD, null, 0, eventHandler, null);
//		boolean modelExists = true;
//		while (modelExists) {
//			control.so
//		}
	}

}
