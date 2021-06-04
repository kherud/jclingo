package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.potassco.jna.ClingoLibrary.AstCallback;
import org.potassco.ast.enums.Type;
import org.potassco.enums.SolveMode;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class AstTest extends CheckModels {
	private static final int VALUE = 42;

	/**
	 * Test AST callback
	 */
	@Test
	public void test() {
		String[] expectedStrings = {"#program base.", "a.", "b."};
		Type[] expectedTypes = {Type.PROGRAM, Type.RULE};
		AstCallback callback = new AstCallback() {
			/**
			 * The callback is invoked for every part of the program
			 */
			@Override
			public byte callback(Pointer ast, Pointer data) {
				int in = data.getInt(0);
				assertEquals(VALUE, in); // test passing data 
				String s = BaseClingo.astToString(ast);
				assertTrue(Arrays.stream(expectedStrings).anyMatch(s::equals));
				Type t = BaseClingo.astGetType(ast);
				assertTrue(Arrays.stream(expectedTypes).anyMatch(t::equals));
				return 1;
			}
		};
		IntByReference data = new IntByReference(VALUE);
		BaseClingo.astParseString("a. b.", callback, data.getPointer(), null, null, 0);
	}

	/**
	 * construct a program by AST calls
	 */
	@Test
	public void test3() {
		String[] expectedStrings = {"a"};
		String name = "base";
		AstCallback callback = new AstCallback() {			
			@Override
			public byte callback(Pointer ast, Pointer data) {
				return 1;
			}
		};
		BaseClingo.astParseString("a.", callback, null, null, null, 0);
		Pointer control = BaseClingo.control(null, null, null, 0);
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), null, null);
		checkModels(control, handle, expectedStrings, 1);
	}

	@Test
	public void test2() {
		String name = "plain";
		Pointer control = BaseClingo.control(null, null, null, 0);
		Pointer builder = BaseClingo.controlProgramBuilder(control);
		BaseClingo.programBuilderBegin(builder);
		Pointer ast = BaseClingo.astBuild(Type.SYMBOLIC_ATOM);
		BaseClingo.programBuilderAdd(builder, ast);
		
		BaseClingo.programBuilderEnd(builder);
	}

}