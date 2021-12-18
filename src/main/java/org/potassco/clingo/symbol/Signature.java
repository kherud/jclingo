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
 
package org.potassco.clingo.symbol;

import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;

/**
 * Represents a predicate signature.
 *
 * Signatures have a name and an arity, and can be positive or negative (to
 * represent classical negation).
 */
public class Signature implements Comparable<Signature> {

    private final String name;
    private final int arity;
    private final boolean positive;
    private final long signature;

    /**
     * Create a new signature. Can throw clingo_error_bad_alloc.
     *
     * @param name name of the signature
     * @param arity arity of the signature
     */
    public Signature(String name, int arity) {
        this(name, arity, true);
    }

    /**
     * Create a new signature. Can throw clingo_error_bad_alloc.
     *
     * @param name name of the signature
     * @param arity arity of the signature
     * @param positive false if the signature has a classical negation sign
     */
    public Signature(String name, int arity, boolean positive) {
        this.name = name;
        this.arity = arity;
        this.positive = positive;

        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_signature_create(name, arity, positive ? (byte) 1 : 0, longByReference));
        this.signature = longByReference.getValue();
    }

    /**
     * Create a new signature from Clingo's internal representation.
     * You will probably want to call Signature/3.
     *
     * @param signature long signature
     */
    public Signature(long signature) {
        this.name = Clingo.INSTANCE.clingo_signature_name(signature);
        this.arity = Clingo.INSTANCE.clingo_signature_arity(signature);
        this.positive = Clingo.INSTANCE.clingo_signature_is_positive(signature) > 0;
        this.signature = signature;
    }

    /**
     * @return Get the name of a signature.
     */
    public String getName() {
        return name;
    }

    /**
     * @return get the arity of a signature.
     */
    public int getArity() {
        return arity;
    }

    /**
     * @return Whether the signature is positive (is not classically negated).
     */
    public boolean isPositive() {
        return positive;
    }

    @Override
    public String toString(){
        return (positive ? "" : "-") + name + "/" + arity;
    }

    /**
     * @return Whether the signature is negative (is classically negated).
     */
    public boolean isNegative() {
        return Clingo.INSTANCE.clingo_signature_is_negative(signature) > 0;
    }

    /**
     * Check if two signatures are equal.
     * @param other second signature
     * @return whether a == b
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Signature))
            return false;
        return Clingo.INSTANCE.clingo_signature_is_equal_to(signature, ((Signature) other).getLong()) > 0;
    }

    /**
     * Check if a signature is less than another signature.
     * Signatures are compared first by sign (int < signed), then by arity, then by name.
     *
     * @param other second signature
     * @return whether a < b
     */
    public boolean isLess(Signature other) {
        return Clingo.INSTANCE.clingo_signature_is_less_than(signature, other.getLong()) > 0;
    }

    @Override
    public int compareTo(Signature other) {
        return equals(other) ? 0 : isLess(other) ? -1 : 1;
    }

    public long getLong() {
        return signature;
    }
}
