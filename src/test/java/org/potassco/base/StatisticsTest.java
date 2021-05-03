package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sun.jna.Pointer;

public class StatisticsTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/statistics_8c-example.html
	 */
	@Test
	public void test() {
		String name = "base";
		String[] arguments = {"-t", "2", "--stats=2"};
		String program = "1 { a; b }.";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control(name, arguments, program);
		Pointer conf = control.configuration();
		long root = clingo.configurationRoot(conf);
		int confSub = clingo.configurationMapAt(conf, root, "stats");
	}

}
