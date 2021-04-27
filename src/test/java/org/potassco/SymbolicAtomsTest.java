package org.potassco;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sun.jna.Pointer;

public class SymbolicAtomsTest {

	@Test
	public void test3() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. b.");
		Pointer control = clingo.getControl();
		Pointer atoms = clingo.controlSymbolicAtoms(control);
		assertEquals(2, clingo.symbolicAtomsSize(atoms));
	}

}
