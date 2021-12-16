import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.control.WarningCode;
import org.potassco.clingo.symbol.*;
import org.potassco.clingo.symbol.Number;

import java.util.ArrayList;
import java.util.List;

public class SymbolTest {

    @Test
    public void testParse() {
        Assert.assertEquals("p(3)", Symbol.fromString("p(3)").toString());
        List<String> log = new ArrayList<>();
        LoggerCallback callback = (WarningCode code, String message) -> log.add(message);
        Assert.assertThrows(RuntimeException.class, () -> Symbol.fromString("p(1/0)", callback));
        Assert.assertEquals(0, log.size());
    }

    @Test
    public void testString() {
        Assert.assertEquals("10", new Number(10).toString());
    }

    @Test
    public void testCompare() {
        Assert.assertEquals(new Number(10), new Number(10));
        Assert.assertNotEquals(new Number(1), new Number(2));
        Assert.assertTrue((new Number(1)).compareTo(new Number(2)) < 0);
        Assert.assertTrue((new Number(2)).compareTo(new Number(1)) > 0);
    }

    @Ignore // Hashing is currently not implemented
    @Test
    public void testHashing() {
        Assert.assertEquals(new Number(10).hashCode(), new Number(10).hashCode());
        Assert.assertNotEquals(new Number(1).hashCode(), new Number(2).hashCode());
    }

    @Test
    public void testMatch() {
        Assert.assertTrue(new Function("f", new Number(1)).match(new Signature("f", 1)));
        Assert.assertFalse(new Function("f", new Number(1)).match(new Signature("f", 2)));
        Assert.assertFalse(new Number(1).match(new Signature("f", 1)));
    }

    @Test
    public void testFunction() {
        Function function = new Function("f", false, new Number(1));
        Assert.assertArrayEquals(function.getArguments(), new Symbol[]{new Number(1)});
        Assert.assertFalse(function.isPositive());
        Assert.assertTrue(function.isNegative());
        Assert.assertEquals("f", function.getName());
        Assert.assertEquals(SymbolType.FUNCTION, function.getType());
        // TODO: maybe add a type for tuple to SymbolType
        // Assert.assertEquals(SymbolType.TUPLE, new Function(null).getType());
    }

    @Test
    public void testInfimumSupremum() {
        Assert.assertEquals(SymbolType.INFIMUM, new Infimum().getType());
        Assert.assertEquals(SymbolType.SUPREMUM, new Supremum().getType());
    }

    @Test
    public void testText() {
        Text text = new Text("blub");
        Assert.assertEquals("\"blub\"", text.toString());
        Assert.assertEquals(SymbolType.STRING, text.getType());
    }

}
