package org.potassco;

import com.sun.jna.Pointer;

/**
 * Intended to hold the control pointer and to free
 * the according memory in clingo.
 * clingo_control_new
 * clingo_control_free
 * 
 * @author Josef Schneeberger
 *
 */
public class Control implements AutoCloseable {
	private String name;
	private String logicProgram;
	private Pointer control;

	private Control() {
		super();
		this.name = "base";
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
