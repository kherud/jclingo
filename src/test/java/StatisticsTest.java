import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.statistics.*;

public class StatisticsTest {

    private final SolveEventCallback onStatistics = new SolveEventCallback() {
        @Override
        public void onStatistics(Statistics step, Statistics accumulated) {
            StatisticsMap stepMap = (StatisticsMap) step;
            StatisticsMap accuMap = (StatisticsMap) accumulated;

            StatisticsMap stepTest = (StatisticsMap) stepMap.addKey("test", StatisticsType.MAP);
            stepTest.set("a", 0);
            stepTest.set("b", new double[]{1, 2});
            StatisticsMap stepTestMap = (StatisticsMap) stepTest.addKey("c", StatisticsType.MAP);
            stepTestMap.set("d", 3);

            accuMap.set("test", stepTest);

            // TODO: should functions be supported as map setters (as with the python API)?

            Assert.assertEquals(3, stepTest.size());
            Assert.assertTrue(step.get("test.b") instanceof StatisticsArray);
            Assert.assertEquals(2, stepTest.get("b").size());
            Assert.assertTrue(step.get("test.c") instanceof StatisticsMap);
            Assert.assertEquals(1, step.get("test.c").size());
            Assert.assertTrue(stepTest.hasKey("a"));

            Assert.assertEquals(3, step.get("test").size());
            Assert.assertEquals("a", ((StatisticsMap) step.get("test")).getKey(0));
            Assert.assertEquals("b", ((StatisticsMap) step.get("test")).getKey(1));
            Assert.assertEquals("c", ((StatisticsMap) step.get("test")).getKey(2));

            Assert.assertEquals("d", ((StatisticsMap) step.get("test.c")).getKey(0));
            Assert.assertEquals(3.0, step.get("test.c.d").get(), 1e-5);

            ((StatisticsValue) step.get("test.b").get(1)).set(99);
            Assert.assertEquals(99, step.get("test.b").get(1).get(), 1e-5);
            ((StatisticsArray) step.get("test.b")).add(new double[]{3, 4});
        }
    };

    @Test
    public void userStatisticsTest() {
        Control control = new Control("-t", "2", "--stats=2");
        control.add("1 { a; b }.");
        control.ground();
        control.solve(onStatistics, SolveMode.NONE);
        Statistics statistics = control.getStatistics();

        StatisticsMap map = (StatisticsMap) statistics.get("user_step.test");
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("a", map.getKey(0));
        Assert.assertEquals(0.0, map.get("a").get(), 1e-9);
        Assert.assertEquals("b", map.getKey(1));
        Assert.assertEquals(1.0, map.get("b").get(0).get(), 1e-9);
        Assert.assertEquals(99.0, map.get("b").get(1).get(), 1e-9);
        Assert.assertEquals(3.0, map.get("b").get(2).get(), 1e-9);
        Assert.assertEquals(4.0, map.get("b").get(3).get(), 1e-9);
        Assert.assertEquals("c", map.getKey(2));
        Assert.assertEquals(1, map.get("c").size());
        Assert.assertEquals(3.0, map.get("c.d").get(), 1e-9);

        // TODO: set user_accu correctly
    }


    @Test
    public void simpleStatisticsTest() {
        Control control = new Control("-t", "2", "--stats=2");
        control.add("1 { a; b }.");
        control.ground();
        control.solve();
        Statistics statistics = control.getStatistics();
        Assert.assertTrue(statistics.get("problem.lp.atoms").get() >= 2.);
        Assert.assertTrue(statistics.get("solving.solvers.choices").get() >= 1.);
    }
}
