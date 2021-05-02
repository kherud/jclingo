package org.potassco;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.potassco.base.Clingo;
import org.potassco.base.ClingoException;
import org.potassco.base.Control;
import org.potassco.dto.Solution;
import org.potassco.jna.ClingoLibrary;

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
		Clingo clingo = Clingo.getInstance();
		assertEquals("5.5.0", clingo.version());
	}

	@Test
	public void test3() {
		String name = "base";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control(name, "a. b.");
		control.ground(name);
		try {
			Solution solution = control.solve();
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
