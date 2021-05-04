package org.potassco.base;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.potassco.dto.Solution;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.Part;
import org.potassco.jna.Size;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class VersionTest {

	@Test
	public void test1() {
		IntByReference major = new IntByReference();
		IntByReference minor = new IntByReference();
		IntByReference patch = new IntByReference();
		ClingoLibrary.INSTANCE.clingo_version(major, minor, patch);
		assertEquals(5, major.getValue());
		assertEquals(5, minor.getValue());
		assertEquals(0, patch.getValue());
	}

	@Test
	public void test2() {
		BaseClingo clingo = new BaseClingo();
		assertEquals("5.5.0", clingo.version());
	}

	@Test
	public void test3() {
		String name = "base";
		Clingo clingo = new Clingo();
		Pointer control = clingo.control(null);
		clingo.controlAdd(control, name, null, "a. b.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		clingo.controlGround(control, parts, new Size(1), null, null);
		try {
			Solution solution = clingo.solve(control);
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
