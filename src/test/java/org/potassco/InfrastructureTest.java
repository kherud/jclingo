package org.potassco;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.ErrorCode;
import org.potassco.enums.SymbolType;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class InfrastructureTest {

	@Test
	public void testSignature() {
		Clingo clingo = new Clingo();
		try {
			String name = "test";
			int arity = 2;
			boolean positive = true;
			Pointer signature = clingo.signatureCreate(name, arity, positive);
			assertEquals(name, clingo.signatureName(signature));
			assertEquals(arity, clingo.signatureArity(signature));
			assertEquals(positive, clingo.signatureIsPositive(signature));
			assertEquals(!positive, clingo.signatureIsNegative(signature));
			assertTrue(clingo.signatureIsEqualTo(signature, clingo.signatureCreate("test", 2, true)));
			assertTrue(clingo.signatureIsLessThan(signature, clingo.signatureCreate("test", 3, true)));
			int hash = clingo.signatureHash(signature);
			assertEquals(hash , clingo.signatureHash(signature)); // returns the same hash
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testErrorMessage() {
		Clingo clingo = new Clingo();
		assertEquals("success", clingo.errorString(0));
		assertEquals("runtime error", clingo.errorString(1));
		assertEquals("logic error", clingo.errorString(2));
		assertEquals("bad allocation", clingo.errorString(3));
		assertEquals("unknown error", clingo.errorString(4));
		
		assertEquals(null, clingo.errorString(5));
		int myCode = 42;
		String myMessage = "jclingo error";
		clingo.setError(myCode, myMessage);
		assertEquals(myCode, clingo.errorCode());
		assertEquals(myMessage, clingo.errorMessage());

		clingo.setError(0, "");
		assertEquals(ErrorCode.SUCCESS, clingo.getError());

		assertEquals("operation undefined", clingo.warningString(0));
		// TODO: typo in clingo api: errer
		assertEquals("runtime errer", clingo.warningString(1));
		assertEquals("atom undefined", clingo.warningString(2));
		assertEquals("file included", clingo.warningString(3));
		assertEquals("variable unbounded", clingo.warningString(4));
		assertEquals("global variable", clingo.warningString(5));
		assertEquals("other", clingo.warningString(6));
		assertEquals("unknown message code", clingo.warningString(7));
	}

	@Test
	public void testSymbolHandling() {
		Clingo clingo = new Clingo();
		int number = 42;
		long num = clingo.symbolCreateNumber(number);
		assertEquals(number, clingo.symbolNumber(num));
		// TODO: Is this correct?
		assertEquals(false, clingo.symbolIsPositive(num));
		// TODO: Is this correct?
		assertEquals(false, clingo.symbolIsNegative(num));
		
		String c = "clingo";
		assertEquals(c, clingo.symbolString(clingo.symbolCreateString(c)));
//		assertEquals("", clingo.symbolString(clingo.symbolCreateSupremum()));
//		assertEquals("", clingo.symbolString(clingo.symbolCreateInfimum()));
		
		String p = "potassco";
		long ps = clingo.symbolCreateId(p, true);
//		assertEquals(p, clingo.symbolString(ps));
		assertEquals(p, clingo.symbolName(ps));
		assertEquals(true, clingo.symbolIsPositive(ps));
		assertEquals(false, clingo.symbolIsNegative(ps));
		clingo.symbolArguments(ps, null, null);
	}

	@Test
	public void testSymbolCreateFunction() {
		Clingo clingo = new Clingo();
		int number = 42;
		long num = clingo.symbolCreateNumber(number);
		String c = "clingo";
		long s = clingo.symbolCreateString(c);
		String p = "potassco";
		List<Long> args = new LinkedList<Long>();
		args.add(num);
		args.add(s);
		long f = clingo.symbolCreateFunction(p, args, true);
		assertEquals(p, clingo.symbolName(f));
		assertEquals(true, clingo.symbolIsPositive(f));
//		clingo.symbolArguments(f, null, null); TODO: infuctional
		assertEquals(SymbolType.FUNCTION, clingo.symbolType(f));
		assertEquals(20, clingo.symbolToStringSize(f));
//	TODO:	assertEquals(p, clingo.symbolToString(f, new Size(2)));
		assertFalse(clingo.symbolIsEqualTo(s, f));
		assertTrue(clingo.symbolIsEqualTo(num, clingo.symbolCreateNumber(number)));
		assertTrue(clingo.symbolIsLessThan(s, f));
		int hash = clingo.symbolHash(f);
		assertEquals(hash, clingo.symbolHash(f));
	}
	
	@Test
	public void test() {
		Clingo clingo = new Clingo();
		String c = "clingo";
		assertEquals(c, clingo.addString(c));
		String t = "f(a,42)";
		long symbol = clingo.parseTerm(t);
		assertEquals(SymbolType.FUNCTION, clingo.symbolType(symbol));
	}
	
	@Test
	public void testConfiguration() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. b.");
//		clingo.ground(name);
		Pointer control = clingo.getControl();
		Pointer conf = clingo.controlConfiguration(control);
		int root = clingo.configurationRoot(conf);
		assertEquals(root, clingo.configurationRoot(conf));
		assertEquals(ConfigurationType.MAP, clingo.configurationType(conf, root));
		assertEquals("Options", clingo.configurationDescription(conf, root));
	}
}
