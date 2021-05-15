package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.TermType;

import com.sun.jna.Pointer;

public class TheoryAtomsTest {

	/**
	 * {@link https://github.com/potassco/clingo/blob/master/libpyclingo/clingo/tests/test_atoms.py}
	 */
	@Test
	public void testTheoryAtoms1() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name,
				null,
				"#theory test { "
				+ "    t { }; "
				+ "    &a/0 : t, head; "
				+ "    &b/0 : t, {=}, t, head "
				+ "}.");
		BaseClingo.controlAdd(control, name, null, "{a; b}.");
		BaseClingo.controlAdd(control, name, null, "&a { 1; 2,3: a,b }.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer theoryAtoms = BaseClingo.controlTheoryAtoms(control);
		assertEquals(1, BaseClingo.theoryAtomsSize(theoryAtoms).intValue());
		assertEquals(TermType.SYMBOL, BaseClingo.theoryAtomsTermType(theoryAtoms, 0));
		assertEquals(0, BaseClingo.theoryAtomsTermNumber(theoryAtoms, 0));
		assertEquals("a", BaseClingo.theoryAtomsTermName(theoryAtoms, 0));
		long[] args = BaseClingo.theoryAtomsTermArguments(theoryAtoms, 0);
		assertEquals(null, args);
		assertEquals("a", BaseClingo.theoryAtomsTermToString(theoryAtoms, 0));
		long[] termIds = BaseClingo.theoryAtomsElementTuple(theoryAtoms, 0);
		assertEquals(0, BaseClingo.theoryAtomsElementCondition(theoryAtoms, 0).length);
		assertEquals(0, BaseClingo.theoryAtomsElementConditionId(theoryAtoms, 0));
		assertEquals("1", BaseClingo.theoryAtomsElementToString(theoryAtoms, 0));
	}

}
