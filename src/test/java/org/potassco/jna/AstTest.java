package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.jna.ClingoLibrary.AstCallback;
import org.potassco.ast.enums.Type;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class AstTest {

	private static final int VALUE = 42;

	@Test
	public void test() {
		AstCallback callback = new AstCallback() {
			@Override
			public byte callback(Pointer ast, Pointer data) {
				int in = data.getInt(0);
				assertEquals(VALUE, in);
				String s = BaseClingo.astToString(ast);
				assertEquals("a", s);
				int t = BaseClingo.astGetType(ast);
				Type type = Type.fromOrdinal(t);
				assertEquals(Type.PROGRAM, type);
				return 1;
			}
		};
		IntByReference data = new IntByReference(VALUE);
		BaseClingo.astParseString("a.", callback, data.getPointer(), null, null, 0);
	}

}
