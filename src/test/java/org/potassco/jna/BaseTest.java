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
		SizeT size = new SizeT(1L);
		BaseClingo.controlGround(control, parts, size, null, null);
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
		SizeT size = new SizeT(1L);
		BaseClingo.controlGround(control, parts, size, null, null);
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
		SizeT size = new SizeT(1L);
		BaseClingo.controlGround(control, parts, size, null, null);
		assertTrue(BaseClingo.controlIsConflicting(control));
	}

}
