package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.TruthValue;

import com.sun.jna.Pointer;

public class AtomsTest {

	@Test
	public void testSymbolicAtoms() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. b. c. d. e.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer atoms = BaseClingo.controlSymbolicAtoms(control);
		assertEquals(5, BaseClingo.symbolicAtomsSize(atoms).intValue());
//		Pointer signature = BaseClingo.signatureCreate(c, 0, true);
		long iteratorBegin = BaseClingo.symbolicAtomsBegin(atoms, 0L);
		long symbol1 = BaseClingo.symbolicAtomsSymbol(atoms, iteratorBegin);
		assertEquals("a", BaseClingo.symbolName(symbol1));
		assertTrue(BaseClingo.symbolicAtomsIsFact(atoms, iteratorBegin));
		long iteratorNext = BaseClingo.symbolicAtomsNext(atoms, iteratorBegin);
		long iteratorNextNext = BaseClingo.symbolicAtomsNext(atoms, iteratorNext);
		long symbol2 = BaseClingo.symbolicAtomsSymbol(atoms, iteratorNextNext);
		assertEquals("c", BaseClingo.symbolName(symbol2));
		long iteratorEnd = BaseClingo.symbolicAtomsEnd(atoms);
		String[] strArray = { "a", "b", "c", "d", "e" };
		int j = 0;
		for (long i = BaseClingo.symbolicAtomsBegin(atoms, 0L);
				!BaseClingo.symbolicAtomsIteratorIsEqualTo(atoms, i, iteratorEnd);
				i = BaseClingo.symbolicAtomsNext(atoms, i)) {
			long s = BaseClingo.symbolicAtomsSymbol(atoms, i);
			assertEquals(strArray[j], BaseClingo.symbolName(s));
			j++;
		}
// iteratorEnd points to the last+1 atom
//		long symbol3 = BaseClingo.symbolicAtomsSymbol(atoms, iteratorEnd);
//		assertEquals("e", BaseClingo.symbolName(symbol3));
		long iteratorFind = BaseClingo.symbolicAtomsFind(atoms, symbol2);
		long c = BaseClingo.symbolicAtomsSymbol(atoms, iteratorFind);
		assertEquals("c", BaseClingo.symbolName(c));
		assertFalse(BaseClingo.symbolicAtomsIsExternal(atoms, iteratorFind));
	}

	@Test
	public void testAtomsIterator() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. b. c. d. e.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer atoms = BaseClingo.controlSymbolicAtoms(control);
		long sig = BaseClingo.signatureCreate("c", 0, true);
//		long atomsIterator = BaseClingo.symbolicAtomsBegin(atoms, sig);
		long atomsIterator = BaseClingo.symbolicAtomsBegin(atoms, 0L);
		long iteratorNext = BaseClingo.symbolicAtomsNext(atoms, atomsIterator);
		long s1 = BaseClingo.symbolicAtomsSymbol(atoms, iteratorNext);
		assertEquals("b", BaseClingo.symbolName(s1));
		long[] sigs = BaseClingo.symbolicAtomsSignatures(atoms);
		long i = BaseClingo.symbolicAtomsBegin(atoms, sigs[3]);
		long s2 = BaseClingo.symbolicAtomsSymbol(atoms, i);
		assertEquals("d", BaseClingo.symbolName(s2));
		boolean isEqual = BaseClingo.signatureIsEqualTo(sigs[0], sig);
	}
	
	/**
	 * See guide p. 45
	 */
	@Test
	public void testExternalSymbolicAtoms() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "p(1). p(2). p(3). #external q(X) : p(X). q(1). r(X) :- q(X).");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer atoms = BaseClingo.controlSymbolicAtoms(control);

		List<Long> ourAtoms = new ArrayList<>(); 
		long iteratorEnd = BaseClingo.symbolicAtomsEnd(atoms);
		int j = 1;
		for (long i = BaseClingo.symbolicAtomsBegin(atoms, 0L);
				!BaseClingo.symbolicAtomsIteratorIsEqualTo(atoms, i, iteratorEnd);
				i = BaseClingo.symbolicAtomsNext(atoms, i)) {
			ourAtoms.add(i);
			assertEquals(j++, BaseClingo.symbolicAtomsLiteral(atoms, i));
		}
		long a4 = ourAtoms.get(4);
		long c = BaseClingo.symbolicAtomsSymbol(atoms, a4);
		assertEquals("q", BaseClingo.symbolName(c));
		assertTrue(BaseClingo.symbolicAtomsIsExternal(atoms, a4));
		assertTrue(BaseClingo.symbolicAtomsIsValid(atoms, a4));
		int a4literal = BaseClingo.symbolicAtomsLiteral(atoms, a4);
		BaseClingo.controlAssignExternal(control, a4literal, TruthValue.FREE);
		// TODO: test if successful
		//  {@link BaseClingo#controlReleaseExternal(Pointer, int)} 
		long[] s = BaseClingo.symbolicAtomsSignatures(atoms);
		String[] strArray = { "p", "q", "r" };
		for (int i = 0; i < s.length; i++) {
			assertEquals(strArray[i], BaseClingo.signatureName(s[i]));
			assertEquals(1, BaseClingo.signatureArity(s[i]));
			assertEquals(true, BaseClingo.signatureIsPositive(s[i]));
		}
	}

	@Test
	public void testConstants() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name,
				null,
				"#const n=6. "
				+ "like(1,2; 3,4). "
				+ "dislike(2,3; 1,3).");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		String constName = "n";
		assertTrue(BaseClingo.controlHasConst(control, constName));
		int symbol = BaseClingo.controlGetConst(control, constName);
		assertEquals(symbol, BaseClingo.controlGetConst(control, constName));
	}
}
