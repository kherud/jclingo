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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

/**
 * Represents a predicate signature.
 * <p>
 * Signatures have a name and an arity, and can be positive or negative (to
 * represent classical negation).
 */
public class Signature implements Comparable<Signature> {

    static {
        Clingo.init();
    }

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

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            this.signature = Out.getLong(out -> ClingoH.clingo_signature_create(name$, arity, positive, out));
        }
    }

    /**
     * Create a new signature from Clingo's internal representation.
     * You will probably want to call Signature/3.
     *
     * @param signature long signature
     */
    public Signature(long signature) {
        this.name = NativeStr.toJava(ClingoH.clingo_signature_name(signature));
        this.arity = ClingoH.clingo_signature_arity(signature);
        this.positive = ClingoH.clingo_signature_is_positive(signature);
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
        return ClingoH.clingo_signature_is_negative(signature);
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
        return ClingoH.clingo_signature_is_equal_to(signature, ((Signature) other).getLong());
    }

    /**
     * Compute a native 64-bit hash for a signature.
     * Note that {@link #hashCode()} re-uses this method, but returns a 32-bit value.
     *
     * @return the resulting hash code
     */
    public long getHash() {
        return ClingoH.clingo_signature_hash(signature);
    }

    /**
     * Calculates a Java hash for this object.
     * Note, that this method relies on the native hash (see {@link #getHash()}), but consists of 32-bit only.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(getHash());
    }

    /**
     * Check if a signature is less than another signature.
     * Signatures are compared first by sign (int &lt; signed), then by arity, then by name.
     *
     * @param other second signature
     * @return whether a &lt; b
     */
    public boolean isLess(Signature other) {
        return ClingoH.clingo_signature_is_less_than(signature, other.getLong());
    }

    @Override
    public int compareTo(Signature other) {
        return equals(other) ? 0 : isLess(other) ? -1 : 1;
    }

    public long getLong() {
        return signature;
    }
}
