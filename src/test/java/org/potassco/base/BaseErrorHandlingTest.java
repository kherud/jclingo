package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;

public class BaseErrorHandlingTest {

	@Test
	public void testErrorMessage() {
		assertEquals("success", BaseClingo.errorString(0));
		assertEquals("runtime error", BaseClingo.errorString(1));
		assertEquals("logic error", BaseClingo.errorString(2));
		assertEquals("bad allocation", BaseClingo.errorString(3));
		assertEquals("unknown error", BaseClingo.errorString(4));
		
		assertEquals(null, BaseClingo.errorString(5));
		int myCode = 42;
		String myMessage = "jclingo error";
		BaseClingo.setError(myCode, myMessage);
		assertEquals(myCode, BaseClingo.errorCode());
		assertEquals(myMessage, BaseClingo.errorMessage());

		BaseClingo.setError(0, "");
		assertEquals(0, BaseClingo.getError());

		assertEquals("operation undefined", BaseClingo.warningString(0));
		// TODO: typo in clingo api: errer
		assertEquals("runtime errer", BaseClingo.warningString(1));
		assertEquals("atom undefined", BaseClingo.warningString(2));
		assertEquals("file included", BaseClingo.warningString(3));
		assertEquals("variable unbounded", BaseClingo.warningString(4));
		assertEquals("global variable", BaseClingo.warningString(5));
		assertEquals("other", BaseClingo.warningString(6));
		assertEquals("unknown message code", BaseClingo.warningString(7));
	}

}
