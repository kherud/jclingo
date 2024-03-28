import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.potassco.clingo.symbol.Text;

@RunWith(Parameterized.class)
public class TestNumber {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String> sources() {
        return Arrays.asList(
                "A",
                "B1",
                "~!§$(/!",
                "\uD83E\uDD2F\uD83E\uDD51"
        );
    }

    private final String text;

    public TestNumber(String text) {
        this.text = text;
    }

    @Test
    public void testCreateText() {
        Text text = new Text(this.text);
        Assert.assertEquals(text.getText(), this.text);
    }
}
