import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstSequence;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.ast.ProgramBuilder;
import org.potassco.clingo.ast.StringSequence;
import org.potassco.clingo.ast.Transformer;
import org.potassco.clingo.ast.UnpoolType;
import org.potassco.clingo.ast.nodes.Comment;
import org.potassco.clingo.ast.nodes.Id;
import org.potassco.clingo.ast.nodes.Program;
import org.potassco.clingo.ast.nodes.Rule;
import org.potassco.clingo.ast.nodes.SymbolicTerm;
import org.potassco.clingo.ast.nodes.TheoryUnparsedTermElement;
import org.potassco.clingo.ast.nodes.Variable;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.WarningCode;

public class AstTest {

    private void logger(WarningCode code, String message) {
        System.out.printf("[%s] %s", code, message);
    }

    private final Map<AstType, List<String>> constructorMapping = getMapping();

    private Ast deepCopy(Ast node) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Constructor<?>[] constructors = node.getClass().getConstructors();
        Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));
        Assert.assertTrue(constructors.length >= 2);
        Constructor<?> constructor = constructors[1];
        Class<?> clazz = node.getClass();

        List<String> parameters = constructorMapping.get(node.getType());
        Object[] arguments = new Object[parameters.size()];
        Assert.assertEquals(parameters.size(), arguments.length);

        for (int i = 0; i < parameters.size(); i++) {
            String parameter = parameters.get(i);
            String getterName = "get" + parameter.substring(0, 1).toUpperCase() + parameter.substring(1);
            Method getter = clazz.getMethod(getterName);
            arguments[i] = getter.invoke(node);
        }

        Ast copy = (Ast) constructor.newInstance(arguments);

        // Also test setters
        for (String parameter : parameters) {
            String getterName = "get" + parameter.substring(0, 1).toUpperCase() + parameter.substring(1);
            String setterName = "set" + parameter.substring(0, 1).toUpperCase() + parameter.substring(1);
            Method getter = clazz.getMethod(getterName);
            Object argument = getter.invoke(node);
            Method setter = Arrays.stream(clazz.getMethods())
                    .filter(method -> method.getName()
                            .startsWith(setterName)).findFirst().orElseThrow();
            setter.invoke(copy, argument);
        }


        return copy;
    }

    private void testString(String term) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        testString(term, term);
    }

    private void testString(String term, String alternative) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        List<Ast> asts = Ast.parseString(term, this::logger, 100);

        Ast node = asts.get(asts.size() - 1);
        Ast copy = deepCopy(node).deepCopy().copy();
        String copyTerm = copy.toString();
        Assert.assertEquals(alternative, copyTerm);

        asts = Ast.parseString(copyTerm, this::logger, 100);
        Ast copy2 = asts.get(asts.size() - 1);
        Assert.assertEquals(alternative, copy2.toString());

        try (Control control = new Control(); ProgramBuilder builder = new ProgramBuilder(control)) {
            try {
                builder.add(copy2);
            } catch (Exception e) {
                String message = e.getMessage();
                if (!message.contains("error: python support not available") ||
                        message.contains("error: lua support not available"))
                    throw e;
            }
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
        testString("not 1 = 2.");
        testString("not not 1 = 2.");
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
        testString("#const x = 10.");
        testString("#const x = 10. [override]");
        testString("#show.");
        testString("#show p/1.");
        testString("#show -p/1.");
        testString("#defined p/1.");
        testString("#defined -p/1.");
        testString("#show x.");
        testString("#show x : y; z.");
        testString(":~ . [1@0]");
        testString(":~ b; c. [1@2,s,t]");
        // testString("#script (lua)\ncode\n#end.");
        // testString("#script (python)\ncode\n#end.");
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
                "  t {\n" +
                "    + : 0, unary;\n" +
                "    - : 1, binary, left;\n" +
                "    * : 2, binary, right\n" +
                "  };\n" +
                "  &a/0: t, head;\n" +
                "  &b/0: t, body;\n" +
                "  &c/0: t, directive;\n" +
                "  &d/0: t, { }, t, any;\n" +
                "  &e/0: t, { =, !=, + }, t, any\n" +
                "}.");
		testString("%* test *%");
		testString("%* test *%\n", "%* test *%");
		testString("% test");
		testString("% test\n", "% test");
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

    @Test
    public void testAstSequence() {
        Location location = new Location("<string>", "<string>", 1, 1, 1, 1);
        Ast[] astArray = new Ast[]{new Id(location, "x"), new Id(location, "y"), new Id(location, "z")};
        Program program = new Program(location, "p", astArray);
        AstSequence parameters = program.getParameters();
        Assert.assertEquals(3, parameters.size());
        Assert.assertArrayEquals(astArray, parameters.get());
        Assert.assertEquals(astArray[0], parameters.get(0));

        parameters.insert(0, new Id(location, "i"));
        Assert.assertArrayEquals(new Ast[]{new Id(location, "i"), new Id(location, "x"), new Id(location, "y"), new Id(location, "z")}, parameters.get());

        parameters.insert(0, parameters.get(3));
        Assert.assertArrayEquals(new Ast[]{new Id(location, "z"), new Id(location, "i"), new Id(location, "x"), new Id(location, "y"), new Id(location, "z")}, parameters.get());

        parameters.delete(2);
        Assert.assertArrayEquals(new Ast[]{new Id(location, "z"), new Id(location, "i"), new Id(location, "y"), new Id(location, "z")}, parameters.get());
    }

    @Test
    public void testAstSequence2() {
        String term = "a :- a; b.";
        List<Ast> asts = Ast.parseString(term);
        Rule rule = (Rule) asts.get(asts.size() - 1);
        rule.setBody(rule.getBody());
        Assert.assertEquals(term, rule.toString());
    }

    @Test
    public void testStringSequence() {
        String[] sequence = new String[]{"x", "y", "z"};
        Location location = new Location("<string>", "<string>", 1, 1, 1, 1);
        SymbolicTerm symbolicTerm = new SymbolicTerm(location, new org.potassco.clingo.symbol.Function("a", new org.potassco.clingo.symbol.Number(1)));
        TheoryUnparsedTermElement unparsedTermElement = new TheoryUnparsedTermElement(sequence, symbolicTerm);
        StringSequence operators = unparsedTermElement.getOperators();
        Assert.assertEquals(3, operators.size());
        Assert.assertArrayEquals(sequence, operators.get());
        Assert.assertEquals(sequence[0], operators.get(0));

        operators.insert(0, "i");
        Assert.assertArrayEquals(new String[]{"i", "x", "y", "z"}, operators.get());
        operators.insert(0, operators.get(3));
        Assert.assertArrayEquals(new String[]{"z", "i", "x", "y", "z"}, operators.get());
        operators.delete(2);
        Assert.assertArrayEquals(new String[]{"z", "i", "y", "z"}, operators.get());
    }

    @Test
    public void testUnpool() {
        List<Ast> program = Ast.parseString("%comment\n:- a(1;2): a(3;4).");
        Assert.assertEquals(3, program.size());

        Comment com = ((Comment) program.get(program.size() - 2));
        List<String> unpooled = com.unpool(UnpoolType.ALL).stream().map(Ast::toString).collect(Collectors.toList());
        Assert.assertEquals(List.of("%comment"), unpooled);

        Ast lit = ((Rule) program.get(program.size()-1)).getBody().get(0);
        unpooled = lit.unpool(UnpoolType.ALL).stream().map(Ast::toString).collect(Collectors.toList());
        Assert.assertEquals(List.of("a(1): a(3)", "a(1): a(4)", "a(2): a(3)", "a(2): a(4)"), unpooled);

        unpooled = lit.unpool(UnpoolType.CONDITION).stream().map(Ast::toString).collect(Collectors.toList());
        Assert.assertEquals(List.of("a(1;2): a(3)", "a(1;2): a(4)"), unpooled);

        unpooled = lit.unpool(UnpoolType.OTHER).stream().map(Ast::toString).collect(Collectors.toList());
        Assert.assertEquals(List.of("a(1): a(3;4)", "a(2): a(3;4)"), unpooled);
    }

    @Test
    public void testCommentOrder() {
        String program = "% comment before `x=10`\n" +
                         "#const x=10.\n" +
                         "% comment after `x=10`\n" +
                         "a.\n" +
                         "% comment before `y=10`\n" +
                         "#const y=10. [override]\n" +
                         "% comment after `y=10`\n" +
                         "b.\n" +
                         "% comment before `#external a`\n" +
                         "#external a.\n" +
                         "% comment after `#external a`\n" +
                         "a.\n" +
                         "% comment before `#external b`\n" +
                         "#external b. [true]\n" +
                         "% comment after `#external b`";
		List<String> expected = List.of(
				"#program base.",
				"% comment before `x=10`",
				"#const x = 10.",
				"% comment after `x=10`",
				"a.",
				"% comment before `y=10`",
				"#const y = 10. [override]",
				"% comment after `y=10`",
				"b.",
				"% comment before `#external a`",
				"#external a. [false]",
				"% comment after `#external a`",
				"a.",
				"% comment before `#external b`",
				"#external b. [true]",
				"% comment after `#external b`"
		);
		List<String> actual = new ArrayList<>();
		Ast.parseString(program, ast -> actual.add(ast.toString()));
		Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTransformer() {
        Transformer transformer = new Transformer() {
            @Override
            public Variable visit(Variable variable) {
                return new Variable(variable.getLocation(), "_" + variable.getName());
            }
        };
        List<Ast> program = new ArrayList<>();
        Ast.parseString("p(X) :- q(X).", ast -> program.add(transformer.visit(ast)));
        Assert.assertEquals("p(_X) :- q(_X).", program.get(program.size() - 1).toString());
    }

    private Map<AstType, List<String>> getMapping() {
        Map<AstType, List<String>> mapping = new HashMap<>();
        mapping.put(AstType.ID, List.of("location", "name"));
        mapping.put(AstType.VARIABLE, List.of("location", "name"));
        mapping.put(AstType.SYMBOLIC_TERM, List.of("location", "symbol"));
        mapping.put(AstType.UNARY_OPERATION, List.of("location", "operatorType", "argument"));
        mapping.put(AstType.BINARY_OPERATION, List.of("location", "operatorType", "left", "right"));
        mapping.put(AstType.INTERVAL, List.of("location", "left", "right"));
        mapping.put(AstType.FUNCTION, List.of("location", "name", "arguments", "external"));
        mapping.put(AstType.POOL, List.of("location", "arguments"));
        mapping.put(AstType.BOOLEAN_CONSTANT, List.of("value"));
        mapping.put(AstType.SYMBOLIC_ATOM, List.of("symbol"));
        mapping.put(AstType.COMPARISON, List.of("comparison", "left", "right"));
        mapping.put(AstType.AGGREGATE_GUARD, List.of("comparison", "term"));
        mapping.put(AstType.CONDITIONAL_LITERAL, List.of("location", "literal", "condition"));
        mapping.put(AstType.AGGREGATE, List.of("location", "leftGuard", "elements", "rightGuard"));
        mapping.put(AstType.BODY_AGGREGATE_ELEMENT, List.of("terms", "condition"));
        mapping.put(AstType.BODY_AGGREGATE, List.of("location", "leftGuard", "function", "elements", "rightGuard"));
        mapping.put(AstType.HEAD_AGGREGATE_ELEMENT, List.of("terms", "condition"));
        mapping.put(AstType.HEAD_AGGREGATE, List.of("location", "leftGuard", "function", "elements", "rightGuard"));
        mapping.put(AstType.DISJUNCTION, List.of("location", "elements"));
        mapping.put(AstType.THEORY_SEQUENCE, List.of("location", "sequenceType", "terms"));
        mapping.put(AstType.THEORY_FUNCTION, List.of("location", "name", "arguments"));
        mapping.put(AstType.THEORY_UNPARSED_TERM_ELEMENT, List.of("operators", "term"));
        mapping.put(AstType.THEORY_UNPARSED_TERM, List.of("location", "elements"));
        mapping.put(AstType.THEORY_GUARD, List.of("operatorName", "term"));
        mapping.put(AstType.THEORY_ATOM_ELEMENT, List.of("terms", "condition"));
        mapping.put(AstType.THEORY_ATOM, List.of("location", "term", "elements", "guard"));
        mapping.put(AstType.LITERAL, List.of("location", "sign", "atom"));
        mapping.put(AstType.THEORY_OPERATOR_DEFINITION, List.of("location", "name", "priority", "operatorType"));
        mapping.put(AstType.THEORY_TERM_DEFINITION, List.of("location", "name", "operators"));
        mapping.put(AstType.THEORY_GUARD_DEFINITION, List.of("operators", "term"));
        mapping.put(AstType.THEORY_ATOM_DEFINITION, List.of("location", "atomType", "name", "arity", "term", "guard"));
        mapping.put(AstType.RULE, List.of("location", "head", "body"));
        mapping.put(AstType.DEFINITION, List.of("location", "name", "value", "isDefault"));
        mapping.put(AstType.SHOW_SIGNATURE, List.of("location", "name", "arity", "positive"));
        mapping.put(AstType.SHOW_TERM, List.of("location", "term", "body"));
        mapping.put(AstType.MINIMIZE, List.of("location", "weight", "priority", "terms", "body"));
        mapping.put(AstType.SCRIPT, List.of("location", "name", "code"));
        mapping.put(AstType.PROGRAM, List.of("location", "name", "parameters"));
        mapping.put(AstType.EXTERNAL, List.of("location", "atom", "body", "externalType"));
        mapping.put(AstType.EDGE, List.of("location", "nodeU", "nodeV", "body"));
        mapping.put(AstType.HEURISTIC, List.of("location", "atom", "body", "bias", "priority", "modifier"));
        mapping.put(AstType.PROJECT_ATOM, List.of("location", "atom", "body"));
        mapping.put(AstType.PROJECT_SIGNATURE, List.of("location", "name", "arity", "positive"));
        mapping.put(AstType.DEFINED, List.of("location", "name", "arity", "positive"));
        mapping.put(AstType.THEORY_DEFINITION, List.of("location", "name", "terms", "atoms"));
        mapping.put(AstType.COMMENT, List.of("location", "value", "commentType"));
        return mapping;
    }
}
