import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.*;
import org.potassco.clingo.symbol.Symbol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test application covering most of the Application related API.
 */
public class ApplicationTest extends Application {

    private static final List<String> queue = new ArrayList<>();
    private final Flag flag;

    public ApplicationTest() {
        flag = new Flag();
    }

    private boolean parseTest(String value) {
        queue.add("parse");
        queue.add(value);
        return true;
    }

    public void registerOptions(ApplicationOptions options) {
        queue.add("register");
        String group = "Clingo.Test";
        options.addOption(group, "test", "test description", this::parseTest);
        options.addFlag(group, "flag", "test description", this.flag);
    }

    public boolean validateOptions() {
        queue.add("validate");
        queue.add("flag");
        queue.add(String.valueOf(this.flag.get()));
        return true;
    }

    public void log(WarningCode code, String message) {
        queue.add("log");
        queue.add(String.valueOf(code));
        queue.add(message.replaceAll("^.*:(?=[0-9]+:)", ""));
    }

    public void main(Control control, Path[] filePaths) {
        queue.add("main");
        for (Path filePath : filePaths)
            control.load(filePath);
        control.ground();
        SolvingTest.TestCallback callback = new SolvingTest.TestCallback();
        control.solve(callback).wait(-1.);
        queue.add("model");
        for (SolvingTest.TestCallback.ModelTuple tuple : callback.models) {
            queue.add(Arrays.stream(tuple.symbols).map(Symbol::toString).collect(Collectors.joining(" ")));
        }
    }

    @Test
    public void testApplication() throws IOException {
        String program = "1 {a; b; c(1/0)}.";
        Path tmpFile = Files.createTempFile(null, null);
        Files.write(tmpFile, program.getBytes(StandardCharsets.UTF_8));
        Application application = new ApplicationTest();
        int ret = application.run(tmpFile.toAbsolutePath().toString(), "--outf=3", "0", "--test=x", "--flag");

        Assert.assertEquals(30, ret);
        Assert.assertEquals(14, queue.size());

        Assert.assertEquals("register", queue.get(0));
        Assert.assertEquals("parse", queue.get(1));
        Assert.assertEquals("x", queue.get(2));
        Assert.assertEquals("validate", queue.get(3));
        Assert.assertEquals("flag", queue.get(4));
        Assert.assertEquals("true", queue.get(5));
        Assert.assertEquals("main", queue.get(6));
        Assert.assertEquals("log", queue.get(7));
        Assert.assertEquals("operation undefined", queue.get(8));
        Assert.assertEquals("1:12-15: info: operation undefined:\n  (1/0)\n", queue.get(9));
        Assert.assertEquals("model", queue.get(10));
        Assert.assertEquals("a", queue.get(11));
        Assert.assertEquals("b", queue.get(12));
        Assert.assertEquals("a b", queue.get(13));
    }
}
