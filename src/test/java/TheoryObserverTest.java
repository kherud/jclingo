import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.backend.ExternalType;
import org.potassco.clingo.backend.HeuristicType;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Observer;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

public class TheoryObserverTest implements Observer {

    private final Set<String> called = new HashSet<>();

    @Test
    public void testBackendObserver() throws IOException {
        String testTheory = Files.readString(
                new File(getClass().getResource("observer-theory.lp").getFile()).toPath()
        );

        Control control = new Control();
        control.registerObserver(this, false);
        control.add(testTheory);
        control.ground();

        Assert.assertTrue(called.contains("outputTerm"));
        Assert.assertTrue(called.contains("theoryTermNumber"));
        Assert.assertTrue(called.contains("theoryTermString"));
        Assert.assertTrue(called.contains("theoryTermCompound"));
        Assert.assertTrue(called.contains("theoryElement"));
        Assert.assertTrue(called.contains("theoryAtom"));

        control.close();

    }

    @Override
	public void initProgram(boolean incremental) {
        called.add("initProgram");
    }

    @Override
    public void beginStep() {
        called.add("beginStep");
    }

    @Override
    public void endStep() {
        called.add("endStep");
    }

    @Override
    public void rule(boolean choice, int[] head, int[] body) {
        called.add("rule");
    }

    @Override
    public void weightRule(boolean choice, int[] head, int lowerBound, WeightedLiteral[] body) {
        called.add("weightRule");
    }

    @Override
    public void minimize(int priority, WeightedLiteral[] literals) {
        called.add("minimize");
    }

    @Override
    public void project(int[] atoms) {
        called.add("project");
    }

    @Override
    public void outputAtom(Symbol symbol, int atom) {
        called.add("outputAtom");
    }

    @Override
    public void outputTerm(Symbol symbol, int[] condition) {
        called.add("outputTerm");
        Assert.assertEquals(new Function("t"), symbol);
        Assert.assertTrue(condition.length >= 1);
    }

    public void outputCSP(Symbol symbol, int value, int[] condition) {
        called.add("outputCSP");
    }

    @Override
    public void external(int atom, ExternalType type) {
        called.add("external");
    }

    @Override
    public void assume(int[] literals) {
        called.add("assume");
    }

    @Override
    public void heuristic(int atom, HeuristicType type, int bias, int priority, int[] condition) {
        called.add("heuristic");
    }

    @Override
    public void acycEdge(int nodeU, int nodeV, int[] condition) {
        called.add("acycEdge");
    }

    @Override
    public void theoryTermNumber(int termId, int number) {
        called.add("theoryTermNumber");
        Assert.assertEquals(1, number);
    }

    @Override
    public void theoryTermString(int termId, String name) {
        called.add("theoryTermString");
        Assert.assertEquals("a", name);
    }

    @Override
    public void theoryTermCompound(int termId, int nameIdOrType, int[] arguments) {
        called.add("theoryTermCompound");
        Assert.assertEquals(-1, nameIdOrType);
        Assert.assertTrue(arguments.length >= 2);
    }

    @Override
    public void theoryElement(int elementId, int[] terms, int[] condition) {
        called.add("theoryElement");
        Assert.assertEquals(1, terms.length);
        Assert.assertEquals(2, condition.length);
    }

    @Override
    public void theoryAtom(int atomIdOrZero, int termId, int[] elements) {
        called.add("theoryAtom");
        Assert.assertEquals(1, elements.length);
    }

    @Override
    public void theoryAtomWithGuard(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId) {
        called.add("theoryAtomWithGuard");
    }
}

