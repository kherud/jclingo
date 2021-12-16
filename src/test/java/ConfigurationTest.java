import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.configuration.Configuration;
import org.potassco.clingo.control.Control;

import java.util.List;

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
    }
}
