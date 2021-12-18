package org.potassco.clingo;

import org.potassco.clingo.solving.ModelType;
import org.potassco.clingo.symbol.Symbol;

import java.util.List;

public class AnswerSet {

    private final List<Symbol> symbols;
    private final int[] cost;
    private final ModelType type;

    public AnswerSet(List<Symbol> symbols, ModelType type, int[] cost) {
        this.symbols = symbols;
        this.type = type;
        this.cost = cost;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public int[] getCost() {
        return cost;
    }

    public ModelType getType() {
        return type;
    }
}
