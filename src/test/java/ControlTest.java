import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.control.ProgramPart;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.solving.*;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;
import org.potassco.clingo.symbol.Text;

import java.util.*;

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
        control.solve(callback).wait(-1.);

        Assert.assertEquals(List.of(1L, 2L, 3L), unsatSymbols);
        Assert.assertEquals(3.0, control.getStatistics().get("summary.lower").get(0).get(), 1e-5);

        control.close();
    }

    //TODO: find a way to test errors
    // error are correctly thrown, but the testing methods do not work
//    @Test(expected = IllegalStateException.class)
//    public void testGroundError() {
//        GroundCallback groundCallback = new GroundCallback() {
//            public void cb_error(Symbol number) {
//                throw new NoSuchElementException();
//            }
//        };
//
//        Control control = new Control();
//        control.add("base", "p(@cb_error(c)).");
//        control.ground(groundCallback);
//
//        control.close();
//    }


//    @Test(expected = ArithmeticException.class)
//    public void testErrorHandling() {
//        SolveEventCallback callback = new SolveEventCallback() {
//            @Override
//            public void onModel(Model model) {
//                int x = 1 / 0;
//            }
//        };
//
//        Control control = new Control();
//        control.add("base", "1 {a; b} 1.");
//        control.ground();
//        Assert.assertThrows(ArithmeticException.class, () -> control.solve(callback));
//
//        try (SolveHandle handle = control.solve(callback, SolveMode.YIELD)) {
//            Assert.assertThrows(ArithmeticException.class, handle::getSolveResult);
//        }
//
//        try (SolveHandle handle = control.solve(callback, SolveMode.ASYNC)) {
//            Assert.assertThrows(ArithmeticException.class, handle::getSolveResult);
//        }
//
//        control.close();
//    }
}
