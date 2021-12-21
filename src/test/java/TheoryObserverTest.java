import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.backend.ExternalType;
import org.potassco.clingo.backend.HeuristicType;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Observer;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class TheoryObserverTest implements Observer {

    private static final Set<String> called = new HashSet<>();

    @Test
    public void testBackendObserver() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("observer-theory.lp");
        Path file = Paths.get(url.getPath());
        String testTheory = Files.readString(file);

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
    }

    public void weightRule(boolean choice, int[] head, int lowerBound, WeightedLiteral[] body) {
        called.add("weightRule");
    }

    public void minimize(int priority, WeightedLiteral[] literals) {
        called.add("minimize");
    }

    public void project(int[] atoms) {
        called.add("project");
    }

    public void outputAtom(Symbol symbol, int atom) {
        called.add("outputAtom");
    }

    public void outputTerm(Symbol symbol, int[] condition) {
        called.add("outputTerm");
        Assert.assertEquals(new Function("t"), symbol);
        Assert.assertTrue(condition.length >= 1);
    }

    public void outputCSP(Symbol symbol, int value, int[] condition) {
        called.add("outputCSP");
    }

    public void external(int atom, ExternalType type) {
        called.add("external");
    }

    public void assume(int[] literals) {
        called.add("assume");
    }

    public void heuristic(int atom, HeuristicType type, int bias, int priority, int[] condition) {
        called.add("heuristic");
    }

    public void acycEdge(int nodeU, int nodeV, int[] condition) {
        called.add("acycEdge");
    }

    public void theoryTermNumber(int termId, int number) {
        called.add("theoryTermNumber");
        Assert.assertEquals(1, number);
    }

    public void theoryTermString(int termId, String name) {
        called.add("theoryTermString");
        Assert.assertEquals("a", name);
    }

    public void theoryTermCompound(int termId, int nameIdOrType, int[] arguments) {
        called.add("theoryTermCompound");
        Assert.assertEquals(-1, nameIdOrType);
        Assert.assertTrue(arguments.length >= 2);
    }

    public void theoryElement(int elementId, int[] terms, int[] condition) {
        called.add("theoryElement");
        Assert.assertEquals(1, terms.length);
        Assert.assertEquals(2, condition.length);
    }

    public void theoryAtom(int atomIdOrZero, int termId, int[] elements) {
        called.add("theoryAtom");
        Assert.assertEquals(1, elements.length);
    }

    public void theoryAtomWithGuard(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId) {
        called.add("theoryAtomWithGuard");
    }
}

