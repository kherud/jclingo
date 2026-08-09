import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.configuration.Configuration;
import org.potassco.clingo.configuration.args.Heuristic;
import org.potassco.clingo.configuration.args.OptMode;
import org.potassco.clingo.configuration.args.Option;
import org.potassco.clingo.configuration.args.Statistics;
import org.potassco.clingo.control.Control;

public class ConfigurationTest {

    @Test
    public void testConfiguration() {
        Control control = new Control("-t", "2");
        Configuration configuration = control.getConfiguration();
        List<String> keys = configuration.getKeys();
        Assert.assertTrue(keys.contains("solver"));
        Configuration solverConfig = configuration.get("solver");
        Assert.assertNotNull(solverConfig.get(0));
        Configuration solver0Config = solverConfig.get(0);
        Assert.assertNotNull(solver0Config.get("heuristic"));
        Assert.assertNotNull(solver0Config.get("heuristic").get());
        Assert.assertNotNull(solver0Config.getDescription("heuristic"));
        solver0Config.set("heuristic", "berkmin");
        Assert.assertTrue(solver0Config.get("heuristic").get().startsWith("berkmin"));
        control.close();
    }

    @Test
    public void testStatisticsLevel() {
        try (Control control = new Control()) {
            Configuration configuration = control.getConfiguration();
            configuration.setStatisticsLevel(Statistics.fullStatistics);
            Assert.assertEquals("2", configuration.get("stats").get());
            configuration.setStatisticsLevel(Statistics.noStatistics);
            Assert.assertEquals("0", configuration.get("stats").get());
        }
    }

    /**
     * Options taking a parameter must not mutate the shared enum constant, and the parameter has to reach clingo.
     */
    @Test
    public void testParameterizedOptions() {
        Assert.assertEquals("Vsids", Heuristic.Vsids.getValue());
        Assert.assertEquals("Vsids,92", Heuristic.Vsids.withParameter(92).getValue());
        Assert.assertEquals("Vsids", Heuristic.Vsids.getValue());
        Assert.assertThrows(IllegalStateException.class, () -> Heuristic.Unit.withParameter(1));

        Assert.assertEquals("enum", OptMode.Enum.getValue());
        Assert.assertEquals("enum,3,4", OptMode.Enum.withBounds(3, 4).getValue());
        Assert.assertSame(OptMode.Enum, OptMode.Enum.withBounds());

        try (Control control = new Control()) {
            Configuration configuration = control.getConfiguration();
            configuration.set(Heuristic.Berkmin.withParameter(512));
            Assert.assertEquals("berkmin,512", configuration.get("solver").get(0).get("heuristic").get());
            configuration.set(OptMode.Enum.withBounds(3));
            Assert.assertEquals("enum,3", configuration.get("solve").get("opt_mode").get());
        }
    }

    /**
     * Enumerating below a bound is the reason {@link OptMode#withBounds(long...)} exists, so it is checked end to end.
     */
    @Test
    public void testOptModeBound() {
        try (Control control = new Control("0")) {
            Option[] options = { OptMode.Enum.withBounds(2) };
            control.getConfiguration().set(options);
            control.add("{a; b; c}. #minimize { 1,a: a; 1,b: b; 1,c: c }.");
            control.ground();
            SolvingTest.TestCallback callback = new SolvingTest.TestCallback();
            control.solveResult(callback);
            // one model with no atom, three with a single atom and three with two atoms
            Assert.assertEquals(7, callback.models.size());
        }
    }
}
