package org.potassco;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.potassco.base.Clingo;
import org.potassco.base.ClingoException;
import org.potassco.base.SolveHandle;
import org.potassco.jna.ClingoLibrary;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class BasicTest {

	@Test
	public void test1() {
		IntByReference major = new IntByReference();
		IntByReference minor = new IntByReference();
		IntByReference patch = new IntByReference();
		ClingoLibrary.INSTANCE.clingo_version(major, minor, patch);
		assertEquals(5, major.getValue());
		assertEquals(5, minor.getValue());
		assertEquals(0, patch.getValue());
	}

	@Test
	public void test2() {
		Clingo clingo = new Clingo();
		assertEquals("5.5.0", clingo.version());
	}

	@Test
	public void test3() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. b.");
		clingo.ground(name);
		try {
			SolveHandle solution = clingo.solve();
			assertEquals(2, solution.getSize());
			String[] strArray = { "a", "b" };
			Set<String> expected = new HashSet<String>(Arrays.asList(strArray));
			Set<String> actual = solution.getSymbols();
			assertTrue(expected.equals(actual));
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCleanupSetting() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. b.");
		clingo.ground(name);
		Pointer control = clingo.getControl();
		assertTrue(clingo.controlGetEnableCleanup(control));
		clingo.controlSetEnableCleanup(control, false);
		assertFalse(clingo.controlGetEnableCleanup(control));
	}

	@Test
	public void testEnumerationAssumptionSetting() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. b.");
		clingo.ground(name);
		Pointer control = clingo.getControl();
		assertTrue(clingo.controlGetEnableEnumerationAssumption(control));
		clingo.controlSetEnableEnumerationAssumption(control, false);
		assertFalse(clingo.controlGetEnableEnumerationAssumption(control));
	}

	@Test
	public void testIsConflicting() {
		String name = "base";
		Clingo clingo = new Clingo(name, "a. not a.");
		clingo.ground(name);
		Pointer control = clingo.getControl();
		assertTrue(clingo.controlIsConflicting(control));
	}

	/**
	 * TODO {@link Clingo#controlAssignExternal(Pointer, int, org.potassco.enums.TruthValue)} 
	 * TODO {@link Clingo#controlReleaseExternal(Pointer, int)} 
	 */
	@Test
	public void testExternalAtoms() {
		String name = "base";
		Clingo clingo = new Clingo(name,
				"p(1). p(2). p(3). "
				+ "#external q(X) : p(X). "
				+ "q(1). "
				+ "r(X) :- q(X).");
//		clingo.ground(name);
	}

	@Test
	public void testTravellingSalesperson() {
		String name = "base";
		Clingo clingo = new Clingo(name,
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
		clingo.ground(name);
		try {
			SolveHandle solution = clingo.solve();
			assertEquals(52, solution.getSize());
//			String[] strArray = { "a", "b" };
//			Set<String> expected = new HashSet<String>(Arrays.asList(strArray));
//			Set<String> actual = solution.getSymbols();
//			assertTrue(expected.equals(actual));
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
