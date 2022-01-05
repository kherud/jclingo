import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.propagator.*;
import org.potassco.clingo.solving.SolveResult;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PropagatorInitTest implements Propagator {

    @Test
    public void testPropagatorControl() {
        SolvingTest.TestCallback mcb = new SolvingTest.TestCallback();
        Control control = new Control("0");
        control.add("{a; b; c}.");
        control.ground();
        control.registerPropagator(this, false);
        SolveResult solveResult = control.solve(mcb).getSolveResult();

        Assert.assertTrue(solveResult.satisfiable());
        Assert.assertFalse(solveResult.unsatisfiable());
        Assert.assertFalse(solveResult.interrupted());
        Assert.assertTrue(solveResult.exhausted());

        Assert.assertEquals(2, mcb.models.size());
        Assert.assertEquals("", Arrays.stream(mcb.models.get(0).symbols).map(Symbol::toString).collect(Collectors.joining(" ")));
        Assert.assertEquals("a b c", Arrays.stream(mcb.models.get(1).symbols).map(Symbol::toString).collect(Collectors.joining(" ")));

        control.close();
    }

    public void init(PropagateInit init) {
        SymbolicAtoms symbolicAtoms = init.getSymbolicAtoms();
        SymbolicAtom a = symbolicAtoms.get(new Function("a"));
        SymbolicAtom b = symbolicAtoms.get(new Function("b"));
        SymbolicAtom c = symbolicAtoms.get(new Function("c"));
        int litA = init.solverLiteral(a.getLiteral());
        int litB = init.solverLiteral(b.getLiteral());
        int litC = init.solverLiteral(c.getLiteral());
        int lit = init.addLiteral();
        init.addClause(new int[]{litA, -lit});
        init.addClause(new int[]{lit, -litA});
        init.addClause(new int[]{litB, -lit});
        init.addClause(new int[]{lit, -litB});
        WeightedLiteral[] weightedLiterals = new WeightedLiteral[2];
        weightedLiterals[0] = new WeightedLiteral(litA, 1);
        weightedLiterals[1] = new WeightedLiteral(litB, 1);
        init.addWeightConstraint(litC, weightedLiterals, 2, WeightConstraintType.EQUIVALENCE, false);
        init.addMinimize(litA, -1);

        Assert.assertTrue(init.propagate());
        Assert.assertEquals(0, init.getTheoryAtoms().size());

        Assignment assignment = init.getAssignment();
        Assert.assertTrue(assignment.isFree(litA));
        Assert.assertFalse(assignment.isTrue(litA));
        Assert.assertFalse(assignment.isFalse(litA));
        Assert.assertFalse(assignment.isFixed(litA));
        Assert.assertEquals(0, assignment.getDecisionLevel());
        Assert.assertTrue(assignment.hasLiteral(litA));
        Assert.assertFalse(assignment.isConflicting());
        Assert.assertFalse(assignment.isTotal());
        Assert.assertEquals(0, assignment.getRootLevel());
        Assert.assertTrue(assignment.size() >= 4);
        List<Integer> assignments = new ArrayList<>();
        assignment.iterator().forEachRemaining(assignments::add);
        Assert.assertTrue(assignments.size() >= 4);
    }

}
