package org.potassco;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.potassco.jna.ClingoLibrary;

import com.sun.jna.ptr.IntByReference;

public class Test {

	@org.junit.Test
	public void test1() {
		IntByReference major = new IntByReference();
		IntByReference minor = new IntByReference();
		IntByReference patch = new IntByReference();
		ClingoLibrary.INSTANCE.clingo_version(major, minor, patch);
		assertEquals(5, major.getValue());
		assertEquals(5, minor.getValue());
		assertEquals(0, patch.getValue());
	}

	@org.junit.Test
	public void test2() {
		Clingo clingo = new Clingo();
		assertEquals("5.5.0", clingo.version());
	}

	@org.junit.Test
	public void test3() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. b.");
		clingo.ground(name);
		try {
			SolveHandle solution = clingo.solve();
			assertEquals(2, solution.getSize());
			String[] strArray = { "a", "b" };
			Set<String> expected = new HashSet<String>(Arrays.asList(strArray));
			Set<String> actual = solution.getSymbols();
			assertTrue(expected.equals(actual));
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
