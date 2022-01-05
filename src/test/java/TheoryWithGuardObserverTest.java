import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.backend.ExternalType;
import org.potassco.clingo.backend.HeuristicType;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.symbol.Symbol;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class TheoryWithGuardObserverTest extends TheoryObserverTest {

    private final Set<String> called = new HashSet<>();

    @Test
    public void testBackendObserver() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("observer-theory-with-guard.lp");
        Path file = Paths.get(url.getPath());
        String testTheory = Files.readString(file);

        Control control = new Control();
        control.registerObserver(this, false);
        control.add(testTheory);
        control.ground();

        Assert.assertTrue(called.contains("theoryTermString: a"));
        Assert.assertTrue(called.contains("theoryTermString: ="));
        Assert.assertTrue(called.contains("theoryAtomWithGuard"));

        control.solve();
    }

    public void theoryTermString(int termId, String name) {
        called.add("theoryTermString: " + name);
    }

    public void theoryAtomWithGuard(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId) {
        called.add("theoryAtomWithGuard");
        Assert.assertEquals(0, elements.length);
    }
}
