import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.control.*;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.solving.*;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    }

    private void groundCallback(Location location, String name, Symbol[] arguments, SymbolCallback symbolCallback) {
        symbolCallback.callback(null, 1);
        throw new IllegalStateException();
    }

    @Test
    public void testGround2() {
        Control control = new Control();
        control.add("part", "p(@cb_num(c)).", "c");
        Assert.assertThrows(IllegalStateException.class, () -> control.ground(this::groundCallback, new ProgramPart("part", new Number(1))));

    }

    @Test
    public void testGroundError() {
        Control control = new Control();
        control.add("base", "p(@cb_error(c)).", "c");;
        Assert.assertThrows(IllegalStateException.class, () -> control.ground(this::groundCallback, new ProgramPart("part", new Number(1))));
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
        control.solve(callback).wait(-1.);

        Assert.assertEquals(List.of(1L, 2L, 3L), unsatSymbols);
        Assert.assertEquals(3.0, control.getStatistics().get("summary.lower").get(0).get(), 1e-5);

    }

    @Test
    public void testErrorHandling() {
        SolveEventCallback callback = new SolveEventCallback() {
            @Override
            public void onModel(Model model) {
                int x = 1 / 0;
            }
        };

        Control control = new Control();
        control.add("base", "1 {a; b} 1.");
        control.ground();
        Assert.assertThrows(ArithmeticException.class, () -> control.solve(callback));

        try (SolveHandle handle = control.solve(callback, SolveMode.YIELD)) {
            Assert.assertThrows(ArithmeticException.class, handle::getSolveResult);
        }

        try (SolveHandle handle = control.solve(callback, SolveMode.ASYNC)) {
            Assert.assertThrows(ArithmeticException.class, handle::getSolveResult);
        }
    }
}
