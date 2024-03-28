import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.GroundCallback;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;

public class OptimizationTest {

    private final GroundCallback groundCallback = new GroundCallback() {
        public Symbol rnd(Symbol index) {
            int rnd = (int) (Math.random() * 100);
            return new Number(rnd);
        }
    };

    private final SolveEventCallback solveCallback = new SolveEventCallback() {
        private long minCost = Integer.MAX_VALUE;

        @Override
        public void onModel(Model model) {
            long[] cost = model.getCost();
            Assert.assertEquals(2, cost.length);
            Assert.assertTrue(cost[0] <= minCost);
            minCost = cost[0];
            System.out.println(model);
            for (long c : cost)
                System.out.print(c + " ");
            System.out.println(model.getOptimalityProven());
        }
    };

    @Test
    public void testCostVector() {
        String program = "#const n = 2." +
                "{a(@rnd(0..100))} = n.\n" +
                "{b(@rnd(0..100))} = n.\n" +
                "#minimize { W@1 : a(W) }.\n" +
                "#minimize { W@2 : b(W) }. ";
        Control control = new Control("--opt-mode=opt", "--single-shot");
        control.add(program);
        control.ground(groundCallback);
        control.solve(solveCallback).wait(-1.);
        control.close();
    }
}
