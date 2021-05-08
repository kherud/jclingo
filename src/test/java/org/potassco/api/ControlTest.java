package org.potassco.api;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * https://github.com/potassco/clingo/blob/master/libpyclingo/clingo/tests/test_control.py
 *
 */
public class ControlTest {

	@Test
	public void testGround() {
		Clingo clingo = new Clingo(); 
		Control control = clingo.control(null);
		String[] parameters = {"c"};
		control.add("part", parameters , "p(@cb_num(c)).");
//		control.ground(parts, null, null, null);
	}

}
