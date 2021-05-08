package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;
import com.sun.jna.Callback;
import org.potassco.jna.Propagator.PropagatorCheckCallback;
import org.potassco.jna.Propagator.PropagatorDecideCallback;
import org.potassco.jna.Propagator.PropagatorInitCallback;
import org.potassco.jna.Propagator.PropagatorPropagateCallback;
import org.potassco.jna.Propagator.PropagatorUndoCallback;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class Propagator extends Structure {
	interface PropagatorDecideCallback extends Callback {
		boolean callback(int init, int data);
	}

	interface PropagatorCheckCallback extends Callback {

	}

	interface PropagatorUndoCallback extends Callback {

	}

	interface PropagatorPropagateCallback extends Callback {

	}

	interface PropagatorInitCallback extends Callback {

	}

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
