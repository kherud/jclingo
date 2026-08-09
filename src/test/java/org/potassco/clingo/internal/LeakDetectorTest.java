package org.potassco.clingo.internal;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;

/**
 * Resources have to be closed explicitly, because clingo does not count its references atomically and freeing them
 * from a cleaner thread would trade a leak for a crash. The detector is what makes a forgotten close visible.
 */
public class LeakDetectorTest {

    /**
     * Both cases share one test, because the detector reports into one process wide list that either of them would
     * otherwise leave behind for the other.
     */
    @Test
    public void testOnlyUnclosedResourcesAreReported() {
        new Control().close();
        LeakDetector.assertNoLeaks();

        leak();
        Assert.assertThrows(IllegalStateException.class, LeakDetector::assertNoLeaks);
    }

    /**
     * In its own frame, so that the control does not stay reachable through a local variable of the test method.
     */
    private void leak() {
        Control control = new Control();
        Assert.assertNotNull(control);
    }
}
