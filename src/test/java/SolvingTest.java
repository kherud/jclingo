import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.ShowType;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.solving.*;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.Symbol;

import java.util.*;

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
    public void testSolveAsync() {
        control.add("1 {a; b} 1. c.");
        control.ground();
        control.solve(mcb).wait(-1.);
        testSatisfiable(mcb.solveResult);
        Assert.assertEquals(2, mcb.models.size());
        Assert.assertArrayEquals(new Symbol[]{new Function("a"), new Function("b")}, mcb.models.get(0).symbols);
        Assert.assertArrayEquals(new Symbol[]{new Function("b"), new Function("c")}, mcb.models.get(1).symbols);
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
        Assert.assertArrayEquals(new Symbol[]{new Function("a"), new Function("b")}, mcb.models.get(0).symbols);
        Assert.assertArrayEquals(new Symbol[]{new Function("b"), new Function("c")}, mcb.models.get(1).symbols);
        Assert.assertArrayEquals(new Symbol[]{new Function("a"), new Function("b")}, mit.models.get(0).symbols);
        Assert.assertArrayEquals(new Symbol[]{new Function("b"), new Function("c")}, mit.models.get(1).symbols);
    }

    @Test
    public void testSolveAsyncYield() {
        control.add("1 {a; b} 1. c.");
        control.ground();
        try (SolveHandle handle = control.solve(Collections.emptyList(), mcb, SolveMode.ASYNC_YIELD)) {
            while (true) {
                handle.resume();
                handle.wait(-1.);
                try {
                    Model model = handle.getModel();
                    mit.onModel(model);
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            mit.onResult(handle.getSolveResult());
        }

        testSatisfiable(mcb.solveResult);
        testSatisfiable(mit.solveResult);
        Assert.assertEquals(2, mcb.models.size());
        Assert.assertEquals(2, mit.models.size());
        Assert.assertArrayEquals(new Symbol[]{new Function("a"), new Function("b")}, mcb.models.get(0).symbols);
        Assert.assertArrayEquals(new Symbol[]{new Function("b"), new Function("c")}, mcb.models.get(1).symbols);
        Assert.assertArrayEquals(new Symbol[]{new Function("a"), new Function("b")}, mit.models.get(0).symbols);
        Assert.assertArrayEquals(new Symbol[]{new Function("b"), new Function("c")}, mit.models.get(1).symbols);
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
        control.add("3 { p(1..10) } 3.");
        control.ground();
        List<Integer> assumptions = new ArrayList<>();
        Iterator<SymbolicAtom> atoms = control.getSymbolicAtoms().iterator(new Signature("p", 1, true));
        atoms.forEachRemaining(atom -> assumptions.add(-atom.getLiteral()));
        int[] assumptionLiterals = new int[assumptions.size()];
        for (int i = 0; i < assumptions.size(); i++)
            assumptionLiterals[i] = assumptions.get(i);
        System.out.println(assumptions);
        control.solve(assumptionLiterals, mcb, SolveMode.ASYNC).wait(-1.);
        System.out.println(mcb.solveResult.unsatisfiable());
    }

    private void testSatisfiable(SolveResult solveResult) {
        Assert.assertTrue(solveResult.satisfiable());
        Assert.assertFalse(solveResult.unsatisfiable());
        Assert.assertFalse(solveResult.interrupted());
        Assert.assertTrue(solveResult.exhausted());
    }


    private static class TestCallback extends SolveEventCallback {
        public final List<ModelTuple> models = new ArrayList<>();
        public final List<List<Integer>> cores = new ArrayList<>();
        public SolveResult solveResult;

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
        public void onUnsat(List<Integer> literals) {
            literals.sort(Integer::compareTo);
            cores.add(literals);
        }

        private static class ModelTuple {
            public final ModelType type;
            public final Symbol[] symbols;

            public ModelTuple(ModelType type, Symbol[] symbols) {
                this.type = type;
                this.symbols = symbols;
            }
        }
    }
}
