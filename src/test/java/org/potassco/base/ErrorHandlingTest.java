package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.ErrorCode;

public class ErrorHandlingTest {

	@Test
	public void testErrorMessage() {
		BaseClingo clingo = new BaseClingo();
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
		assertEquals(0, clingo.getError());

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

}
