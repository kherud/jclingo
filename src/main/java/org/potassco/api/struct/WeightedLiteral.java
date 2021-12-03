package org.potassco.api.struct;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * A Literal with an associated weight.
 */
public class WeightedLiteral extends Structure {
    private int literal;
    private int weight;

    public WeightedLiteral(int literal, int weight) {
        super();
        this.literal = literal;
        this.weight = weight;
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("literal", "weight");
    }

}
