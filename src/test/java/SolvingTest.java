import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.potassco.clingo.configuration.Configuration;
import org.potassco.clingo.configuration.args.EnumMode;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.ShowType;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.solving.ConsequenceType;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.ModelType;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.solving.SolveResult;
import org.potassco.clingo.solving.TruthValue;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.Symbol;

public class SolvingTest {

	private Control control;
	private TestCallback mcb;
	private TestCallback mit;

	@Before
	public void setup() {
		this.control = new Control("0");
		this.mcb = new TestCallback();
		this.mit = new TestCallback();
	}

	@After
	public void tearDown() {
		this.control.close();
	}

	@Test
	public void testSolveResult() {
		Assert.assertTrue(control.solve().getSolveResult().satisfiable());
	}

	@Test
	public void testModelString() {
		control.add("a.");
		control.ground();
		try (SolveHandle handle = control.solve()) {
			while (handle.hasNext()) {
				Model model = handle.next();
				Assert.assertEquals("a", model.toString());
			}
		}
	}

	@Test
	public void testSolveCallback() {
		control.add("1 {a; b} 1. c.");
		control.ground();
		ModelType lastType;
		Symbol[] lastSymbols;
		try (SolveHandle handle = control.solve(mcb, SolveMode.NONE)) {
			testSatisfiable(handle.getSolveResult());
			Model last = handle.getLast();
			lastType = last.getType();
			lastSymbols = last.getSymbols(ShowType.shown());
		}
		testSatisfiable(mcb.solveResult);
		Assert.assertEquals(2, mcb.models.size());
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c") }, mcb.models.get(0).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("b"), new Function("c") }, mcb.models.get(1).symbols);
		Assert.assertEquals(ModelType.STABLE_MODEL, lastType);
		Assert.assertArrayEquals(mcb.models.get(1).symbols, lastSymbols);
	}

	@Test
	public void testSolveAsync() {
		control.add("1 {a; b} 1. c.");
		control.ground();
		control.solve(mcb).getSolveResult();
		testSatisfiable(mcb.solveResult);
		Assert.assertEquals(2, mcb.models.size());
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c") }, mcb.models.get(0).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("b"), new Function("c") }, mcb.models.get(1).symbols);
	}

	@Test
	public void testSolveYield() {
		control.add("1 {a; b} 1. c.");
		control.ground();
		try (SolveHandle handle = control.solve(Collections.emptyList(), mcb, SolveMode.YIELD)) {
			while (handle.hasNext()) {
				Model model = handle.next();
				mit.onModel(model);
			}
			mit.onResult(handle.getSolveResult());
		}
		testSatisfiable(mcb.solveResult);
		testSatisfiable(mit.solveResult);
		Assert.assertEquals(2, mcb.models.size());
		Assert.assertEquals(2, mit.models.size());
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c") }, mcb.models.get(0).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("b"), new Function("c") }, mcb.models.get(1).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c") }, mit.models.get(0).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("b"), new Function("c") }, mit.models.get(1).symbols);
	}

	@Test
	public void testSolveAsyncYield() {
		control.add("1 {a; b} 1. c.");
		control.ground();
		try (SolveHandle handle = control.solve(Collections.emptyList(), mcb, SolveMode.ASYNC_YIELD)) {
			while (true) {
				handle.resume();
				handle.getSolveResult();
				Model model = handle.getModel();
				if (model != null) {
					mit.onModel(model);
				}
				else {
					break;
				}
			}
			mit.onResult(handle.getSolveResult());
		}

		testSatisfiable(mcb.solveResult);
		testSatisfiable(mit.solveResult);
		Assert.assertEquals(2, mcb.models.size());
		Assert.assertEquals(2, mit.models.size());
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c") }, mcb.models.get(0).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("b"), new Function("c") }, mcb.models.get(1).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c") }, mit.models.get(0).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("b"), new Function("c") }, mit.models.get(1).symbols);
	}

	@Test
	public void testSolveInterrupt() {
		control.add("1 { p(P,H): H=1..99 } 1 :- P=1..100. 1 { p(P,H): P=1..100 } 1 :- H=1..99.");
		control.ground();
		try (SolveHandle handle = control.solve(Collections.emptyList(), null, SolveMode.ASYNC)) {
			handle.resume();
			handle.cancel();
			SolveResult result = handle.getSolveResult();
			Assert.assertTrue(result.interrupted());
		}

		try (SolveHandle handle = control.solve(Collections.emptyList(), null, SolveMode.ASYNC)) {
			handle.resume();
			control.interrupt();
			SolveResult result = handle.getSolveResult();
			Assert.assertTrue(result.interrupted());
		}
	}

	@Test
	public void testSolveCore() {
		Control control = new Control("0");
		control.add("3 { p(1..10) } 3.");
		control.ground();
		List<Integer> assumptions = new ArrayList<>();
		Iterator<SymbolicAtom> atoms = control.getSymbolicAtoms().iterator(new Signature("p", 1));
		atoms.forEachRemaining(atom -> assumptions.add(-atom.getLiteral()));
		int[] assumptionLiterals = new int[assumptions.size()];
		for (int i = 0; i < assumptions.size(); i++) {
			assumptionLiterals[i] = assumptions.get(i);
		}

		// this unit tests differs a little from the python API due to its opaque handling of SolveMode.NONE
		SolveHandle handle = control.solve(assumptionLiterals, null, SolveMode.NONE);
		SolveResult result = handle.getSolveResult();
		int[] core = handle.getCore();

		Assert.assertTrue(result.unsatisfiable());
		Assert.assertTrue(core.length > 7);

		control.close();
	}

	@Test
	public void testSolveEnumCautious() {
		Control control = new Control("0", "-e", "cautious");
		control.add("1 {a; b} 1. c.");
		control.ground();
		control.solve(mcb).getSolveResult();
		TestCallback.ModelTuple model = mcb.models.get(mcb.models.size() - 1);
		Assert.assertEquals(ModelType.CAUTIOUS_CONSEQUENCES, model.type);
		Assert.assertEquals("c", Arrays.stream(model.symbols).map(Symbol::toString).collect(Collectors.joining(" ")));

		control.close();
	}

	@Test
	public void testSolveEnumBrave() {
		Control control = new Control("0", "-e", "brave");
		control.add("1 {a; b} 1. c.");
		control.ground();
		control.solve(mcb).getSolveResult();
		TestCallback.ModelTuple model = mcb.models.get(mcb.models.size() - 1);
		Assert.assertEquals(ModelType.BRAVE_CONSEQUENCES, model.type);
		Assert.assertEquals("a b c", Arrays.stream(model.symbols)
				.map(Symbol::toString)
				.collect(Collectors.joining(" ")));

		control.close();
	}

	@Test
	public void testModel() {
		SolveEventCallback solveEventCallback = new SolveEventCallback() {
			@Override
			public void onModel(Model model) {
				Assert.assertTrue(model.contains(new Function("a")));
				int literal = model.getContext().getSymbolicAtoms().get(new Function("a")).getLiteral();
				Assert.assertTrue(model.isTrue(literal));
				Assert.assertFalse(model.isTrue(1000));
				Assert.assertEquals(0, model.getThreadId());
				Assert.assertEquals(1, model.getNumber());
				Assert.assertFalse(model.getOptimalityProven());
				Assert.assertArrayEquals(new long[] { 3 }, model.getCost());
				Assert.assertArrayEquals(new int[] { 5 }, model.getPriorities());
				model.extend(new Function("e"));
				Assert.assertArrayEquals(new Symbol[] { new Function("e") }, model.getSymbols(ShowType.Type.THEORY));
			}
		};
		control.add("a. b. c. #minimize { 1@5,a:a; 1@5,b:b; 1@5,c:c }.");
		control.ground();
		control.solve(solveEventCallback, SolveMode.NONE).getSolveResult();
	}

	@Test
	public void testConsequences() {
		Control control = new Control("0", "-e", "cautious");
		control.add("a. b | c.");
		control.ground();
		control.solve(new SolveEventCallback() {
			@Override
			public void onModel(Model model) {
				SymbolicAtoms atoms = model.getContext().getSymbolicAtoms();
				int a = atoms.get(new Function("a")).getLiteral();
				int b = atoms.get(new Function("b")).getLiteral();
				int c = atoms.get(new Function("c")).getLiteral();
				ConsequenceType ca = model.getConsequenceType(a);
				ConsequenceType cb = model.getConsequenceType(b);
				ConsequenceType cc = model.getConsequenceType(c);
				Assert.assertEquals(ConsequenceType.TRUE, ca);
				if (model.getNumber() == 1) {
					Assert.assertTrue(cb == ConsequenceType.UNKNOWN || cb == ConsequenceType.FALSE);
					Assert.assertTrue(cc == ConsequenceType.UNKNOWN || cc == ConsequenceType.FALSE);
					Assert.assertNotSame(cb, cc);
				}
				else if (model.getNumber() == 2) {
					Assert.assertEquals(ConsequenceType.FALSE, cb);
					Assert.assertEquals(ConsequenceType.FALSE, cc);
				}
			}
		}).getSolveResult();

		control.close();
	}

	@Test
	public void testControlClause() {
		control.add("1 {a; b; c} 1.");
		control.ground();
		try (SolveHandle handle = control.solve(mcb, SolveMode.YIELD)) {
			while (handle.hasNext()) {
				Model model = handle.next();
				Symbol clause = model.contains(new Function("a")) ? new Function("b") : new Function("a");
				model.getContext().addClause(new Symbol[] { clause }, TruthValue.FALSE);
			}
			testSatisfiable(handle.getSolveResult());
			Assert.assertEquals(2, mcb.models.size());
		}
	}

	@Test
	public void testControlNoGood() {
		control.add("1 {a; b; c} 1.");
		control.ground();
		try (SolveHandle handle = control.solve(mcb, SolveMode.YIELD)) {
			while (handle.hasNext()) {
				Model model = handle.next();
				Symbol clause = model.contains(new Function("a")) ? new Function("b") : new Function("a");
				model.getContext().addNogood(new Symbol[] { clause }, TruthValue.TRUE);
			}
			testSatisfiable(handle.getSolveResult());
			Assert.assertEquals(2, mcb.models.size());
		}
	}

	@Test
	public void testRemoveMinimize() {
		control.add("a. #minimize { 1,t : a }.");
		control.ground();
		control.solve(new SolveEventCallback() {
			@Override
			public void onModel(Model model) {
				Assert.assertArrayEquals(new long[] { 1 }, model.getCost());
			}
		}).getSolveResult();

		control.add("s1", "b. #minimize { 1,t : b }.");
		control.ground("s1");
		control.solve(new SolveEventCallback() {
			@Override
			public void onModel(Model model) {
				Assert.assertArrayEquals(new long[] { 1 }, model.getCost());
			}
		}).getSolveResult();

		control.removeMinimize();
		control.add("s2", "c. #minimize { 1,x : c ; 1,t : c}.");
		control.ground("s2");
		control.solve(new SolveEventCallback() {
			@Override
			public void onModel(Model model) {
				Assert.assertArrayEquals(new long[] { 2 }, model.getCost());
			}
		}).getSolveResult();

		control.removeMinimize();
		control.solve(new SolveEventCallback() {
			@Override
			public void onModel(Model model) {
				Assert.assertArrayEquals(new long[] {}, model.getCost());
			}
		}).getSolveResult();
	}

	@Test
	public void testCautiousConsequences() {
		Configuration configuration = control.getConfiguration();
		configuration.set(EnumMode.Cautious);
		control.add("a. b | c.");
		control.ground();
		control.solve(new SolveEventCallback() {
			@Override
			public void onModel(Model model) {
				int a = lookupLiteral(model, "a");
				int b = lookupLiteral(model, "b");
				int c = lookupLiteral(model, "c");
				ConsequenceType ca = model.getConsequenceType(a);
				ConsequenceType cb = model.getConsequenceType(b);
				ConsequenceType cc = model.getConsequenceType(c);
				ConsequenceType nca = model.getConsequenceType(-a);
				ConsequenceType ncb = model.getConsequenceType(-b);
				ConsequenceType ncc = model.getConsequenceType(-c);

				Assert.assertEquals(ConsequenceType.TRUE, ca);
				Assert.assertEquals(ConsequenceType.FALSE, nca);
				Assert.assertEquals(ConsequenceType.FALSE, ncb);
				Assert.assertEquals(ConsequenceType.FALSE, ncc);

				if (model.getNumber() == 1) {
					Assert.assertTrue(ncb == ConsequenceType.UNKNOWN || ncb == ConsequenceType.FALSE);
					Assert.assertTrue(ncc == ConsequenceType.UNKNOWN || ncc == ConsequenceType.FALSE);
					Assert.assertTrue(cb == ConsequenceType.UNKNOWN || cb == ConsequenceType.FALSE);
					Assert.assertTrue(cc == ConsequenceType.UNKNOWN || cc == ConsequenceType.FALSE);
					Assert.assertNotSame(cb, cc);
				}
				else if (model.getNumber() == 2) {
					Assert.assertEquals(ConsequenceType.FALSE, cb);
					Assert.assertEquals(ConsequenceType.FALSE, cc);
				}
			}
		}).getSolveResult();
	}

	@Test
	public void testUpdateProjection() {
		Configuration configuration = control.getConfiguration().get("solve");
		configuration.set("project", "auto");
		configuration.set("models", "0");
		control.add("{a;b;c;d}. #project a/0. #project b/0.");
		control.ground();
		control.solve(mcb).getSolveResult();
		Assert.assertEquals(4, mcb.models.size());
		Assert.assertEquals(0, mcb.models.get(0).symbols.length);
		Assert.assertArrayEquals(new Symbol[] { new Function("b") }, mcb.models.get(1).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a") }, mcb.models.get(2).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("b") }, mcb.models.get(3).symbols);

		List<SymbolicAtom> atoms = new ArrayList<>();
		SymbolicAtoms symbolicAtoms = control.getSymbolicAtoms();
		symbolicAtoms.iterator(new Signature("c", 0)).forEachRemaining(atoms::add);
		atoms.add(symbolicAtoms.get(new Function("d")));

		mcb = new TestCallback();
		control.updateProject(atoms.toArray(SymbolicAtom[]::new), false);
		control.solve(mcb).getSolveResult();
		Assert.assertEquals(4, mcb.models.size());
		Assert.assertEquals(0, mcb.models.get(0).symbols.length);
		Assert.assertArrayEquals(new Symbol[] { new Function("d") }, mcb.models.get(1).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("c") }, mcb.models.get(2).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("c"), new Function("d") }, mcb.models.get(3).symbols);

		atoms = List.of(symbolicAtoms.get(new Function("a")));

		mcb = new TestCallback();
		control.updateProject(atoms.toArray(SymbolicAtom[]::new), true);
		control.solve(mcb).getSolveResult();
		Assert.assertEquals(8, mcb.models.size());
		Assert.assertEquals(0, mcb.models.get(0).symbols.length);
		Assert.assertArrayEquals(new Symbol[] { new Function("c") }, mcb.models.get(1).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("d") }, mcb.models.get(2).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("c"), new Function("d") }, mcb.models.get(3).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a") }, mcb.models.get(4).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("d") }, mcb.models.get(5).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c") }, mcb.models.get(6).symbols);
		Assert.assertArrayEquals(new Symbol[] { new Function("a"), new Function("c"), new Function("d") }, mcb.models.get(7).symbols);
	}

	private int lookupLiteral(Model model, String name) {
		return model.getContext().getSymbolicAtoms().get(Function.fromString(name)).getLiteral();
	}

	private void testSatisfiable(SolveResult solveResult) {
		Assert.assertTrue(solveResult.satisfiable());
		Assert.assertFalse(solveResult.unsatisfiable());
		Assert.assertFalse(solveResult.interrupted());
		Assert.assertTrue(solveResult.exhausted());
	}

	static class TestCallback extends SolveEventCallback {
		final List<ModelTuple> models = new ArrayList<>();
		final List<long[]> cores = new ArrayList<>();
		SolveResult solveResult;

		@Override
		public void onModel(Model model) {
			Symbol[] symbols = model.getSymbols(ShowType.shown());
			Arrays.sort(symbols);
			models.add(new ModelTuple(model.getType(), symbols));
		}

		@Override
		public void onResult(SolveResult result) {
			solveResult = result;
		}

		@Override
		public void onUnsat(long[] literals) {
			Arrays.sort(literals);
			cores.add(literals);
		}

		static class ModelTuple {
			final ModelType type;
			final Symbol[] symbols;

			ModelTuple(ModelType type, Symbol[] symbols) {
				this.type = type;
				this.symbols = symbols;
			}
		}
	}
}
