package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sun.jna.Pointer;

public class BaseTest {

	@Test
	public void testCleanupSetting() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. b.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, 1L, null, null);
		assertTrue(BaseClingo.controlGetEnableCleanup(control));
		BaseClingo.controlSetEnableCleanup(control, false);
		assertFalse(BaseClingo.controlGetEnableCleanup(control));
	}

	@Test
	public void testEnumerationAssumptionSetting() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. b.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, 1L, null, null);
		assertTrue(BaseClingo.controlGetEnableEnumerationAssumption(control));
		BaseClingo.controlSetEnableEnumerationAssumption(control, false);
		assertFalse(BaseClingo.controlGetEnableEnumerationAssumption(control));
	}

	@Test
	public void testIsConflicting() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "a. not a.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, 1L, null, null);
		assertTrue(BaseClingo.controlIsConflicting(control));
	}

	/**
	 * TODO {@link BaseClingo#controlAssignExternal(Pointer, int, org.potassco.base.enums.TruthValue)} 
	 * TODO {@link BaseClingo#controlReleaseExternal(Pointer, int)} 
	 */
	@Test
	public void testExternalAtoms() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name,
				null,
				"p(1). p(2). p(3). "
				+ "#external q(X) : p(X). "
				+ "q(1). "
				+ "r(X) :- q(X).");
//		BaseClingo.ground(name);
	}

}
