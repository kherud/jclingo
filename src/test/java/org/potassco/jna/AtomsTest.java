package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sun.jna.Pointer;

public class AtomsTest {

	@Test
	public void testSymbolicAtoms() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. b. c. d. e.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, 1L, null, null);
		Pointer atoms = BaseClingo.controlSymbolicAtoms(control);
		assertEquals(5, BaseClingo.symbolicAtomsSize(atoms));
//		Pointer signature = BaseClingo.signatureCreate(c, 0, true);
		Pointer iteratorBegin = BaseClingo.symbolicAtomsBegin(atoms, null);
		long symbol1 = BaseClingo.symbolicAtomsSymbol(atoms, iteratorBegin);
		assertEquals("a", BaseClingo.symbolName(symbol1));
		assertTrue(BaseClingo.symbolicAtomsIsFact(atoms, iteratorBegin));
		Pointer iteratorNext = BaseClingo.symbolicAtomsNext(atoms, iteratorBegin);
		Pointer iteratorNextNext = BaseClingo.symbolicAtomsNext(atoms, iteratorNext);
		long symbol2 = BaseClingo.symbolicAtomsSymbol(atoms, iteratorNextNext);
		assertEquals("c", BaseClingo.symbolName(symbol2));
		Pointer iteratorEnd = BaseClingo.symbolicAtomsEnd(atoms);
		String[] strArray = { "a", "b", "c", "d", "e" };
		int j = 0;
		for (Pointer i = BaseClingo.symbolicAtomsBegin(atoms, null);
				!BaseClingo.symbolicAtomsIteratorIsEqualTo(atoms, i, iteratorEnd);
				i = BaseClingo.symbolicAtomsNext(atoms, i)) {
			long s = BaseClingo.symbolicAtomsSymbol(atoms, i);
			assertEquals(strArray[j], BaseClingo.symbolName(s));
			j++;
		}
//		long symbol3 = BaseClingo.symbolicAtomsSymbol(atoms, iteratorEnd);
//		assertEquals("e", BaseClingo.symbolName(symbol3));
//		Pointer iteratorFind = BaseClingo.symbolicAtomsFind(atoms, symbol2);
		
		
	}
// TODO
// 	public long symbolicAtomsIsExternal(Pointer atoms, Pointer iterator) {
// 	public Pointer symbolicAtomsLiteral(Pointer atoms, Pointer iterator) {
// 	public long symbolicAtomsSignaturesSize(Pointer atoms) {
// 	public Pointer symbolicAtomsSignatures(Pointer atoms, long size) {
// 	public boolean symbolicAtomsIsValid(Pointer atoms, Pointer iterator) {
	
	@Test
	public void testCallback() {
		SymbolCallback sb = new SymbolCallback() {
			
			@Override
			public boolean call(Pointer symbols, Pointer symbolsSize, Pointer data) {
				// TODO Auto-generated method stub
				return false;
			}
		};
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
		BaseClingo.controlGround(control, parts, 1L, null, null);
		String constName = "n";
		assertTrue(BaseClingo.controlHasConst(control, constName));
		int symbol = BaseClingo.controlGetConst(control, constName);
		assertEquals(symbol, BaseClingo.controlGetConst(control, constName));
	}
}
