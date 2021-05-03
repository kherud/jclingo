package org.potassco.base;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.base.Clingo;
import org.potassco.base.ClingoException;
import org.potassco.base.Control;
import org.potassco.dto.Solution;
import org.potassco.jna.GroundCallbackT;
import org.potassco.jna.Location;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SymbolCallbackT;

import com.sun.jna.Pointer;

public class Solve2Test {

	@Test
	public void testTravellingSalesperson() {
		String name = "base";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control(name,
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
		control.ground(name);
		try {
			Solution solution = control.solve();
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

	@Test
	public void testMultiModels() {
		String name = "base";
		Clingo clingo = Clingo.getInstance();
		Control control = clingo.control(name,
				null,
				"{elected(ann; bob; carol; dan; elaine; fred)} = 3.");
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
		control.ground(parts, new Size(1), new GroundCallbackT() {
			@Override
			public boolean call(Pointer location, String name, Pointer arguments, long argumentsSize, Pointer data,
					SymbolCallbackT symbolCallback, Pointer symbolCallbackData) {
				System.out.println("GroundCallbackT");
				return true;
			}
		}, null);
		try {
			Solution solution = control.solve();
			assertEquals(3, solution.getSize());
//			clingo.solveHandleModel(null)
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	solveHandleModel
//    public ModelType modelType(Pointer model) {
//    public int modelNumber(Pointer model) {
//    public long modelSymbolsSize(Pointer model, ShowType show) {
//    public long[] modelSymbols(Pointer model, ShowType show, long size) {
//    public byte clingo_model_contains(Pointer model, long atom) {
//    public byte clingo_model_is_true(Pointer model, long literal) {
//    public long clingo_model_cost_size(Pointer model) {
//    public int clingo_model_cost(Pointer model, long size) {
//    public byte clingo_model_optimality_proven(Pointer model) {
//    public int clingo_model_thread_id(Pointer model) {
//    public void clingo_model_extend(Pointer model, long symbols, long size) {
//    public Pointer clingo_solve_control_symbolic_atoms(Pointer control) {
//    public void clingo_solve_control_add_clause(Pointer control, Pointer clause, long size) {

}
