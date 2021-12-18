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
import org.potassco.clingo.symbol.Number;

import java.util.HashSet;
import java.util.Set;

public class TestObserver extends Observer {

    private static final Set<String> called = new HashSet<>();

    @Test
    public void testBackendObserver() throws Exception {
        Control control = new Control();
        control.registerObserver(new TestObserver(), false);

        control.add("{a}.");
        try (Backend backend = control.getBackend()) {
            Assert.assertTrue(called.contains("initProgram"));
            Assert.assertTrue(called.contains("beginStep"));
            backend.addAtom();
//            backend.addAtom(new Function("a"));
//            backend.addRule(new int[]{1}, new int[]{2, 3}, true);
//            Assert.assertTrue(called.contains("rule"));
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

    }

    public boolean initProgram(boolean incremental, Pointer data) {
        called.add("initProgram");
        return true;
    }

    public boolean beginStep(Pointer data) {
        called.add("beginStep");
        return true;
    }

    public boolean endStep(Pointer data) {
        called.add("endStep");
        return true;
    }

    public boolean rule(boolean choice, Pointer head, NativeSize headSize, Pointer body, NativeSize bodySize, Pointer data) {
        called.add("rule");
        Assert.assertTrue(choice);
        Assert.assertTrue(headSize.intValue() > 0);
        Assert.assertTrue(bodySize.intValue() > 0);
        int[] headAtoms = head.getIntArray(0, headSize.intValue());
        int[] bodyAtoms = body.getIntArray(0, bodySize.intValue());
        Assert.assertArrayEquals(headAtoms, new int[]{1});
        Assert.assertArrayEquals(bodyAtoms, new int[]{2, 3});
        return true;
    }

    public boolean weightRule(boolean choice, Pointer head, NativeSize headSize, int lowerBound, Pointer body, NativeSize bodySize, Pointer data) {
        Assert.assertTrue(headSize.intValue() > 0);
        Assert.assertTrue(bodySize.intValue() > 0);
        int[] headAtoms = head.getIntArray(0, headSize.intValue());
        int[] bodyAtoms = body.getIntArray(0, bodySize.intValue());
        called.add("weightRule");
        Assert.assertFalse(choice);
        Assert.assertArrayEquals(headAtoms, new int[]{2});
        // TODO: test for weighted literals
        Assert.assertArrayEquals(bodyAtoms, new int[]{2});
        Assert.assertEquals(lowerBound, 1);
        return true;
    }

    public boolean minimize(int priority, Pointer literals, NativeSize size, Pointer data) {
        called.add("minimize");
        Assert.assertEquals(priority, 0);
        // TODO: test for weighted literals
//        Assert.assertEquals(literals);
        return true;
    }

    public boolean project(Pointer atoms, NativeSize size, Pointer data) {
        called.add("project");
        Assert.assertTrue(size.intValue() > 0);
        int[] atomInts = atoms.getIntArray(0, size.intValue());
        Assert.assertEquals(atomInts, new int[]{2, 4});
        return true;
    }

    public boolean outputAtom(long symbol, Pointer atom, Pointer data) {
        called.add("outputAtom");
        Assert.assertEquals(symbol, new Function("a").getLong());
        return true;
    }

    public boolean outputTerm(Pointer symbol, Pointer condition, NativeSize size, Pointer data) {
        called.add("outputTerm");
        return true;
    }

    public boolean outputCSP(Pointer symbol, int value, Pointer condition, NativeSize size, Pointer data) {
        called.add("outputCSP");
        return true;
    }

    public boolean external(Pointer atom, int type, Pointer data) {
        called.add("external");
        Assert.assertEquals(atom.getInt(0), 3);
        Assert.assertEquals(type, ExternalType.RELEASE.getValue());
        return true;
    }

    public boolean assume(Pointer literals, NativeSize size, Pointer data) {
        called.add("assume");
        Assert.assertTrue(size.intValue() > 0);
        int[] literalInts = literals.getIntArray(0, size.intValue());
        Assert.assertEquals(literalInts, new int[]{2, 3});
        return true;
    }

    public boolean heuristic(Pointer atom, int type, int bias, int priority, Pointer condition, NativeSize size, Pointer data) {
        called.add("heuristic");
        Assert.assertTrue(size.intValue() > 0);
        int[] conditionInts = condition.getIntArray(0, size.intValue());
        Assert.assertEquals(atom.getInt(0), 2);
        Assert.assertEquals(type, HeuristicType.LEVEL.getValue());
        Assert.assertEquals(bias, 5);
        Assert.assertEquals(priority, 7);
        Assert.assertEquals(conditionInts, new int[]{1, 3});
        return true;
    }

    public boolean acycEdge(int nodeU, int nodeV, Pointer condition, NativeSize size, Pointer data) {
        called.add("acycEdge");
        Assert.assertTrue(size.intValue() > 0);
        int[] conditionInts = condition.getIntArray(0, size.intValue());
        Assert.assertEquals(nodeU, 1);
        Assert.assertEquals(nodeV, 2);
        Assert.assertEquals(conditionInts, new int[]{3, 4});
        return true;
    }

    public boolean theoryTermNumber(int termId, int number, Pointer data) {
        called.add("theoryTermNumber");
        return true;
    }

    public boolean theoryTermString(int termId, String name, Pointer data) {
        called.add("theoryTermString");
        return true;
    }

    public boolean theoryTermCompound(int termId, int nameIdOrType, Pointer arguments, NativeSize size, Pointer data) {
        called.add("theoryTermCompound");
        return true;
    }

    public boolean theoryElement(int elementId, Pointer terms, NativeSize termsSize, Pointer condition, NativeSize conditionSize, Pointer data) {
        called.add("theoryElement");
        return true;
    }

    public boolean theoryAtom(int atomIdOrZero, int termId, Pointer elements, NativeSize size, Pointer data) {
        called.add("theoryAtom");
        return true;
    }

    public boolean theoryAtomWithGuard(int atomIdOrZero, int termId, Pointer elements, NativeSize size, int operatorId, int rightHandSideId, Pointer data) {
        called.add("theoryAtomWithGuard");
        return true;
    }
}
