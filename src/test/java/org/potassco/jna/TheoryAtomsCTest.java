package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.TermType;

import com.sun.jna.Pointer;

public class TheoryAtomsCTest {

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
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer theoryAtoms = BaseClingo.controlTheoryAtoms(control);
		assertEquals(2, BaseClingo.theoryAtomsSize(theoryAtoms).intValue());
		assertEquals(TermType.NUMBER, BaseClingo.theoryAtomsTermType(theoryAtoms, 0));
		assertEquals(3, BaseClingo.theoryAtomsTermNumber(theoryAtoms, 0));
		assertEquals(null, BaseClingo.theoryAtomsTermName(theoryAtoms, 0));
		long[] args = BaseClingo.theoryAtomsTermArguments(theoryAtoms, 0);
		assertEquals(null, args);
		assertEquals("3", BaseClingo.theoryAtomsTermToString(theoryAtoms, 0));
		long[] termIds = BaseClingo.theoryAtomsElementTuple(theoryAtoms, 0);
		long[] condIds = BaseClingo.theoryAtomsElementCondition(theoryAtoms, 0);
		int condId = BaseClingo.theoryAtomsElementConditionId(theoryAtoms, 0);
		String taeString = BaseClingo.theoryAtomsElementToString(theoryAtoms, 0);
//		BaseClingo.theoryAtomsAtomTerm(theoryAtoms, atom);
	}

}
