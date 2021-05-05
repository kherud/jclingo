package org.potassco.base;

import org.potassco.cpp.clingo_h;
import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;

public class Clingo {

	/**
	 * Create a new control object.
	 * <p>
	 * A control object has to be freed using clingo_control_free(). TODO: This will
	 * be done in the Control class.
	 * 
	 * @param arguments array of command line arguments
	 * @return resulting control object
	 */
	public Control control(String[] arguments) {
		return new Control(arguments);
	}

	/**
	 * @param name    {@link clingo_h#clingo_control_ground}
	 * @param control
	 */
	public void ground(Pointer control, String name) {
		Part[] parts = new Part[1];
		parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
	}

}
