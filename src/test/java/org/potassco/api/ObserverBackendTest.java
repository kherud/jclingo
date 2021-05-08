package org.potassco.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class ObserverBackendTest {

	@Test
	public void testBackend() {
		Clingo clingo = new Clingo(); 
		Control control = clingo.control(null);
		Backend backend = control.backend();
		backend.addAtom(0);
		
	}

}
