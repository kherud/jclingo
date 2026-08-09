import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.ErrorCode;
import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.control.ProgramPart;
import org.potassco.clingo.internal.ClingoRuntimeException;
import org.potassco.clingo.solving.GroundCallback;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.solving.SolveResult;
import org.potassco.clingo.solving.TruthValue;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;
import org.potassco.clingo.symbol.Text;

public class ControlTest {

    @Test
    public void testCreate() {
        LoggerCallback logger = (code, message) -> System.out.printf("[%d] %s\n", code.getValue(), message);
        Control control;
        control = new Control();
        control.close();
        control = new Control(logger, -10);
        control.close();
        control = new Control("--models", "0");
        control.close();
        control = new Control(logger, 10);
        control.close();
        control = new Control(logger, 10, "0");
        control.close();
    }

    @Test
    public void testGround() {
        Control control = new Control();
        control.add("part1", "{a}.");
        control.add("part2", "{b}.");
        ProgramPart programPart1 = new ProgramPart("part1");
        ProgramPart programPart2 = new ProgramPart("part2");
        control.ground(programPart1, programPart2);
        control.close();
    }

    @Test
    public void testGround2() {
        GroundCallback groundCallback = new GroundCallback() {
            public void cb_num_void(Symbol number) {
                assert number instanceof Number;
                addSymbols(((Number) number).mul(2));
            }
        };

        Control control = new Control();
        control.add("p(@cb_num_void(1)).");
        control.ground(groundCallback);

        List<Symbol> symbols = new ArrayList<>();
        control.getSymbolicAtoms().forEach(atom -> symbols.add(atom.getSymbol()));
        Assert.assertEquals(List.of(new Function("p", new Number(2))), symbols);

        control.close();
    }

    @Test
    public void testGround3() {
        GroundCallback groundCallback = new GroundCallback() {
            public Symbol cb_num_ret(Symbol number) {
                assert number instanceof Number;
                return ((Number) number).mul(2);
            }
        };

        Control control = new Control();
        control.add("p(@cb_num_ret(1)).");
        control.ground(groundCallback);

        List<Symbol> symbols = new ArrayList<>();
        control.getSymbolicAtoms().forEach(atom -> symbols.add(atom.getSymbol()));
        Assert.assertEquals(List.of(new Function("p", new Number(2))), symbols);

        control.close();
    }

    @Test
    public void testGround4() {
        GroundCallback groundCallback = new GroundCallback() {
            public Symbol cb_num_ret(Symbol number) {
                assert number instanceof Number;
                return ((Number) number).mul(2);
            }
        };

        Control control = new Control();
        control.add("part", "p(@cb_num_ret(c)).", "c");
        control.ground(groundCallback, new ProgramPart("part", new Number(1)));

        List<Symbol> symbols = new ArrayList<>();
        control.getSymbolicAtoms().forEach(atom -> symbols.add(atom.getSymbol()));
        Assert.assertEquals(List.of(new Function("p", new Number(2))), symbols);

        control.close();
    }

    @Test
    public void testGround5() {
        GroundCallback groundCallback = new GroundCallback() {
            public Symbol cb_a(Symbol arg1, Symbol arg2, Symbol arg3) {
                return new Function(arg1, arg2, arg3);
            }

            public void cb_b(Symbol arg1) {
                addSymbols(arg1);
            }
        };

        Control control = new Control();
        control.add("part1", "a(@cb_a(c, d, e)).", "c", "d", "e");
        control.add("part2", "b(@cb_b(f)).", "f");
        ProgramPart programPart1 = new ProgramPart("part1", new Function("1"), new Number(2), new Text("3"));
        ProgramPart programPart2 = new ProgramPart("part2", new Function("g", false, new Function("h")));
        control.ground(groundCallback, programPart1, programPart2);

        Set<Symbol> symbols = new HashSet<>();
        control.getSymbolicAtoms().forEach(atom -> symbols.add(atom.getSymbol()));

        Set<Symbol> expected = Set.of(
                new Function("a", new Function(new Function("1"), new Number(2), new Text("3"))),
                new Function("b", new Function("g", false, new Function("h")))
        );

        Assert.assertEquals(expected, symbols);

        control.close();
    }

    @Test
    public void testLowerBounds() {
        List<Long> unsatSymbols = new ArrayList<>();
        SolveEventCallback callback = new SolveEventCallback() {
            @Override
            public void onUnsat(long[] literals) {
                Arrays.sort(literals);
                for (long literal : literals)
                    unsatSymbols.add(literal);
            }

            @Override
            public void onResult(SolveResult solveResult) {
                Assert.assertTrue(solveResult.isType(SolveResult.Type.SATISFIABLE));
            }
        };
        Control control = new Control("--opt-str=usc,oll,0", "--stats=2", "0");
        control.add("1 { p(X); q(X) } 1 :- X=1..3. #minimize { 1,p,X: p(X); 1,q,X: q(X) }.");
        control.ground();
        control.solveResult(callback);

        Assert.assertEquals(List.of(1L, 2L, 3L), unsatSymbols);
        Assert.assertEquals(3.0, control.getStatistics().get("summary.lower").get(0).get(), 1e-5);

        control.close();
    }

    /**
     * An exception raised by an external function has to abort grounding and reach the caller unchanged.
     */
    @Test
    public void testGroundError() {
        GroundCallback groundCallback = new GroundCallback() {
            public Symbol cb_error(Symbol argument) {
                throw new IllegalArgumentException("no value for " + argument);
            }
        };

        try (Control control = new Control()) {
            control.add("p(@cb_error(1)).");
            IllegalArgumentException exception = Assert.assertThrows(
                    IllegalArgumentException.class,
                    () -> control.ground(groundCallback)
            );
            Assert.assertEquals("no value for 1", exception.getMessage());
        }
    }

    /**
     * A single ground callback can serve more than one control, because the sink to inject symbols into is not kept as
     * instance state.
     */
    @Test
    public void testGroundCallbackReuse() {
        GroundCallback groundCallback = new GroundCallback() {
            public Symbol twice(Symbol number) {
                return ((Number) number).mul(2);
            }
        };

        for (int i = 1; i <= 2; i++) {
            try (Control control = new Control()) {
                control.add("p(@twice(" + i + ")).");
                control.ground(groundCallback);
                List<Symbol> symbols = new ArrayList<>();
                control.getSymbolicAtoms().forEach(atom -> symbols.add(atom.getSymbol()));
                Assert.assertEquals(List.of(new Function("p", new Number(2 * i))), symbols);
            }
        }
    }

    /**
     * Injecting symbols outside of a call from clingo would write through a stale pointer.
     */
    @Test
    public void testGroundOutsideCallback() {
        GroundCallback groundCallback = new GroundCallback() {
        };
        Assert.assertThrows(IllegalStateException.class, () -> groundCallback.addSymbols(new Number(1)));
    }

    /**
     * clingo terminates the process when a solve event callback reports failure, so an exception raised by one has to
     * surface through the solve handle instead.
     */
    @Test
    public void testSolveEventError() {
        for (SolveMode solveMode : List.of(SolveMode.NONE, SolveMode.YIELD, SolveMode.ASYNC)) {
            SolveEventCallback callback = new SolveEventCallback() {
                @Override
                public void onModel(Model model) {
                    throw new IllegalStateException("model rejected");
                }
            };
            try (Control control = new Control()) {
                control.add("1 {a; b} 1.");
                control.ground();
                try (SolveHandle handle = control.solve(callback, solveMode)) {
                    IllegalStateException exception = Assert.assertThrows(
                            IllegalStateException.class,
                            handle::getSolveResult
                    );
                    Assert.assertEquals("model rejected", exception.getMessage());
                }
            }
        }
    }

    /**
     * The last event of a search is reported after clingo stopped accepting failures, so an exception raised there has
     * to surface as well.
     */
    @Test
    public void testSolveResultError() {
        SolveEventCallback callback = new SolveEventCallback() {
            @Override
            public void onResult(SolveResult result) {
                throw new IllegalStateException("result rejected");
            }
        };
        try (Control control = new Control()) {
            control.add("1 {a; b} 1.");
            control.ground();
            try (SolveHandle handle = control.solve(callback)) {
                IllegalStateException exception = Assert.assertThrows(
                        IllegalStateException.class,
                        handle::getSolveResult
                );
                Assert.assertEquals("result rejected", exception.getMessage());
            }
        }
    }

    /**
     * A failing clingo call reports a code the caller can act on.
     */
    @Test
    public void testErrorCode() {
        // the message is asserted on, so it does not have to be logged as well
        try (Control control = new Control((code, message) -> {}, 0)) {
            ClingoRuntimeException exception = Assert.assertThrows(
                    ClingoRuntimeException.class,
                    () -> control.add("a :-")
            );
            Assert.assertEquals(ErrorCode.RUNTIME, exception.getErrorCode());
        }
    }

    /**
     * Freeing the same native object twice would be a double free.
     */
    @Test
    public void testDoubleClose() {
        Control control = new Control();
        control.add("a.");
        control.ground();
        SolveHandle handle = control.solve();
        handle.getSolveResult();
        handle.close();
        handle.close();
        control.close();
        control.close();
    }

    /**
     * Assigning an external takes a truth value, and releasing it is a separate operation.
     */
    @Test
    public void testAssignExternal() {
        try (Control control = new Control("0")) {
            control.add("#external a. b :- a.");
            control.ground();

            Symbol[] external = { new Function("a") };
            Assert.assertEquals(List.of(""), solveSymbols(control));
            control.assignExternal(external, TruthValue.TRUE);
            Assert.assertEquals(List.of("a b"), solveSymbols(control));
            control.assignExternal(external, TruthValue.FALSE);
            Assert.assertEquals(List.of(""), solveSymbols(control));
            control.assignExternal(external, TruthValue.FREE);
            Assert.assertEquals(List.of("", "a b"), solveSymbols(control));
            control.releaseExternal(external);
            control.assignExternal(external, TruthValue.TRUE);
            Assert.assertEquals(List.of(""), solveSymbols(control));
        }
    }

    private List<String> solveSymbols(Control control) {
        SolvingTest.TestCallback callback = new SolvingTest.TestCallback();
        control.solveResult(callback);
        return callback.models.stream()
                .map(model -> Arrays.stream(model.symbols).map(Symbol::toString).collect(Collectors.joining(" ")))
                .collect(Collectors.toList());
    }
}
