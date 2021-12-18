import com.sun.jna.Pointer;
import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Observer;
import org.potassco.clingo.internal.NativeSize;

import java.util.HashSet;
import java.util.Set;

public class TheoryTest extends Observer {

    private static final Set<String> called = new HashSet<>();

    @Test
    public void testBackendObserver() throws Exception {
        Control control = new Control();
        control.registerObserver(new TheoryTest(), false);
        control.add(
                "#theory test {\n" +
                "    t { };\n" +
                "    &a/0 : t, head\n" +
                "}.\n" +
                "{a; b}.\n" +
                "#show t : a, b.\n" +
                "&a { (1,a): a,b }.");
        control.ground();

        Assert.assertTrue(called.contains("outputTerm"));
        Assert.assertTrue(called.contains("theoryTermNumber"));
        Assert.assertTrue(called.contains("theoryTermString"));
        Assert.assertTrue(called.contains("theoryTermCompound"));
        Assert.assertTrue(called.contains("theoryElement"));
        Assert.assertTrue(called.contains("theoryAtom"));

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
        return true;
    }

    public boolean weightRule(boolean choice, Pointer head, NativeSize headSize, int lowerBound, Pointer body, NativeSize bodySize, Pointer data) {
        called.add("weightRule");
        return true;
    }

    public boolean minimize(int priority, Pointer literals, NativeSize size, Pointer data) {
        called.add("minimize");
        return true;
    }

    public boolean project(Pointer atoms, NativeSize size, Pointer data) {
        called.add("project");
        return true;
    }

    public boolean outputAtom(long symbol, Pointer atom, Pointer data) {
        called.add("outputAtom");
        return true;
    }

    public boolean outputTerm(long symbol, Pointer condition, NativeSize size, Pointer data) {
        called.add("outputTerm");
//        Assert.assertEquals(symbol, new Function("t").getLong());
        return true;
    }

    public boolean outputCSP(Pointer symbol, int value, Pointer condition, NativeSize size, Pointer data) {
        called.add("outputCSP");
        return true;
    }

    public boolean external(Pointer atom, int type, Pointer data) {
        called.add("external");
        return true;
    }

    public boolean assume(Pointer literals, NativeSize size, Pointer data) {
        called.add("assume");
        return true;
    }

    public boolean heuristic(Pointer atom, int type, int bias, int priority, Pointer condition, NativeSize size, Pointer data) {
        called.add("heuristic");
        return true;
    }

    public boolean acycEdge(int nodeU, int nodeV, Pointer condition, NativeSize size, Pointer data) {
        called.add("acycEdge");
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

