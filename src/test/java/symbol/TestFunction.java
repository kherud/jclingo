package symbol;

import org.junit.Test;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;
import org.potassco.clingo.symbol.Text;

public class TestFunction {

    @Test
    public void testCreateFunction() {
        Function function = new Function("b", new Number(0), new Text("a"));
//        Assert.assertEquals("b", function.getName());
//        Assert.assertEquals(2, function.getArity());
//        Assert.assertEquals("0", function.getArguments()[0].toString());
//        Assert.assertEquals("\"a\"", function.getArguments()[1].toString());
        System.out.println(function.toString());
//        Assert.assertEquals("b(0, \"a\")", function.toString());
    }

    @Test
    public void testCreateFunction2() {
        Function function = new Function("b", true, new Number(0), new Text("a"));
        Function function2 = new Function("c", false, function, new Text("d"));
//        Assert.assertEquals("c", function2.getName());
//        Assert.assertEquals(2, function2.getArity());
//        Assert.assertEquals("0", function.getArguments()[0].toString());
//        Assert.assertEquals("\"a\"", function.getArguments()[1].toString());
        System.out.println(function2);
    }

    @Test
    public void testCreateFromString() {
        Symbol symbol = Symbol.fromString("a(1, \"b\")");
        System.out.println(symbol);
    }

    @Test
    public void testCreateFromString2() {
        Symbol symbol = Symbol.fromString("a(1, b)");
        System.out.println(symbol);
    }

    @Test
    public void testCreateArithmeticFunction() {
        Symbol symbol = Symbol.fromString("a(1, 1+1)");
        System.out.println(symbol);
    }
}
