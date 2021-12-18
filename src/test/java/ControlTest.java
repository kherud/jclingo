import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import org.junit.Assert;
import org.junit.Test;
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
        control.add("part", "{a}.");
//        ProgramPart programPart = new ProgramPart("part");
//        control.ground(programPart);
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

        Assert.assertEquals(unsatSymbols, List.of(1L, 2L, 3L));
//        Assert.assertEquals(control.getStatistics());

    }
}
