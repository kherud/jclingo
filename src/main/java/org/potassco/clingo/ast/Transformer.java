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

import org.potassco.clingo.ast.nodes.Aggregate;
import org.potassco.clingo.ast.nodes.AggregateGuard;
import org.potassco.clingo.ast.nodes.BinaryOperation;
import org.potassco.clingo.ast.nodes.BodyAggregate;
import org.potassco.clingo.ast.nodes.BodyAggregateElement;
import org.potassco.clingo.ast.nodes.BooleanConstant;
import org.potassco.clingo.ast.nodes.Comparison;
import org.potassco.clingo.ast.nodes.ConditionalLiteral;
import org.potassco.clingo.ast.nodes.Defined;
import org.potassco.clingo.ast.nodes.Definition;
import org.potassco.clingo.ast.nodes.Disjunction;
import org.potassco.clingo.ast.nodes.Edge;
import org.potassco.clingo.ast.nodes.External;
import org.potassco.clingo.ast.nodes.Function;
import org.potassco.clingo.ast.nodes.HeadAggregate;
import org.potassco.clingo.ast.nodes.HeadAggregateElement;
import org.potassco.clingo.ast.nodes.Heuristic;
import org.potassco.clingo.ast.nodes.Id;
import org.potassco.clingo.ast.nodes.Interval;
import org.potassco.clingo.ast.nodes.Literal;
import org.potassco.clingo.ast.nodes.Minimize;
import org.potassco.clingo.ast.nodes.Pool;
import org.potassco.clingo.ast.nodes.Program;
import org.potassco.clingo.ast.nodes.ProjectAtom;
import org.potassco.clingo.ast.nodes.ProjectSignature;
import org.potassco.clingo.ast.nodes.Rule;
import org.potassco.clingo.ast.nodes.Script;
import org.potassco.clingo.ast.nodes.ShowSignature;
import org.potassco.clingo.ast.nodes.ShowTerm;
import org.potassco.clingo.ast.nodes.SymbolicAtom;
import org.potassco.clingo.ast.nodes.SymbolicTerm;
import org.potassco.clingo.ast.nodes.TheoryAtom;
import org.potassco.clingo.ast.nodes.TheoryAtomDefinition;
import org.potassco.clingo.ast.nodes.TheoryAtomElement;
import org.potassco.clingo.ast.nodes.TheoryDefinition;
import org.potassco.clingo.ast.nodes.TheoryFunction;
import org.potassco.clingo.ast.nodes.TheoryGuard;
import org.potassco.clingo.ast.nodes.TheoryGuardDefinition;
import org.potassco.clingo.ast.nodes.TheoryOperatorDefinition;
import org.potassco.clingo.ast.nodes.TheorySequence;
import org.potassco.clingo.ast.nodes.TheoryTermDefinition;
import org.potassco.clingo.ast.nodes.TheoryUnparsedTerm;
import org.potassco.clingo.ast.nodes.TheoryUnparsedTermElement;
import org.potassco.clingo.ast.nodes.UnaryOperation;
import org.potassco.clingo.ast.nodes.Variable;

/**
 * Utility class to transform ASTs.
 * <p>
 * Classes should inherit from this class and implement functions with name
 * <code>visit&lt;AstType&gt;</code> where <code>&lt;AstType&gt;</code> is the type of the ASTs to visit and
 * modify. Such a function should return an updated AST or the same AST if no
 * change is necessary. The transformer will take care to copy all parent ASTs
 * involving a modified child. Note that the class works like a visitor if
 * only self references are returned from such functions.
 * <p>
 * Any extra arguments passed to the visit method are passed on to child ASTs.
 */
public class Transformer {

    public Ast visit(Ast ast) {
        AstType type = ast.getType();
        switch (ast.getType()) {
            case ID: return visit((Id) ast);
            case VARIABLE: return visit((Variable) ast);
            case SYMBOLIC_TERM: return visit((SymbolicTerm) ast);
            case UNARY_OPERATION: return visit((UnaryOperation) ast);
            case BINARY_OPERATION: return visit((BinaryOperation) ast);
            case INTERVAL: return visit((Interval) ast);
            case FUNCTION: return visit((Function) ast);
            case POOL: return visit((Pool) ast);
            case BOOLEAN_CONSTANT: return visit((BooleanConstant) ast);
            case SYMBOLIC_ATOM: return visit((SymbolicAtom) ast);
            case COMPARISON: return visit((Comparison) ast);
            case AGGREGATE_GUARD: return visit((AggregateGuard) ast);
            case CONDITIONAL_LITERAL: return visit((ConditionalLiteral) ast);
            case AGGREGATE: return visit((Aggregate) ast);
            case BODY_AGGREGATE_ELEMENT: return visit((BodyAggregateElement) ast);
            case BODY_AGGREGATE: return visit((BodyAggregate) ast);
            case HEAD_AGGREGATE_ELEMENT: return visit((HeadAggregateElement) ast);
            case HEAD_AGGREGATE: return visit((HeadAggregate) ast);
            case DISJUNCTION: return visit((Disjunction) ast);
            case THEORY_SEQUENCE: return visit((TheorySequence) ast);
            case THEORY_FUNCTION: return visit((TheoryFunction) ast);
            case THEORY_UNPARSED_TERM_ELEMENT: return visit((TheoryUnparsedTermElement) ast);
            case THEORY_UNPARSED_TERM: return visit((TheoryUnparsedTerm) ast);
            case THEORY_GUARD: return visit((TheoryGuard) ast);
            case THEORY_ATOM_ELEMENT: return visit((TheoryAtomElement) ast);
            case THEORY_ATOM: return visit((TheoryAtom) ast);
            case LITERAL: return visit((Literal) ast);
            case THEORY_OPERATOR_DEFINITION: return visit((TheoryOperatorDefinition) ast);
            case THEORY_TERM_DEFINITION: return visit((TheoryTermDefinition) ast);
            case THEORY_GUARD_DEFINITION: return visit((TheoryGuardDefinition) ast);
            case THEORY_ATOM_DEFINITION: return visit((TheoryAtomDefinition) ast);
            case RULE: return visit((Rule) ast);
            case DEFINITION: return visit((Definition) ast);
            case SHOW_SIGNATURE: return visit((ShowSignature) ast);
            case SHOW_TERM: return visit((ShowTerm) ast);
            case MINIMIZE: return visit((Minimize) ast);
            case SCRIPT: return visit((Script) ast);
            case PROGRAM: return visit((Program) ast);
            case EXTERNAL: return visit((External) ast);
            case EDGE: return visit((Edge) ast);
            case HEURISTIC: return visit((Heuristic) ast);
            case PROJECT_ATOM: return visit((ProjectAtom) ast);
            case PROJECT_SIGNATURE: return visit((ProjectSignature) ast);
            case DEFINED: return visit((Defined) ast);
            case THEORY_DEFINITION: return visit((TheoryDefinition) ast);
            default: throw new IllegalStateException("Unknown AST type: " + type.name());
        }
    }

    public Id visit(Id id) {
        return id;
    }

    public AstSequence visit(AstSequence astSequence) {
        int size = astSequence.size();
        for (int i = 0; i < size; i++) {
            astSequence.set(i, visit(astSequence.get(i)));
        }
        return astSequence;
    }

    public Variable visit(Variable variable) {
        return variable;
    }

    public SymbolicTerm visit(SymbolicTerm symbolicTerm) {
        return symbolicTerm;
    }

    public UnaryOperation visit(UnaryOperation unaryOperation) {
        unaryOperation.setArgument(visit(unaryOperation.getArgument()));
        return unaryOperation;
    }

    public BinaryOperation visit(BinaryOperation binaryOperation) {
        binaryOperation.setLeft(visit(binaryOperation.getLeft()));
        binaryOperation.setRight(visit(binaryOperation.getRight()));
        return binaryOperation;
    }

    public Interval visit(Interval interval) {
        interval.setLeft(visit(interval.getLeft()));
        interval.setRight(visit(interval.getRight()));
        return interval;
    }

    public Function visit(Function function) {
        function.setArguments(visit(function.getArguments()));
        return function;
    }

    public Pool visit(Pool pool) {
        pool.setArguments(visit(pool.getArguments()));
        return pool;
    }

    public BooleanConstant visit(BooleanConstant booleanConstant) {
        return booleanConstant;
    }

    public SymbolicAtom visit(SymbolicAtom symbolicAtom) {
        symbolicAtom.setSymbol(visit(symbolicAtom.getSymbol()));
        return symbolicAtom;
    }

    public Comparison visit(Comparison comparison) {
        comparison.setLeft(visit(comparison.getLeft()));
        comparison.setRight(visit(comparison.getRight()));
        return comparison;
    }

    public AggregateGuard visit(AggregateGuard aggregateGuard) {
        aggregateGuard.setTerm(visit(aggregateGuard.getTerm()));
        return aggregateGuard;
    }

    public ConditionalLiteral visit(ConditionalLiteral conditionalLiteral) {
        conditionalLiteral.setLiteral(visit(conditionalLiteral.getLiteral()));
        conditionalLiteral.setCondition(visit(conditionalLiteral.getCondition()));
        return conditionalLiteral;
    }

    public Aggregate visit(Aggregate aggregate) {
        aggregate.setLeftGuard(visit(aggregate.getLeftGuard()));
        aggregate.setElements(visit(aggregate.getElements()));
        aggregate.setRightGuard(visit(aggregate.getRightGuard()));
        return aggregate;
    }

    public BodyAggregateElement visit(BodyAggregateElement bodyAggregateElement) {
        bodyAggregateElement.setTerms(visit(bodyAggregateElement.getTerms()));
        bodyAggregateElement.setCondition(visit(bodyAggregateElement.getCondition()));
        return bodyAggregateElement;
    }

    public BodyAggregate visit(BodyAggregate bodyAggregate) {
        bodyAggregate.setLeftGuard(visit(bodyAggregate.getLeftGuard()));
        bodyAggregate.setElements(visit(bodyAggregate.getElements()));
        bodyAggregate.setRightGuard(visit(bodyAggregate.getRightGuard()));
        return bodyAggregate;
    }

    public HeadAggregateElement visit(HeadAggregateElement headAggregateElement) {
        headAggregateElement.setTerms(visit(headAggregateElement.getTerms()));
        headAggregateElement.setCondition(visit(headAggregateElement.getCondition()));
        return headAggregateElement;
    }

    public HeadAggregate visit(HeadAggregate headAggregate) {
        headAggregate.setLeftGuard(visit(headAggregate.getLeftGuard()));
        headAggregate.setElements(visit(headAggregate.getElements()));
        headAggregate.setRightGuard(visit(headAggregate.getRightGuard()));
        return headAggregate;
    }

    public Disjunction visit(Disjunction disjunction) {
        disjunction.setElements(visit(disjunction.getElements()));
        return disjunction;
    }

    public TheorySequence visit(TheorySequence theorySequence) {
        theorySequence.setTerms(visit(theorySequence.getTerms()));
        return theorySequence;
    }

    public TheoryFunction visit(TheoryFunction theoryFunction) {
        theoryFunction.setArguments(visit(theoryFunction.getArguments()));
        return theoryFunction;
    }

    public TheoryUnparsedTermElement visit(TheoryUnparsedTermElement theoryUnparsedTermElement) {
        theoryUnparsedTermElement.setTerm(visit(theoryUnparsedTermElement.getTerm()));
        return theoryUnparsedTermElement;
    }

    public TheoryUnparsedTerm visit(TheoryUnparsedTerm theoryUnparsedTerm) {
        theoryUnparsedTerm.setElements(visit(theoryUnparsedTerm.getElements()));
        return theoryUnparsedTerm;
    }

    public TheoryGuard visit(TheoryGuard theoryGuard) {
        theoryGuard.setTerm(visit(theoryGuard.getTerm()));
        return theoryGuard;
    }

    public TheoryAtomElement visit(TheoryAtomElement theoryAtomElement) {
        theoryAtomElement.setTerms(visit(theoryAtomElement.getTerms()));
        theoryAtomElement.setCondition(visit(theoryAtomElement.getCondition()));
        return theoryAtomElement;
    }

    public TheoryAtom visit(TheoryAtom theoryAtom) {
        theoryAtom.setTerm(visit(theoryAtom.getTerm()));
        theoryAtom.setElements(visit(theoryAtom.getElements()));
        theoryAtom.setGuard(visit(theoryAtom.getGuard()));
        return theoryAtom;
    }

    public Literal visit(Literal literal) {
        literal.setAtom(visit(literal.getAtom()));
        return literal;
    }

    public TheoryOperatorDefinition visit(TheoryOperatorDefinition theoryOperatorDefinition) {
        return theoryOperatorDefinition;
    }

    public TheoryTermDefinition visit(TheoryTermDefinition theoryTermDefinition) {
        theoryTermDefinition.setOperators(visit(theoryTermDefinition.getOperators()));
        return theoryTermDefinition;
    }

    public TheoryGuardDefinition visit(TheoryGuardDefinition theoryGuardDefinition) {
        return theoryGuardDefinition;
    }

    public TheoryAtomDefinition visit(TheoryAtomDefinition theoryAtomDefinition) {
        theoryAtomDefinition.setGuard(visit(theoryAtomDefinition.getGuard()));
        return theoryAtomDefinition;
    }

    public Rule visit(Rule rule) {
        rule.setHead(visit(rule.getHead()));
        rule.setBody(visit(rule.getBody()));
        return rule;
    }

    public Definition visit(Definition definition) {
        definition.setValue(visit(definition.getValue()));
        return definition;
    }

    public ShowSignature visit(ShowSignature showSignature) {
        return showSignature;
    }

    public ShowTerm visit(ShowTerm showTerm) {
        showTerm.setTerm(visit(showTerm.getTerm()));
        showTerm.setBody(visit(showTerm.getBody()));
        return showTerm;
    }

    public Minimize visit(Minimize minimize) {
        minimize.setWeight(visit(minimize.getWeight()));
        minimize.setPriority(visit(minimize.getPriority()));
        minimize.setTerms(visit(minimize.getTerms()));
        minimize.setBody(visit(minimize.getBody()));
        return minimize;
    }

    public Script visit(Script script) {
        return script;
    }

    public Program visit(Program program) {
        program.setParameters(visit(program.getParameters()));
        return program;
    }

    public External visit(External external) {
        external.setAtom(visit(external.getAtom()));
        external.setBody(visit(external.getBody()));
        external.setExternalType(visit(external.getExternalType()));
        return external;
    }

    public Edge visit(Edge edge) {
        edge.setNodeU(visit(edge.getNodeU()));
        edge.setNodeV(visit(edge.getNodeV()));
        edge.setBody(visit(edge.getBody()));
        return edge;
    }

    public Heuristic visit(Heuristic heuristic) {
        heuristic.setAtom(visit(heuristic.getAtom()));
        heuristic.setBody(visit(heuristic.getBody()));
        heuristic.setBias(visit(heuristic.getBias()));
        heuristic.setPriority(visit(heuristic.getPriority()));
        heuristic.setModifier(visit(heuristic.getModifier()));
        return heuristic;
    }

    public ProjectAtom visit(ProjectAtom projectAtom) {
        projectAtom.setAtom(visit(projectAtom.getAtom()));
        projectAtom.setBody(visit(projectAtom.getBody()));
        return projectAtom;
    }

    public ProjectSignature visit(ProjectSignature projectSignature) {
        return projectSignature;
    }

    public Defined visit(Defined defined) {
        return defined;
    }

    public TheoryDefinition visit(TheoryDefinition theoryDefinition) {
        theoryDefinition.setTerms(visit(theoryDefinition.getTerms()));
        theoryDefinition.setAtoms(visit(theoryDefinition.getAtoms()));
        return theoryDefinition;
    }


}


