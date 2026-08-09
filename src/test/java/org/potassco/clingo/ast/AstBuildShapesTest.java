package org.potassco.clingo.ast;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.ast.nodes.Variable;
import org.potassco.clingo.symbol.Number;

/**
 * Builds one node of every {@link AstType} through the variadic {@code clingo_ast_build}.
 * <p>
 * Every constructor argument kind is passed in the shape derived from the table clingo exports about itself, so this
 * covers every calling convention the binding can produce on the platform it runs on. The nodes are not meant to be
 * meaningful programs - what is verified is that the arguments arrive where clingo expects them.
 */
public class AstBuildShapesTest {

    private static final Location LOCATION = new Location("<begin>", "<end>", 1, 2, 3, 4);

    @Test
    public void testEveryAstTypeCanBeBuilt() {
        for (AstType type : AstType.values()) {
            AttributeType[] specification = AstBuildInvokers.specificationOf(type);
            try (AstScope scope = new AstScope()) {
                Object[] arguments = new Object[specification.length];
                for (int i = 0; i < specification.length; i++) {
                    arguments[i] = sample(specification[i], scope);
                }
                Ast node = scope.add(Ast.create(AstBuildInvokers.build(type, arguments)));
                Assert.assertEquals(type, node.getType());
                // a location is the widest argument and follows arguments of every other kind, so reading it back
                // catches a call shape that got out of step somewhere before it
                if (Arrays.asList(specification).contains(AttributeType.LOCATION)) {
                    Assert.assertEquals(LOCATION, node.getLocation(AstAttribute.LOCATION));
                }
                if (Arrays.asList(specification).contains(AttributeType.STRING_ARRAY)) {
                    Assert.assertArrayEquals(new String[] { "first", "second" },
                            new StringSequence(node, AstAttribute.OPERATORS).get());
                }
            }
        }
    }

    @Test
    public void testWrongArgumentCountIsRejected() {
        Assert.assertThrows(IllegalArgumentException.class, () -> AstBuildInvokers.build(AstType.VARIABLE, LOCATION));
    }

    @Test
    public void testWrongArgumentTypeIsRejected() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> AstBuildInvokers.build(AstType.VARIABLE, LOCATION, 42));
    }

    private Object sample(AttributeType attribute, AstScope scope) {
        return switch (attribute) {
            case NUMBER -> 1;
            case SYMBOL -> new Number(42);
            case LOCATION -> LOCATION;
            case STRING -> "sample";
            case AST -> scope.add(new Variable(LOCATION, "X"));
            case OPTIONAL_AST -> null;
            case STRING_ARRAY -> new String[] { "first", "second" };
            case AST_ARRAY -> new Ast[] { scope.add(new Variable(LOCATION, "Y")) };
        };
    }
}
