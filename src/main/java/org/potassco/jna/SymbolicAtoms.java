package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Structure;

/**
 * Object to inspect symbolic atoms in a program---the relevant Herbrand base gringo uses to instantiate programs.
 * @see clingo_control_symbolic_atoms()
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_symbolic_atoms_t}
 */
public class SymbolicAtoms extends Structure {
	public long id = 0L;

	protected List<String> getFieldOrder() {
		return Arrays.asList("id");
	}
}
