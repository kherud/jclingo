import com.sun.jna.Pointer;
import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.solving.Observer;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.backend.ExternalType;
import org.potassco.clingo.backend.HeuristicType;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.backend.Backend;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

import java.util.HashSet;
import java.util.Set;

public class BackendObserverTest extends Observer {

    private static final Set<String> called = new HashSet<>();

    @Test
    public void testBackendObserver() {
        Control control = new Control();
        control.registerObserver(new BackendObserverTest(), false);

        control.add("{a}.");
        try (Backend backend = control.getBackend()) {
            Assert.assertTrue(called.contains("initProgram"));
            Assert.assertTrue(called.contains("beginStep"));
            backend.addAtom();
            backend.addAtom(new Function("a"));
            backend.addRule(new int[]{1}, new int[]{2, 3}, true);
            Assert.assertTrue(called.contains("rule"));
            WeightedLiteral[] weightedLiterals = new WeightedLiteral[2];
            weightedLiterals[0] = new WeightedLiteral(2, 3);
            weightedLiterals[1] = new WeightedLiteral(4, 5);
            backend.addWeightRule(new int[]{2}, 1, weightedLiterals, false);
            Assert.assertTrue(called.contains("weightRule"));
            backend.addMinimize(weightedLiterals, 0);
            Assert.assertTrue(called.contains("minimize"));
            backend.addProject(new int[]{2, 4});
            Assert.assertTrue(called.contains("project"));
            backend.addHeuristic(2, new int[]{1, 3}, HeuristicType.LEVEL, 5, 7);
            Assert.assertTrue(called.contains("heuristic"));
            backend.addAssume(new int[]{2, 3});
            Assert.assertTrue(called.contains("assume"));
            backend.addAcycEdge(1, 2, new int[]{3, 4});
            Assert.assertTrue(called.contains("acycEdge"));
            backend.addExternal(3, ExternalType.RELEASE);
            Assert.assertTrue(called.contains("external"));
        }
        Assert.assertTrue(called.contains("outputAtom"));
        control.solve();
        Assert.assertTrue(called.contains("endStep"));

        Assert.assertFalse(called.contains("outputTerm"));
        Assert.assertFalse(called.contains("outputCSP"));
        Assert.assertFalse(called.contains("theoryTermNumber"));
        Assert.assertFalse(called.contains("theoryTermString"));
        Assert.assertFalse(called.contains("theoryTermCompound"));
        Assert.assertFalse(called.contains("theoryElement"));
        Assert.assertFalse(called.contains("theoryAtom"));
        Assert.assertFalse(called.contains("theoryAtomWithGuard"));

    }

    public void initProgram(boolean incremental) {
        called.add("initProgram");
    }

    public void beginStep() {
        called.add("beginStep");
    }

    public void endStep() {
        called.add("endStep");
    }

    public void rule(boolean choice, int[] head, int[] body) {
        called.add("rule");
        Assert.assertTrue(choice);
        Assert.assertArrayEquals(new int[]{1}, head);
        Assert.assertArrayEquals(new int[]{2, 3}, body);
    }

    public void weightRule(boolean choice, int[] head, int lowerBound, WeightedLiteral[] body) {
        called.add("weightRule");
        Assert.assertFalse(choice);
        Assert.assertArrayEquals(new int[]{2}, head);
        Assert.assertEquals(1, lowerBound);
        Assert.assertEquals(2, body.length);
        Assert.assertEquals(2, body[0].literal);
        Assert.assertEquals(3, body[0].weight);
        Assert.assertEquals(4, body[1].literal);
        Assert.assertEquals(5, body[1].weight);
    }

    public void minimize(int priority, WeightedLiteral[] literals) {
        called.add("minimize");
        Assert.assertEquals(0, priority);
        Assert.assertEquals(2, literals.length);
        Assert.assertEquals(2, literals[0].literal);
        Assert.assertEquals(3, literals[0].weight);
        Assert.assertEquals(4, literals[1].literal);
        Assert.assertEquals(5, literals[1].weight);
    }

    public void project(int[] atoms) {
        called.add("project");
        Assert.assertArrayEquals(new int[]{2, 4}, atoms);
    }

    public void outputAtom(Symbol symbol, int atom) {
        called.add("outputAtom");
        Assert.assertEquals(new Function("a"), symbol);
        Assert.assertEquals(2, atom);
    }

    public void outputTerm(Symbol symbol, int[] condition) {
        called.add("outputTerm");
    }

    public void outputCSP(Symbol symbol, int value, int[] condition) {
        called.add("outputCSP");
    }

    public void external(int atom, ExternalType type) {
        called.add("external");
        Assert.assertEquals(3, atom);
        Assert.assertEquals(ExternalType.RELEASE, type);
    }

    public void assume(int[] literals) {
        called.add("assume");
        Assert.assertArrayEquals(new int[]{2, 3}, literals);
    }

    public void heuristic(int atom, HeuristicType type, int bias, int priority, int[] condition) {
        called.add("heuristic");
        Assert.assertEquals(2, atom);
        Assert.assertEquals(HeuristicType.LEVEL, type);
        Assert.assertEquals(5, bias);
        Assert.assertEquals(7, priority);
        Assert.assertArrayEquals(new int[]{1, 3}, condition);
    }

    public void acycEdge(int nodeU, int nodeV, int[] condition) {
        called.add("acycEdge");
        Assert.assertEquals(1, nodeU);
        Assert.assertEquals(2, nodeV);
        Assert.assertArrayEquals(new int[]{3, 4}, condition);
    }

    public void theoryTermNumber(int termId, int number) {
        called.add("theoryTermNumber");
    }

    public void theoryTermString(int termId, String name) {
        called.add("theoryTermString");
    }

    public void theoryTermCompound(int termId, int nameIdOrType, int[] arguments) {
        called.add("theoryTermCompound");
    }

    public void theoryElement(int elementId, int[] terms, int[] condition) {
        called.add("theoryElement");
    }

    public void theoryAtom(int atomIdOrZero, int termId, int[] elements) {
        called.add("theoryAtom");
    }

    public void theoryAtomWithGuard(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId) {
        called.add("theoryAtomWithGuard");
    }
}
