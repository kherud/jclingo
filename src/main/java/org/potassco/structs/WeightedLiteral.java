package org.potassco.structs;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Structure;

/**
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_weighted_literal_t}
 */
public class WeightedLiteral extends Structure {
    private Literal literal;
    private Weight weight;

}
