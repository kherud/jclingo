package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.Callback;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class PropagatorTest {

class PropagatorData extends Structure {
	public IntByReference pigeons;
	public long pigeonsSize;
	public State state;
	public long stateSize;

	public PropagatorData(IntByReference pigeons, long pigeonsSize, State state, long stateSize) {
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
	public long size;

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
		Pointer control = BaseClingo.control(null, null, null, 0);
		

		Propagator prop = new Propagator(null, null, null, null, null);
		PropagatorData propData = new PropagatorData(null, 0L, null, 0L);
		  
		BaseClingo.controlRegisterPropagator(control, prop, propData, false);
		
		
		
		BaseClingo.controlAdd(control, name, params , "a. b.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		BaseClingo.controlGround(control, parts, new Size(1), null, null);
		
	}

}
