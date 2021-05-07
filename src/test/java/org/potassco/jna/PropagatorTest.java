package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sun.jna.Pointer;

public class PropagatorTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/propagator_8c-example.html
	 */
	@Test
	public void test() {
		String name = "pigeon";
		String[] params = {"h", "p"};
		String program = "1 { place(P,H) : H = 1..h } 1 :- P = 1..p.";
		long holes = BaseClingo.symbolCreateNumber(8);
		long pigeons = BaseClingo.symbolCreateNumber(9);
		Pointer control = BaseClingo.control(null, null, null, 0);
		
		  // using the default implementation for the model check
		  clingo_propagator_t prop = {
		    (clingo_propagator_init_callback_t)init,
		    (clingo_propagator_propagate_callback_t)propagate,
		    (clingo_propagator_undo_callback_t)undo,
		    NULL,
		    NULL,
		  };
		  propagator_t prop_data = { NULL, 0, NULL, 0 };
		  
		BaseClingo.controlRegisterPropagator(control, prop, prop_data, false);
		
		
		
		BaseClingo.controlAdd(control, name, params , "a. b.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
		
	}

}
