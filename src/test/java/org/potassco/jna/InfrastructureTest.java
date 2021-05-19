package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.SymbolType;

import com.sun.jna.Pointer;

public class InfrastructureTest {

	@Test
	public void testSignature() {
		String name = "test";
		int arity = 2;
		boolean positive = true;
		long signature = BaseClingo.signatureCreate(name, arity, positive);
		assertEquals(name, BaseClingo.signatureName(signature));
		assertEquals(arity, BaseClingo.signatureArity(signature));
		assertEquals(positive, BaseClingo.signatureIsPositive(signature));
		assertEquals(!positive, BaseClingo.signatureIsNegative(signature));
		assertTrue(BaseClingo.signatureIsEqualTo(signature, BaseClingo.signatureCreate("test", 2, true)));
		assertTrue(BaseClingo.signatureIsLessThan(signature, BaseClingo.signatureCreate("test", 3, true)));
		SizeT hash = BaseClingo.signatureHash(signature);
		assertEquals(hash , BaseClingo.signatureHash(signature)); // returns the same hash
	}

	@Test
	public void testSymbolHandling() {
		int number = 42;
		long num = BaseClingo.symbolCreateNumber(number);
		assertEquals(number, BaseClingo.symbolNumber(num));
		assertEquals(false, BaseClingo.symbolIsPositive(num));
		assertEquals(false, BaseClingo.symbolIsNegative(num));
		
		String c = "clingo";
		assertEquals(c, BaseClingo.symbolString(BaseClingo.symbolCreateString(c)));
		long sup = BaseClingo.symbolCreateSupremum();
		assertEquals(sup, BaseClingo.symbolCreateSupremum());
		long inf = BaseClingo.symbolCreateInfimum();
		assertEquals(inf, BaseClingo.symbolCreateInfimum());
		
		String p = "potassco";
		long ps = BaseClingo.symbolCreateId(p, true);
		assertEquals(null, BaseClingo.symbolString(ps));
		assertEquals(p, BaseClingo.symbolName(ps));
		assertEquals(true, BaseClingo.symbolIsPositive(ps));
		assertEquals(false, BaseClingo.symbolIsNegative(ps));
	}

	@Test
	public void testSymbolCreateFunction() {
		int number = 42;
		long num = BaseClingo.symbolCreateNumber(number);
		String c = "clingo";
		long sc = BaseClingo.symbolCreateString(c);
		String p = "potassco";
		long[] args = new long[2];
		args[0] = num;
		args[1] = sc;
		long f = BaseClingo.symbolCreateFunction(p, args, true);
		assertTrue("potassco(42,\"clingo\")".equals(BaseClingo.symbolToString(f)));
		@SuppressWarnings("unused")
		String s5 = BaseClingo.symbolString(f);
		assertEquals(p, BaseClingo.symbolName(f));
		assertEquals(true, BaseClingo.symbolIsPositive(f));
		long[] found = BaseClingo.symbolArguments(f);

		assertNull(BaseClingo.symbolName(found[0]));
		assertEquals(SymbolType.NUMBER, BaseClingo.symbolType(found[0]));
		assertEquals("42", BaseClingo.symbolToString(found[0]));

		assertNull(BaseClingo.symbolName(found[1]));
		assertEquals(SymbolType.STRING, BaseClingo.symbolType(found[1]));
		assertEquals("\"" + c + "\"", BaseClingo.symbolToString(found[1]));

		assertEquals(SymbolType.FUNCTION, BaseClingo.symbolType(f));
//	TODO:	assertEquals(p, BaseClingo.symbolToString(f, new Size(2)));
		assertFalse(BaseClingo.symbolIsEqualTo(sc, f));
		assertTrue(BaseClingo.symbolIsEqualTo(num, BaseClingo.symbolCreateNumber(number)));
		assertTrue(BaseClingo.symbolIsLessThan(sc, f));
		SizeT hash = BaseClingo.symbolHash(f);
		assertEquals(hash, BaseClingo.symbolHash(f));
		long[] res = BaseClingo.symbolArguments(f);
		for (int i = 0; i < res.length; i++) {
			assertTrue(BaseClingo.symbolIsEqualTo(args[i], res[i]));
		}
	}

	@Test
	public void testSymbolType() {
		String c = "clingo";
		assertEquals(c, BaseClingo.addString(c));
		String t = "f(a,42)";
		long symbol = BaseClingo.parseTerm(t);
		assertEquals(SymbolType.FUNCTION, BaseClingo.symbolType(symbol));
	}

	@Test
	public void testConfiguration1() {
		String name = "base";
		String program = "a. b.";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, program);
//		BaseClingo.controlGround(control, name); - not used here!
		Pointer conf = BaseClingo.controlConfiguration(control);
		int root = BaseClingo.configurationRoot(conf);
		assertEquals(root, BaseClingo.configurationRoot(conf));
		assertEquals(4, BaseClingo.configurationType(conf, root));
		assertEquals("Options", BaseClingo.configurationDescription(conf, root));
	}

}
