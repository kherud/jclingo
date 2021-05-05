package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.jna.Part;
import org.potassco.jna.Size;

import com.sun.jna.Pointer;

public class BaseTest {

	@Test
	public void testCleanupSetting() {
		String name = "base";
		BaseClingo clingo = new BaseClingo(); 
		Pointer control = clingo.control(null, null, null, 0);
		clingo.controlAdd(control, name, null, "a. b.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		clingo.controlGround(control, parts, new Size(1), null, null);
		assertTrue(clingo.controlGetEnableCleanup(control));
		clingo.controlSetEnableCleanup(control, false);
		assertFalse(clingo.controlGetEnableCleanup(control));
	}

	@Test
	public void testEnumerationAssumptionSetting() {
		String name = "base";
		BaseClingo clingo = new BaseClingo(); 
		Pointer control = clingo.control(null, null, null, 0);
		clingo.controlAdd(control, name, null, "a. b.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		clingo.controlGround(control, parts, new Size(1), null, null);
		assertTrue(clingo.controlGetEnableEnumerationAssumption(control));
		clingo.controlSetEnableEnumerationAssumption(control, false);
		assertFalse(clingo.controlGetEnableEnumerationAssumption(control));
	}

	@Test
	public void testIsConflicting() {
		String name = "base";
		BaseClingo clingo = new BaseClingo(); 
		Pointer control = clingo.control(null, null, null, 0);
		clingo.controlAdd(control, name, null, "a. not a.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		clingo.controlGround(control, parts, new Size(1), null, null);
		assertTrue(clingo.controlIsConflicting(control));
	}

	/**
	 * TODO {@link BaseClingo#controlAssignExternal(Pointer, int, org.potassco.base.enums.TruthValue)} 
	 * TODO {@link BaseClingo#controlReleaseExternal(Pointer, int)} 
	 */
	@Test
	public void testExternalAtoms() {
		String name = "base";
		BaseClingo clingo = new BaseClingo(); 
		Pointer control = clingo.control(null, null, null, 0);
		clingo.controlAdd(control, name,
				null,
				"p(1). p(2). p(3). "
				+ "#external q(X) : p(X). "
				+ "q(1). "
				+ "r(X) :- q(X).");
//		clingo.ground(name);
	}

}
