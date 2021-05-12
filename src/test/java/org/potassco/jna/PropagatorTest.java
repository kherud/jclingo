package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.SolveMode;
import org.potassco.jna.PropagatorSt.PropagatorInitCallback;
import org.potassco.jna.PropagatorSt.PropagatorPropagateCallback;
import org.potassco.jna.PropagatorSt.PropagatorUndoCallback;

import com.sun.jna.Pointer;
import com.sun.jna.Callback;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class PropagatorTest {

class PropagatorData extends Structure {
	public IntByReference pigeons;
	public SizeT pigeonsSize;
	public State state;
	public long stateSize;

	public PropagatorData(IntByReference pigeons, SizeT pigeonsSize, State state, long stateSize) {
		super();
		this.pigeons = pigeons;
		this.pigeonsSize = pigeonsSize;
		this.state = state;
		this.stateSize = stateSize;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("pigeons", "pigeonsSize", "state", "stateSize");
	}

}

class State extends Structure {
	public int holes;
	public SizeT size;

	protected List<String> getFieldOrder() {
		return Arrays.asList("holes", "size");
	}

}

	/**
	 * @see <a href="https://potassco.org/clingo/c-api/5.5/propagator_8c-example.html">propagator.c</a>
	 */
	@Test
	public void test() {
		String name = "pigeon";
		String[] params = {"h", "p"};
		String program = "1 { place(P,H) : H = 1..h } 1 :- P = 1..p.";
		long holes = BaseClingo.symbolCreateNumber(8);
		long pigeons = BaseClingo.symbolCreateNumber(9);
		// Part with arguments
		PartSt[] parts = new PartSt[1];
		long[] args = new long[2];
		args[0] = holes;
		args[1] = pigeons;
		parts[0] = new PartSt(name, args, 2L);
		
		// create a propagator 
		// using the default implementation for the model check
		PropagatorInitCallback init = new PropagatorInitCallback() {	
			@Override
			public byte callback(Pointer init, Pointer data) {
				// TODO Auto-generated method stub
				return 1;
			}
		};
		PropagatorPropagateCallback propagate = new PropagatorPropagateCallback() {
			@Override
			public byte callback(Pointer control, Pointer changes, SizeT size, Pointer data) {
				// TODO Auto-generated method stub
				return 1;
			}
		};
		PropagatorUndoCallback undo = new PropagatorUndoCallback() {
			@Override
			public void callback(Pointer control, Pointer changes, SizeT size, Pointer data) {
				// TODO Auto-generated method stub
				int i = 0;
			}
		};
		PropagatorSt prop = new PropagatorSt(init, propagate , undo , null, null);
//		PropagatorData propData = new PropagatorData(null, new SizeT(), null, 0L);

		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlRegisterPropagator(control, prop, /* propData */ null, false);
		BaseClingo.controlAdd(control, name, params, program);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), null, null);
		boolean modelExists = true;
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
		}
	}

}
