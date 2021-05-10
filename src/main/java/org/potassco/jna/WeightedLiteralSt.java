package org.potassco.jna;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Structure;

/**
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_weighted_literal_t}
 */
public class WeightedLiteralSt extends Structure {
    private LiteralSt literal;
    private WeightSt weight;

}
