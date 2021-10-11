package org.potassco.api;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.jna.BaseClingo;

import com.sun.jna.Pointer;

/**
 * Tests of {@link BaseClingo} - theory atoms
 * 
 * @author Josef Schneeberger
 *
 */
public class PropagatorTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/propagator_8c-example.html
	 */
	@Test
	public void pigeon() {
		String name = "pigeon";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name,
				null,
				"1 { place(P,H) : H = 1..h } 1 :- P = 1..p.");
		// to be done
	}

}
