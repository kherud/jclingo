package org.potassco.clingo.backend;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * A Literal with an associated weight.
 */
public class WeightedLiteral extends Structure {
    public int literal;
    public int weight;

    public WeightedLiteral() {

    }

    public WeightedLiteral(int literal, int weight) {
        super();
        this.literal = literal;
        this.weight = weight;
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("literal", "weight");
    }

}
