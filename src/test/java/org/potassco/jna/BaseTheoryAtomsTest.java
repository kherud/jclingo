package org.potassco.jna;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.potassco.enums.TermType;
import org.potassco.jna.BaseClingo;
import org.potassco.jna.Part;
import org.potassco.jna.Size;

import com.sun.jna.Pointer;

public class BaseTheoryAtomsTest {

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
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
		Pointer theoryAtoms = BaseClingo.controlTheoryAtoms(control);
		assertEquals(1, BaseClingo.theoryAtomsSize(theoryAtoms));
		assertEquals(TermType.SYMBOL, BaseClingo.theoryAtomsTermType(theoryAtoms, 0));
		assertEquals(0, BaseClingo.theoryAtomsTermNumber(theoryAtoms, 0));
		assertEquals("a", BaseClingo.theoryAtomsTermName(theoryAtoms, 0));
		long[] args = BaseClingo.theoryAtomsTermArguments(theoryAtoms, 0);
		assertEquals(null, args);
		assertEquals("a", BaseClingo.theoryAtomsTermToString(theoryAtoms, 0).trim());
		long[] termIds = BaseClingo.theoryAtomsElementTuple(theoryAtoms, 0);
		assertEquals(0, BaseClingo.theoryAtomsElementCondition(theoryAtoms, 0).length);
		assertEquals(0, BaseClingo.theoryAtomsElementConditionId(theoryAtoms, 0));
		assertEquals("1", BaseClingo.theoryAtomsElementToString(theoryAtoms, 0).trim());
	}

	/**
	 * {@link https://potassco.org/clingo/c-api/5.5/theory-atoms_8c-example.html}
	 */
	@Test
	public void testTheoryAtoms2() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name,
				null,
			    "#theory t {" +
			    "  term   { + : 1, binary, left };" +
			    "  &a/0 : term, any;" +
			    "  &b/1 : term, {=}, term, any" +
			    "}." +
			    "x :- &a { 1+2 }." +
			    "y :- &b(3) { } = 17.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
		Pointer theoryAtoms = BaseClingo.controlTheoryAtoms(control);
		assertEquals(2, BaseClingo.theoryAtomsSize(theoryAtoms));
		assertEquals(TermType.NUMBER, BaseClingo.theoryAtomsTermType(theoryAtoms, 0));
		assertEquals(3, BaseClingo.theoryAtomsTermNumber(theoryAtoms, 0));
		assertEquals(null, BaseClingo.theoryAtomsTermName(theoryAtoms, 0));
		long[] args = BaseClingo.theoryAtomsTermArguments(theoryAtoms, 0);
		assertEquals(null, args);
		assertEquals("3", BaseClingo.theoryAtomsTermToString(theoryAtoms, 0).trim());
		long[] termIds = BaseClingo.theoryAtomsElementTuple(theoryAtoms, 0);
		long[] condIds = BaseClingo.theoryAtomsElementCondition(theoryAtoms, 0);
		int condId = BaseClingo.theoryAtomsElementConditionId(theoryAtoms, 0);
		String taeString = BaseClingo.theoryAtomsElementToString(theoryAtoms, 0);
//		BaseClingo.theoryAtomsAtomTerm(theoryAtoms, atom);
	}

}
