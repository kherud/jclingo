package org.potassco;

import static org.junit.Assert.*;

import org.potassco.enums.ErrorCode;

import com.sun.jna.Pointer;

public class InfrastructureTest {

	@org.junit.Test
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

	@org.junit.Test
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
		// TODO: typo in clingo api
		assertEquals("runtime errer", clingo.warningString(1));
		assertEquals("atom undefined", clingo.warningString(2));
		assertEquals("file included", clingo.warningString(3));
		assertEquals("variable unbounded", clingo.warningString(4));
		assertEquals("global variable", clingo.warningString(5));
		assertEquals("other", clingo.warningString(6));
		assertEquals("unknown message code", clingo.warningString(7));
	}

}
