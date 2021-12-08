package backend;

import com.sun.jna.Pointer;
import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.grounding.Observer;
import org.potassco.clingo.dtype.NativeSize;
import org.potassco.clingo.backend.Backend;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

import java.util.HashSet;
import java.util.Set;

public class ObserverTest extends Observer {

    private Set<String> called;

    @Test
    public void testRegisterObserver() throws Exception {
        called = new HashSet<>();

        Control control = new Control("{a}.");
        control.registerObserver(new TestObserver(), false);

        try (Backend backend = control.getBackend()) {
//            Assert.assertTrue(called.contains("initProgram"));
//            Assert.assertTrue(called.contains("beginStep"));
            Symbol symbol = new Function("a");
            backend.addAtom(symbol);
        }

    }

    @Override
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
        Assert.assertEquals(headAtoms, new int[]{1});
        Assert.assertEquals(bodyAtoms, new int[]{2, 3});
        return true;
    }

    public boolean weightRule(boolean choice, Pointer head, NativeSize headSize, int lowerBound, Pointer body, NativeSize bodySize, Pointer data) {
        return true;
    }

    public boolean minimize(int priority, Pointer literals, NativeSize size, Pointer data) {
        return true;
    }

    public boolean project(Pointer atoms, NativeSize size, Pointer data) {
        return true;
    }

    public boolean outputAtom(long symbol, Pointer atom, Pointer data) {
        return true;
    }

    public boolean outputTerm(Pointer symbol, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    public boolean outputCSP(Pointer symbol, int value, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    public boolean external(Pointer atom, int type, Pointer data) {
        return true;
    }

    public boolean assume(Pointer literals, NativeSize size, Pointer data) {
        return true;
    }

    public boolean heuristic(Pointer atom, int type, int bias, int priority, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    public boolean acycEdge(int nodeU, int nodeV, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    public boolean theoryTermNumber(int termId, int number, Pointer data) {
        return true;
    }

    public boolean theoryTermString(int termId, String name, Pointer data) {
        return true;
    }

    public boolean theoryTermCompound(int termId, int nameIdOrType, Pointer arguments, NativeSize size, Pointer data) {
        return true;
    }

    public boolean theoryElement(int elementId, Pointer terms, NativeSize termsSize, Pointer condition, NativeSize conditionSize, Pointer data) {
        return true;
    }

    public boolean theoryAtom(int atomIdOrZero, int termId, Pointer elements, NativeSize size, Pointer data) {
        return true;
    }

    public boolean theoryAtomWithGuard(int atomIdOrZero, int termId, Pointer elements, NativeSize size, int operatorId, int rightHandSideId, Pointer data) {
        return true;
    }
}
