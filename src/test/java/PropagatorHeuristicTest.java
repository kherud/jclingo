import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.propagator.Assignment;
import org.potassco.clingo.propagator.PropagateInit;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PropagatorHeuristicTest implements Propagator {

    private int litA;
    private int litB;

    @Test
    public void testPropagatorControl() {
        SolvingTest.TestCallback mcb = new SolvingTest.TestCallback();
        Control control = new Control("1");
        control.add("{a;b}.");
        control.ground();
        control.registerPropagator(this, false);
        control.solve(mcb).getSolveResult();

        Assert.assertEquals(1, mcb.models.size());
        Assert.assertEquals("a", Arrays.stream(mcb.models.get(0).symbols).map(Symbol::toString).collect(Collectors.joining(" ")));
    }

    public void init(PropagateInit init) {
        SymbolicAtoms symbolicAtoms = init.getSymbolicAtoms();
        SymbolicAtom a = symbolicAtoms.get(new Function("a"));
        SymbolicAtom b = symbolicAtoms.get(new Function("b"));
        litA = init.solverLiteral(a.getLiteral());
        litB = init.solverLiteral(b.getLiteral());
    }

    public int decide(int threadId, Assignment assignment, int fallback) {
        Assert.assertEquals(0, threadId);
        if (assignment.isFree(litA)) {
            return litA;
        } else if (assignment.isFree(litB)) {
            return -litB;
        }
        return fallback;
    }
}
