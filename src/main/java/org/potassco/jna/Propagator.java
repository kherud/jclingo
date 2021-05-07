package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class Propagator extends Structure {
	public PropagatorInitCallback init;
	public PropagatorPropagateCallback propagate;
	public PropagatorUndoCallback undo;
	public PropagatorCheckCallback check;
	public PropagatorDecideCallback decide;
	
	public Propagator(PropagatorInitCallback init, PropagatorPropagateCallback propagate, PropagatorUndoCallback undo,
			PropagatorCheckCallback check, PropagatorDecideCallback decide) {
		super();
		this.init = init;
		this.propagate = propagate;
		this.undo = undo;
		this.check = check;
		this.decide = decide;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("init", "propagate", "undo", "check", "decide");
	}

}
