import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.ast.*;
import org.potassco.clingo.ast.nodes.Id;
import org.potassco.clingo.ast.nodes.Program;
import org.potassco.clingo.ast.nodes.Rule;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.WarningCode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AstTest {

    private void logger(WarningCode code, String message) {
        System.out.printf("[%s] %s", code, message);
    }

    private Ast deepCopy(Ast node) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // TODO: update reflection
        Constructor<?>[] constructors = node.getClass().getConstructors();
        Assert.assertEquals(2, constructors.length);
        Constructor<?> constructor = constructors[1];
        Method[] methods = node.getClass().getDeclaredMethods();
        List<Method> getters = Arrays.stream(methods).filter(method -> method.getName().startsWith("get")).collect(Collectors.toList());
        getters.sort((o1, o2) -> {
            Class<?>[] types = constructor.getParameterTypes();
            int pos1 = IntStream.range(0, types.length).filter(i -> types[i] == o1.getReturnType()).findFirst().orElseThrow();
            int pos2 = IntStream.range(0, types.length).filter(i -> types[i] == o2.getReturnType()).findFirst().orElseThrow();
            return Integer.compare(pos1, pos2);
        });

        Object[] arguments = new Object[getters.size()];
        for (int i = 0; i < getters.size(); i++) {
            arguments[i] = getters.get(i).invoke(node);
        }

        return (Ast) constructor.newInstance(arguments);
    }

    private void testString(String term) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        testString(term, term);
    }

    private void testString(String term, String alternative) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List<Ast> asts = Ast.parseString(term, this::logger, 100);

        Ast node = asts.get(asts.size() - 1);
        Ast copy = deepCopy(node).deepCopy().copy();
        String copyTerm = copy.toString();
        Assert.assertEquals(alternative, copyTerm);

        asts = Ast.parseString(copyTerm, this::logger, 100);
        Ast copy2 = asts.get(asts.size() - 1);
        Assert.assertEquals(alternative, copy2.toString());

        Control control = new Control();
        try (ProgramBuilder builder = new ProgramBuilder(control)) {
            builder.add(copy2);
        }
    }

    @Test
    public void testTerms() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        testString("a.");
        testString("-a.");
        testString("a(X).");
        testString("a(-X).");
        testString("a(|X|).");
        testString("a(~X).");
        testString("a((X^Y)).");
        testString("a((X?Y)).");
        testString("a((X&Y)).");
        testString("a((X+Y)).");
        testString("a((X-Y)).");
        testString("a((X*Y)).");
        testString("a((X/Y)).");
        testString("a((X\\Y)).");
        testString("a((X**Y)).");
        testString("a((X..Y)).");
        testString("-a(f).");
        testString("-a(-f).");
        testString("-a(f(X)).");
        testString("-a(f(X,Y)).");
        testString("-a(()).");
        testString("-a((a,)).");
        testString("-a((a,b)).");
        testString("-a(@f(a,b)).");
        testString("-a(@f).");
        testString("-a(a;b;c).");
        testString("-a((a;b;c)).");
        testString("-a(f(a);f(b);f(c)).");
    }

    @Test
    public void testTheoryTerms() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        testString("&a { 1 }.");
        testString("&a { (- 1) }.");
        testString("&a { X }.");
        testString("&a { () }.");
        testString("&a { (1,) }.");
        testString("&a { (1,2) }.");
        testString("&a { [] }.");
        testString("&a { [1] }.");
        testString("&a { [1,2] }.");
        testString("&a { {} }.");
        testString("&a { {1} }.");
        testString("&a { {1,2} }.");
        testString("&a { f }.");
        testString("&a { f(X) }.");
        testString("&a { f(X,Y) }.");
        testString("&a { (+ a + - * b + c) }.");
    }

    @Test
    public void testLiterals() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        testString("a.");
        testString("not a.");
        testString("not not a.");
        testString("1 < 2.");
        testString("1 <= 2.");
        testString("1 > 2.");
        testString("1 >= 2.");
        testString("1 = 2.");
        testString("1 != 2.");
        testString("#false.");
        testString("#true.");
    }

    @Test
    public void testHeadLiterals() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        testString("{ }.");
        testString("{ } < 2.", "2 > { }.");
        testString("1 < { }.");
        testString("1 < { } < 2.");
        testString("{ b }.");
        testString("{ a; b }.");
        testString("{ a; b: c, d }.");
        testString("#count { }.");
        testString("#count { } < 2.", "2 > #count { }.");
        testString("1 < #count { }.");
        testString("1 < #count { } < 2.");
        testString("#count { b: a }.");
        testString("#count { b,c: a }.");
        testString("#count { a: a; b: c }.");
        testString("#count { a: d; b: x: c, d }.");
        testString("#min { }.");
        testString("#max { }.");
        testString("#sum { }.");
        testString("#sum+ { }.");
        testString("a; b.");
        testString("a; b: c.");
        testString("a; b: c, d.");
        testString("&a { }.");
        testString("&a { 1 }.");
        testString("&a { 1; 2 }.");
        testString("&a { 1,2 }.");
        testString("&a { 1,2: a }.");
        testString("&a { 1,2: a, b }.");
        testString("&a { } != x.");
        testString("&a(x) { }.");
    }

    @Test
    public void testBodyLiterals() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        testString("a :- { }.");
        testString("a :- not { }.");
        testString("a :- not not { }.");
        testString("a :- { } < 2.", "a :- 2 > { }.");
        testString("a :- 1 < { }.");
        testString("a :- 1 < { } < 2.");
        testString("a :- { b }.");
        testString("a :- { a; b }.");
        testString("a :- { a; b: c, d }.");
        testString("a :- #count { }.");
        testString("a :- not #count { }.");
        testString("a :- not not #count { }.");
        testString("a :- #count { } < 2.", "a :- 2 > #count { }.");
        testString("a :- 1 < #count { }.");
        testString("a :- 1 < #count { } < 2.");
        testString("a :- #count { b }.");
        testString("a :- #count { b,c }.");
        testString("a :- #count { a; b }.");
        testString("a :- #count { a; b: c, d }.");
        testString("a :- #min { }.");
        testString("a :- #max { }.");
        testString("a :- #sum { }.");
        testString("a :- #sum+ { }.");
        testString("a :- a; b.");
        testString("a :- a; b: c.");
        testString("a :- a; b: c, d.");
        testString("a :- &a { }.");
        testString("a :- &a { 1 }.");
        testString("a :- &a { 1; 2 }.");
        testString("a :- &a { 1,2 }.");
        testString("a :- &a { 1,2: a }.");
        testString("a :- &a { 1,2: a, b }.");
        testString("a :- &a { } != x.");
        testString("a :- &a(x) { }.");
        testString("a :- a.");
        testString("a :- not a.");
        testString("a :- not not a.");
        testString("a :- 1 < 2.");
        testString("a :- 1 <= 2.");
        testString("a :- 1 > 2.");
        testString("a :- 1 >= 2.");
        testString("a :- 1 = 2.");
        testString("a :- 1 != 2.");
        testString("a :- #false.");
        testString("a :- #true.");
    }

    @Test
    public void testStatements() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        testString("a.");
        testString("#false.");
        testString("#false :- a.");
        testString("a :- a; b.");
        // testString("#const x = 10.");
        // testString("#const x = 10. [override]");
        testString("#show p/1.");
        testString("#show -p/1.");
        testString("#defined p/1.");
        testString("#defined -p/1.");
        testString("#show x.");
        testString("#show x : y; z.");
        testString(":~ . [1@0]");
        testString(":~ b; c. [1@2,s,t]");
        testString("#script (lua)\ncode\n#end.");
        testString("#script (python)\ncode\n#end.");
        testString("#program x(y, z).");
        testString("#program x.");
        testString("#external a. [X]");
        testString("#external a : b; c. [false]");
        testString("#edge (1,2).");
        testString("#edge (1,2) : x; y.");
        testString("#heuristic a. [b@p,m]");
        testString("#heuristic a : b; c. [b@p,m]");
        testString("#project a.");
        testString("#project a : b; c.");
        testString("#project -a/0.");
        testString("#project a/0.");
        testString("#theory x {\n}.");
        testString("#theory x {\n" +
                "                           t {\n" +
                "                             + : 0, unary;\n" +
                "                             - : 1, binary, left;\n" +
                "                             * : 2, binary, right\n" +
                "                           };\n" +
                "                           &a/0: t, head;\n" +
                "                           &b/0: t, body;\n" +
                "                           &c/0: t, directive;\n" +
                "                           &d/0: t, { }, t, any;\n" +
                "                           &e/0: t, { =, !=, + }, t, any\n" +
                "                         }.\"\"\"");
    }

    @Test
    public void testCompare() {
        Location location = new Location("<string>", "<string>", 1, 1, 1, 1);
        Location location2 = new Location("<string>", "<string>", 1, 1, 1, 2);
        Id x = new Id(location, "x");
        Id y = new Id(location2, "x");
        Id z = new Id(location, "z");
        Assert.assertEquals(x, y);
        Assert.assertEquals(x, x);
        Assert.assertNotEquals(x, z);
        Assert.assertTrue(x.compareTo(z) < 0);
        Assert.assertTrue(x.compareTo(z) != 0);
        Assert.assertTrue(z.compareTo(x) > 0);
        Assert.assertTrue(y.compareTo(x) <= 0);
        Assert.assertTrue(x.compareTo(y) <= 0);
        Assert.assertTrue(y.compareTo(x) >= 0);
        Assert.assertTrue(x.compareTo(y) >= 0);
    }

    public void testAstSequence() {
        Location location = new Location("<string>", "<string>", 1, 1, 1, 1);
        // AstSequence sequence = new AstSequence();
        // Program program = new Program(location, "p", sequence);
    }

    @Test
    public void testUnpool() {
        List<Ast> program = Ast.parseString(":- a(1;2): a(3;4).");
        Ast lit = ((Rule) program.get(program.size()-1)).getBody().get(0);
        List<String> unpooled = lit.unpool(UnpoolType.ALL).stream().map(Ast::toString).collect(Collectors.toList());
        Assert.assertEquals(List.of("a(1): a(3)", "a(1): a(4)", "a(2): a(3)", "a(2): a(4)"), unpooled);

        unpooled = lit.unpool(UnpoolType.CONDITION).stream().map(Ast::toString).collect(Collectors.toList());
        Assert.assertEquals(List.of("a(1;2): a(3)", "a(1;2): a(4)"), unpooled);

        unpooled = lit.unpool(UnpoolType.OTHER).stream().map(Ast::toString).collect(Collectors.toList());
        Assert.assertEquals(List.of("a(1): a(3;4)", "a(2): a(3;4)"), unpooled);

    }

    @Test
    public void testTransformer() {
        Ast.parseString("p(X) :- q(X).", new AstCallback() {
            @Override
            public void call(Ast ast) {

            }
        });
    }
}
