package org.potassco.clingo.solving;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;

/**
 * Under JNA the trampoline of a solve event callback could be freed while clingo was still calling it, which crashed
 * the process. The stub is now owned by the arena of the search, so a collection during the search must not disturb it.
 */
public class AsyncSolveCallbackTest {

    @Test
    public void testEventCallbackSurvivesGarbageCollectionDuringAnAsynchronousSearch() {
        AtomicInteger models = new AtomicInteger();
        try (Control control = new Control("0")) {
            control.add("1 {a(1..12)} 12.");
            control.ground();
            SolveEventCallback callback = new SolveEventCallback() {
                @Override
                public void onModel(Model model) {
                    models.incrementAndGet();
                    System.gc();
                }
            };
            try (SolveHandle handle = control.solve(callback, SolveMode.ASYNC)) {
                while (!handle.wait(0.01)) {
                    System.gc();
                }
                Assert.assertTrue(handle.getSolveResult().satisfiable());
            }
        }
        Assert.assertEquals(4095, models.get());
    }

    /**
     * An exception in a solve event must not be reported to clingo, which would exit the process, but stop the search
     * and surface on the thread that started it.
     */
    @Test
    public void testEventCallbackErrorReachesTheCaller() {
        try (Control control = new Control("0")) {
            control.add("1 {a(1..12)} 12.");
            control.ground();
            SolveEventCallback callback = new SolveEventCallback() {
                @Override
                public void onModel(Model model) {
                    throw new IllegalStateException("callback failed");
                }
            };
            IllegalStateException error = Assert.assertThrows(IllegalStateException.class,
                    () -> control.solveResult(callback));
            Assert.assertEquals("callback failed", error.getMessage());
        }
    }

    /**
     * A model is only valid until the search continues, so the java object handed out for it refuses to be used
     * afterwards instead of reading whatever clingo put there in the meantime.
     */
    @Test
    public void testModelIsInvalidatedWhenTheSearchContinues() {
        try (Control control = new Control("0")) {
            control.add("1 {a; b} 1.");
            control.ground();
            try (SolveHandle handle = control.solve(SolveMode.YIELD)) {
                handle.resume();
                Model model = handle.getModel();
                Assert.assertNotNull(model.getSymbols());
                handle.resume();
                Assert.assertThrows(IllegalStateException.class, model::getSymbols);
            }
        }
    }
}
