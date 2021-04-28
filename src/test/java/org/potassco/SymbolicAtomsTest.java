package org.potassco;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import com.sun.jna.Pointer;

public class SymbolicAtomsTest {

	@Test
	public void test3() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. b. c. d. e.");
		Pointer control = clingo.getControl();
		clingo.ground(name);
		Pointer atoms = clingo.controlSymbolicAtoms(control);
		assertEquals(5, clingo.symbolicAtomsSize(atoms));
//		Pointer signature = clingo.signatureCreate(c, 0, true);
		Pointer iteratorBegin = clingo.symbolicAtomsBegin(atoms, null);
		long symbol1 = clingo.symbolicAtomsSymbol(atoms, iteratorBegin);
		assertEquals("a", clingo.symbolName(symbol1));
		assertTrue(clingo.symbolicAtomsIsFact(atoms, iteratorBegin));
		Pointer iteratorNext = clingo.symbolicAtomsNext(atoms, iteratorBegin);
		Pointer iteratorNextNext = clingo.symbolicAtomsNext(atoms, iteratorNext);
		long symbol2 = clingo.symbolicAtomsSymbol(atoms, iteratorNextNext);
		assertEquals("c", clingo.symbolName(symbol2));
		Pointer iteratorEnd = clingo.symbolicAtomsEnd(atoms);
		String[] strArray = { "a", "b", "c", "d", "e" };
		int j = 0;
		for (Pointer i = clingo.symbolicAtomsBegin(atoms, null);
				!clingo.symbolicAtomsIteratorIsEqualTo(atoms, i, iteratorEnd);
				i = clingo.symbolicAtomsNext(atoms, i)) {
			long s = clingo.symbolicAtomsSymbol(atoms, i);
			assertEquals(strArray[j], clingo.symbolName(s));
			j++;
		}
//		long symbol3 = clingo.symbolicAtomsSymbol(atoms, iteratorEnd);
//		assertEquals("e", clingo.symbolName(symbol3));
		Pointer iteratorFind = clingo.symbolicAtomsFind(atoms, symbol2);
		
		
	}
// TODO
// 	public long symbolicAtomsIsExternal(Pointer atoms, Pointer iterator) {
// 	public Pointer symbolicAtomsLiteral(Pointer atoms, Pointer iterator) {
// 	public long symbolicAtomsSignaturesSize(Pointer atoms) {
// 	public Pointer symbolicAtomsSignatures(Pointer atoms, long size) {
// 	public boolean symbolicAtomsIsValid(Pointer atoms, Pointer iterator) {
}
