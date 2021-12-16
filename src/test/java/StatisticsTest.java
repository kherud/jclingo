import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.statistics.Statistics;
import org.potassco.clingo.statistics.StatisticsArray;
import org.potassco.clingo.statistics.StatisticsMap;
import org.potassco.clingo.statistics.StatisticsType;

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
            Assert.assertEquals(2, ((StatisticsArray) stepTest.get("b")).size());
            Assert.assertTrue(step.get("test.c") instanceof StatisticsMap);
            Assert.assertEquals(1, ((StatisticsMap) step.get("test.c")).size());
            Assert.assertTrue(stepTest.hasKey("a"));

            // TODO: implement rest
        }
    };


    @Test
    public void simpleStatisticsTest() {
        Control control = new Control("-t", "2", "--stats=2");
        control.add("1 { a; b }.");
        control.ground();
        control.solve();
        Statistics statistics = control.getStatistics();
        Assert.assertTrue(statistics.get("problem.lp.atoms").get() >= 2);
        Assert.assertTrue(statistics.get("solving.solvers.choices").get() >= 2);
    }

    @Test
    public void userStatisticsTest() {
        Control control = new Control("-t", "2", "--stats=2");
        control.add("1 { a; b }.");
        control.ground();
        control.solve(onStatistics, SolveMode.NONE);
        Statistics statistics = control.getStatistics();
        StatisticsMap map1 = (StatisticsMap) statistics.get("userStep.test");
        Assert.assertEquals(4, map1.size());
        Assert.assertEquals(1.0, map1.get("a").get(), 1e-9);
        Assert.assertEquals(1.0, map1.get("a").get(), 1e-9);

    }
}
