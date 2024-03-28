import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.propagator.PropagateControl;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.solving.SolveResult;
import org.potassco.clingo.symbol.Symbol;

public class PropagatorCheckTest implements Propagator {

    private boolean added = false;

    @Test
    public void testPropagatorControl() {
        SolvingTest.TestCallback mcb = new SolvingTest.TestCallback();
        Control control = new Control("0");
        control.add("");
        control.ground();
        control.registerPropagator(this, false);
        SolveResult solveResult = control.solve(mcb).getSolveResult();

        Assert.assertTrue(solveResult.satisfiable());
        Assert.assertFalse(solveResult.unsatisfiable());
        Assert.assertFalse(solveResult.interrupted());
        Assert.assertTrue(solveResult.exhausted());

        Assert.assertEquals(2, mcb.models.size());
        Assert.assertEquals("", Arrays.stream(mcb.models.get(0).symbols).map(Symbol::toString).collect(Collectors.joining(" ")));
        Assert.assertEquals("", Arrays.stream(mcb.models.get(1).symbols).map(Symbol::toString).collect(Collectors.joining(" ")));

        control.close();
    }

    @Override
    public void check(PropagateControl control) {
        if (!added) {
            added = true;
            int literal = control.addLiteral();
            Assert.assertFalse(control.hasWatch(literal));
            control.addWatch(literal);
            Assert.assertTrue(control.hasWatch(literal));
            control.removeWatch(literal);
            Assert.assertFalse(control.hasWatch(literal));
        }
    }
}
