/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.potassco.clingo.ast;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.ast.nodes.*;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a node in the abstract syntax tree.
 * <p>
 * The attributes of an `AST` are tied to its type.
 * <p>
 * Furthermore, AST nodes implement comparison operators and are
 * ordered structurally ignoring the location. Their string representation
 * corresponds to their gringo representation. In fact, the string
 * representation of any AST obtained from `parse_files` and `parse_string`
 * can be parsed again. Note that it is possible to construct ASTs
 * that are not parsable, though.
 */
public abstract class Ast implements Comparable<Ast> {

    protected final Pointer ast;

    public Ast(Pointer ast) {
        this.ast = ast;
        Clingo.INSTANCE.clingo_ast_acquire(ast);
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_to_string_size(ast, nativeSizeByReference));
        int stringSize = (int) nativeSizeByReference.getValue();
        byte[] stringBytes = new byte[stringSize];
        Clingo.check(Clingo.INSTANCE.clingo_ast_to_string(ast, stringBytes, new NativeSize(stringSize)));
        // return Native.toString(stringBytes);
        return new String(Arrays.copyOf(stringBytes, stringBytes.length - 1));
    }

    /**
     * @return The type of the node.
     */
    public AstType getType() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_get_type(ast, intByReference));
        return AstType.fromOrdinal(intByReference.getValue());
    }

    /**
     * Get the type of the given AST attribute.
     *
     * @param attribute the target attribute
     * @return the resulting type
     */
    public AttributeType getAttributeType(Attribute attribute) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_type(ast, attribute.ordinal(), intByReference));
        return AttributeType.fromValue(intByReference.getValue());
    }

    /**
     * Check if an AST has the given attribute.
     *
     * @param attribute the attribute to check
     * @return the result
     */
    public boolean hasAttribute(Attribute attribute) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_has_attribute(ast, attribute.ordinal(), byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Unpool the AST returning a list of ASTs without pool terms.
     *
     * @param unpoolType how to unpool
     * @return list of asts
     */
    public List<Ast> unpool(UnpoolType unpoolType) {
        List<Ast> returnValues = new ArrayList<>();
        AstCallback callback = (Ast ast) -> {
            Clingo.INSTANCE.clingo_ast_acquire(ast.getPointer());
            returnValues.add(ast);
        };
        Clingo.check(Clingo.INSTANCE.clingo_ast_unpool(ast, unpoolType.getValue(), callback, null));
        return returnValues;
    }

    /**
     * Compute a hash for an AST node.
     *
     * @return the resulting hash code
     */
    public long getHash() {
        return Clingo.INSTANCE.clingo_ast_hash(ast).longValue();
    }

    /**
     * @return Return a shallow copy of the ast.
     */
    public Ast copy() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_copy(ast, pointerByReference));
        return create(pointerByReference.getValue());
    }

    /**
     * @return Return a deep copy of the ast.
     */
    public Ast deepCopy() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_deep_copy(ast, pointerByReference));
        return create(pointerByReference.getValue());
    }

    /**
     * Parse the given program and return a list of abstract syntax trees for each statement.
     *
     * @param program String representation of the program.
     */
    public static List<Ast> parseString(String program) {
        List<Ast> asts = new ArrayList<>();
        Ast.parseString(program, asts::add, null, 0);
        return asts;
    }

    /**
     * Parse the given program and return a list of abstract syntax trees for each statement.
     *
     * @param program      String representation of the program.
     * @param logger       Function to intercept messages normally printed to standard error.
     * @param messageLimit The maximum number of messages passed to the logger.
     */
    public static List<Ast> parseString(String program, LoggerCallback logger, int messageLimit) {
        List<Ast> asts = new ArrayList<>();
        Ast.parseString(program, asts::add, logger, messageLimit);
        return asts;
    }

    /**
     * Parse the given program and return an abstract syntax tree for each
     * statement via a callback.
     *
     * @param program  String representation of the program.
     * @param callback Callable taking an ast as argument.
     */
    public static void parseString(String program, AstCallback callback) {
        Ast.parseString(program, callback, null, 0);
    }

    /**
     * Parse the given program and return an abstract syntax tree for each
     * statement via a callback.
     *
     * @param program      String representation of the program.
     * @param callback     Callable taking an ast as argument.
     * @param logger       Function to intercept messages normally printed to standard error.
     * @param messageLimit The maximum number of messages passed to the logger.
     */
    public static void parseString(String program, AstCallback callback, LoggerCallback logger, int messageLimit) {
        Clingo.INSTANCE.clingo_ast_parse_string(program, callback, null, logger, null, messageLimit);
    }

    /**
     * Decrement the reference count of an AST node.
     * The node is deleted if the reference count reaches zero.
     */
    public void release() {
        Clingo.INSTANCE.clingo_ast_release(ast);
    }

    /**
     * Equality compare two AST nodes.
     *
     * @param other the right-hand-side AST
     * @return the result of the compariso
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Ast))
            return false;
        return Clingo.INSTANCE.clingo_ast_equal(ast, ((Ast) other).getPointer()) > 0;
    }

    /**
     * Less than compare two AST nodes.
     *
     * @param other the right-hand-side AST
     * @return the result of the compariso
     */
    public boolean isLess(Ast other) {
        return Clingo.INSTANCE.clingo_ast_less_than(ast, other.getPointer()) > 0;
    }

    @Override
    public int compareTo(Ast other) {
        return equals(other) ? 0 : isLess(other) ? -1 : 1;
    }

    public Pointer getPointer() {
        return ast;
    }

    protected static Ast create(Pointer ast) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_get_type(ast, intByReference));
        AstType type = AstType.fromOrdinal(intByReference.getValue());
        return create(ast, type);
    }

    protected static Ast create(Pointer ast, AstType type) {
        switch (type) {
            case ID:
                return new Id(ast);
            case VARIABLE:
                return new Variable(ast);
            case SYMBOLIC_TERM:
                return new SymbolicTerm(ast);
            case UNARY_OPERATION:
                return new UnaryOperation(ast);
            case BINARY_OPERATION:
                return new BinaryOperation(ast);
            case INTERVAL:
                return new Interval(ast);
            case FUNCTION:
                return new Function(ast);
            case POOL:
                return new Pool(ast);
            case CSP_PRODUCT:
                return new CspProduct(ast);
            case CSP_SUM:
                return new CspSum(ast);
            case CSP_GUARD:
                return new CspGuard(ast);
            case BOOLEAN_CONSTANT:
                return new BooleanConstant(ast);
            case SYMBOLIC_ATOM:
                return new SymbolicAtom(ast);
            case COMPARISON:
                return new Comparison(ast);
            case CSP_LITERAL:
                return new CspLiteral(ast);
            case AGGREGATE_GUARD:
                return new AggregateGuard(ast);
            case CONDITIONAL_LITERAL:
                return new ConditionalLiteral(ast);
            case AGGREGATE:
                return new Aggregate(ast);
            case BODY_AGGREGATE_ELEMENT:
                return new BodyAggregateElement(ast);
            case BODY_AGGREGATE:
                return new BodyAggregate(ast);
            case HEAD_AGGREGATE_ELEMENT:
                return new HeadAggregateElement(ast);
            case HEAD_AGGREGATE:
                return new HeadAggregate(ast);
            case DISJUNCTION:
                return new Disjunction(ast);
            case DISJOINT_ELEMENT:
                return new DisjointElement(ast);
            case DISJOINT:
                return new Disjoint(ast);
            case THEORY_SEQUENCE:
                return new TheorySequence(ast);
            case THEORY_FUNCTION:
                return new TheoryFunction(ast);
            case THEORY_UNPARSED_TERM_ELEMENT:
                return new TheoryUnparsedTermElement(ast);
            case THEORY_UNPARSED_TERM:
                return new TheoryUnparsedTerm(ast);
            case THEORY_GUARD:
                return new TheoryGuard(ast);
            case THEORY_ATOM_ELEMENT:
                return new TheoryAtomElement(ast);
            case THEORY_ATOM:
                return new TheoryAtom(ast);
            case LITERAL:
                return new Literal(ast);
            case THEORY_OPERATOR_DEFINITION:
                return new TheoryOperatorDefinition(ast);
            case THEORY_TERM_DEFINITION:
                return new TheoryTermDefinition(ast);
            case THEORY_GUARD_DEFINITION:
                return new TheoryGuardDefinition(ast);
            case THEORY_ATOM_DEFINITION:
                return new TheoryAtomDefinition(ast);
            case RULE:
                return new Rule(ast);
            case DEFINITION:
                return new Definition(ast);
            case SHOW_SIGNATURE:
                return new ShowSignature(ast);
            case SHOW_TERM:
                return new ShowTerm(ast);
            case MINIMIZE:
                return new Minimize(ast);
            case SCRIPT:
                return new Script(ast);
            case PROGRAM:
                return new Program(ast);
            case EXTERNAL:
                return new External(ast);
            case EDGE:
                return new Edge(ast);
            case HEURISTIC:
                return new Heuristic(ast);
            case PROJECT_ATOM:
                return new ProjectAtom(ast);
            case PROJECT_SIGNATURE:
                return new ProjectSignature(ast);
            case DEFINED:
                return new Defined(ast);
            case THEORY_DEFINITION:
                return new TheoryDefinition(ast);
            default:
                throw new IllegalStateException("Unknown AST type: " + type.name());
        }
    }

}
