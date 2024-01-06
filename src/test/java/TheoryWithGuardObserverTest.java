import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;

public class TheoryWithGuardObserverTest extends TheoryObserverTest {

    private final Set<String> called = new HashSet<>();

    @Override
	@Test
    public void testBackendObserver() throws IOException {
        String testTheory = Files.readString(
                new File(getClass().getResource("observer-theory-with-guard.lp").getFile()).toPath()
        );

        Control control = new Control();
        control.registerObserver(this, false);
        control.add(testTheory);
        control.ground();

        Assert.assertTrue(called.contains("theoryTermString: a"));
        Assert.assertTrue(called.contains("theoryTermString: ="));
        Assert.assertTrue(called.contains("theoryAtomWithGuard"));

        control.solve();

        control.close();
    }

    @Override
	public void theoryTermString(int termId, String name) {
        called.add("theoryTermString: " + name);
    }

    @Override
    public void theoryAtomWithGuard(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId) {
        called.add("theoryAtomWithGuard");
        Assert.assertEquals(0, elements.length);
    }
}
