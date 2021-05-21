package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.enums.SolveMode;

import com.sun.jna.Pointer;

public class GroundSolveTest extends CheckModels {

	@Test
	public void testMultipleModels() {
		String[] expectedStrings = {"p(1)", "p(2)", "p(3)"};
		String name = "base";
		String program = "1 {p(1..3)} 2.";
		String[] arguments = { "0" }; // enumerate all models
		Pointer control = BaseClingo.control(arguments, null, null, 0);
		BaseClingo.controlAdd(control, name, null, program);
		PartSt[] parts = new PartSt[1];
		parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), null, null);
		checkModels(control, handle, expectedStrings, 6);
	}

	@Test
	public void testGrounding() {
		String[] expectedStrings = {"p(1)", "p(2)", "p(3)"};
		String name = "base";
		String program = "node(1..6). "
				+ ""
				+ "edge(1,(2;3;4)). edge(2,(4;5;6)). edge(3,(1;4;5)). "
				+ "edge(4,(1;2)). edge(5,(3;4;6)). edge(6,(2;3;5)). "
				+ ""
				+ "cost(1,2,2). cost(1,3,3). cost(1,4,1). "
				+ "cost(2,4,2). cost(2,5,2). cost(2,6,4). "
				+ "cost(3,1,3). cost(3,4,2). cost(3,5,2). "
				+ "cost(4,1,1). cost(4,2,2). "
				+ "cost(5,3,2). cost(5,4,2). cost(5,6,1). "
				+ "cost(6,2,4). cost(6,3,3). cost(6,5,1). "
				+ ""
				+ "edge(X,Y) :- cost(X,Y,_). "
				+ "node(X) :- cost(X,_,_). "
				+ "node(Y) :- cost(_,Y,_). "
				+ ""
				+ "{ cycle(X,Y) : edge(X,Y) } = 1 :- node(X). "
				+ "{ cycle(X,Y) : edge(X,Y) } = 1 :- node(Y). "
				+ " "
				+ "reached(Y) :- cycle(1,Y). "
				+ "reached(Y) :- cycle(X,Y), reached(X). "
				+ " "
				+ ":- node(Y), not reached(Y). "
				+ " "
				+ "#minimize { C,X,Y : cycle(X,Y), cost(X,Y,C) }. ";
		String[] arguments = { "0" }; // enumerate all models
		Pointer control = BaseClingo.control(arguments, null, null, 0);
		BaseClingo.controlAdd(control, name, null, program);
		PartSt[] parts = new PartSt[1];
		parts[0] = new PartSt(name, null, 0L);
		GroundProgramObserverSt observer = new GroundProgramObserverSt(null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null);
		BaseClingo.controlRegisterObserver(control, observer, true, null);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), null, null);
		checkModels(control, handle, expectedStrings, 6);
	}

}
