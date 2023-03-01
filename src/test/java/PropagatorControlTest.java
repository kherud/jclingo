import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.propagator.*;
import org.potassco.clingo.solving.SolveResult;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PropagatorControlTest implements Propagator {

    private int litA;

    @Test
    public void testPropagatorControl() {
        SolvingTest.TestCallback mcb = new SolvingTest.TestCallback();
        Control control = new Control("0");
        control.add("{a}.");
        control.ground();
        control.registerPropagator(this, false);
        SolveResult solveResult = control.solve(mcb).getSolveResult();

        Assert.assertTrue(solveResult.satisfiable());
        Assert.assertFalse(solveResult.unsatisfiable());
        Assert.assertFalse(solveResult.interrupted());
        Assert.assertTrue(solveResult.exhausted());

        Assert.assertEquals(1, mcb.models.size());
        Assert.assertEquals("a", Arrays.stream(mcb.models.get(0).symbols).map(Symbol::toString).collect(Collectors.joining(" ")));

        control.close();
    }

    @Override
    public void init(PropagateInit init) {
        init.setCheckMode(PropagatorCheckMode.NONE);
        Assert.assertEquals(PropagatorCheckMode.NONE, init.getCheckMode());
        Assert.assertEquals(1, init.getAmountThreads());
        SymbolicAtom a = init.getSymbolicAtoms().get(new Function("a"));
        Assert.assertNotNull(a);
        litA = init.solverLiteral(a.getLiteral());
        init.addWatch(-litA);
    }

    @Override
    public void propagate(PropagateControl control, int[] changes) {
        Assignment assignment = control.getAssignment();
        Trail trail = assignment.getTrail();
        int level = assignment.getDecisionLevel();
        Assert.assertTrue(Arrays.stream(changes).anyMatch(x -> x == -litA));
        Assert.assertTrue(level >= 1);
        Assert.assertTrue(trail.size() >= 1);
        List<Integer> literals = new ArrayList<>();
        trail.iterator().forEachRemaining(literals::add);
        Assert.assertTrue(literals.size() >= 1);
        Assert.assertEquals(-litA, (int) trail.iterator(level).next());
        Assert.assertEquals(-litA, assignment.getDecision(level));
        Assert.assertEquals(0, control.getThreadId());
        Assert.assertTrue(control.hasWatch(-litA));
        Assert.assertTrue(control.propagate());
        Assert.assertFalse(control.addClause(new int[]{litA}));
    }

    @Override
    public void undo(PropagateControl control, int[] changes) {
        Assert.assertEquals(0, control.getThreadId());
        Assert.assertTrue(Arrays.stream(changes).anyMatch(x -> x == -litA));
    }
}
