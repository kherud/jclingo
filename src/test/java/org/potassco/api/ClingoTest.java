package org.potassco.api;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.dto.Solution;
import org.potassco.enums.SolveMode;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;

public class ClingoTest {

	@Test
	public void testTravellingSalesperson() {
		String name = "base";
		Clingo clingo = new Clingo(); 
		Control control = clingo.control(null);
		control.add(name,
				null,
				"node(1..6). "
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
				+ "#minimize { C,X,Y : cycle(X,Y), cost(X,Y,C) }. "
				+ "");
		control.ground();
		Solution solution = new Solution();
		control.solve(SolveMode.YIELD, null, new SolveEventCallback() {
			@Override
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				// TODO Auto-generated method stub
				return true;
			}
		}, null);
//		assertEquals(52, solution.getSize());
	}

}
