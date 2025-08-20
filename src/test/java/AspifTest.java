import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.AnswerSet;
import org.potassco.clingo.Solver;
import org.potassco.clingo.configuration.args.NumModels;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.internal.ClingoRuntimeException;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

public class AspifTest {

	@Test
	public void testPreamble() {
		try (Control control = new Control()) {
			control.add("asp 1 0 0\n0");
		}
		try (Control control = new Control()) {
			Assert.assertThrows(ClingoRuntimeException.class, () -> control.add("asp 1 0 0 unknown\n0"));
		}
		try (Control control = new Control()) {
			control.add("asp 1 0 0 incremental\n0");
			Assert.assertThrows(ClingoRuntimeException.class, () -> control.add("asp 1 0 0 incremental\n0"));
		}
		try (Control control = new Control()) {
			Assert.assertThrows(ClingoRuntimeException.class, () -> control.add("asp 1 0 0 incremental\n0\n0"));
		}
	}

	@Test
	public void testRule() {
		Solver solver = new Solver();
		List<AnswerSet> result = solver.solve("asp 1 0 0\n1 0 1 1 0 0\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(List.of(new Function("a")), result.get(0).getSymbols());

		result = solver.solve("asp 1 0 0\n1 1 1 1 0 0\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Collections.emptyList(), result.get(0).getSymbols());
		Assert.assertEquals(List.of(new Function("a")), result.get(1).getSymbols());

		result = solver.solve("asp 1 0 0\n1 1 1 1 0 0\n1 0 1 2 0 1 1\n1 0 1 3 0 1 -2\n4 1 a 1 1\n4 1 b 1 2\n4 1 c 1 3\n0", NumModels.all());
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(List.of(new Function("c")), result.get(0).getSymbols());
		Assert.assertEquals(List.of(new Function("a"), new Function("b")), result.get(1).getSymbols());

		result = solver.solve("asp 1 0 0\n1 1 2 1 2 0 0\n1 0 1 3 1 2 2 1 1 2 1\n1 0 0 0 1 -3\n4 1 a 1 1\n4 1 b 1 2\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(List.of(new Function("a"), new Function("b")), result.get(0).getSymbols());
	}

	@Test
	public void testMinimize() {
		Solver solver = new Solver();
		List<AnswerSet> result = solver.solve("asp 1 0 0\n1 1 2 1 2 0 0\n1 0 0 0 1 -2\n1 0 0 0 1 -1\n2 1 1 1 1\n2 2 1 2 2\n4 1 a 1 1\n4 1 b 1 2\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(List.of(new Function("a"), new Function("b")), result.get(0).getSymbols());
	}

	@Test
	public void testExternal() {
		Solver solver = new Solver();
		List<AnswerSet> result = solver.solve("asp 1 0 0\n5 1 0\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Collections.emptyList(), result.get(0).getSymbols());
		Assert.assertEquals(List.of(new Function("a")), result.get(1).getSymbols());

		result = solver.solve("asp 1 0 0\n5 1 1\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(List.of(new Function("a")), result.get(0).getSymbols());

		result = solver.solve("asp 1 0 0\n5 1 2\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Collections.emptyList(), result.get(0).getSymbols());

		result = solver.solve("asp 1 0 0\n5 1 3\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Collections.emptyList(), result.get(0).getSymbols());
	}

	@Test
	public void testAssume() {
		Solver solver = new Solver();
		List<AnswerSet> result = solver.solve("asp 1 0 0\n1 1 2 1 2 0 0\n6 2 1 -2\n4 1 a 1 1\n4 1 b 1 2\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(List.of(new Function("a")), result.get(0).getSymbols());
	}

	@Test
	public void testHeuristic() {
		Solver solver = new Solver();
		List<AnswerSet> result = solver.solve("asp 1 0 0\n1 1 1 1 0 0\n7 4 1 1 2 0\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(Collections.emptyList(), result.get(0).getSymbols());
		Assert.assertEquals(List.of(new Function("a")), result.get(1).getSymbols());
	}

	@Test
	public void testEdge() {
		Solver solver = new Solver();
		List<AnswerSet> result = solver.solve("asp 1 0 0\n1 1 1 1 0 0\n8 0 1 1 1\n8 1 0 0\n4 1 a 1 1\n0", NumModels.all());
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Collections.emptyList(), result.get(0).getSymbols());
	}

	@Test
	public void testTheory() {
		// todo
//		Solver solver = new Solver();
//		List<AnswerSet> result = solver.solve("asp 1 0 0\n1 1 1 1 0 0\n1 0 1 2 0 0\n9 1 0 1 b\n9 0 1 1\n9 4 0 1 1 1 1\n9 5 2 0 1 0\n4 1 x 1 1\n0", NumModels.all());
//		Assert.assertEquals(1, result.size());
//		Assert.assertEquals("&b { 1 : x }", result.get(0).toString());
	}

	@Test
	public void testComment() {
		Solver solver = new Solver();
		solver.solve("asp 1 0 0\n10 nothing!\n0", NumModels.all());
	}

	@Test
	public void testLoad() {
		Path path = new File(Objects.requireNonNull(getClass().getResource("assume.aspif")).getFile()).toPath();
		List<List<Symbol>> models = new ArrayList<>();
		try (Control control = new Control()) {
			control.loadAspif(path);
			control.ground();
			try (SolveHandle solveHandle = control.solve(new int[0], null, SolveMode.YIELD)) {
				while (solveHandle.hasNext()) {
					Model model = solveHandle.next();
					models.add(List.of(model.getSymbols()));
				}
			}
		}
		Assert.assertEquals(1, models.size());
		Assert.assertEquals(List.of(List.of(new Function("a"))), models);
	}
}
