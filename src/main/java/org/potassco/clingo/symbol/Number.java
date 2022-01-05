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

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;

public class Number extends Symbol {

    private final int number;

    protected Number(long symbol) {
        super(symbol);
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_number(symbol, intByReference));
        this.number = intByReference.getValue();
    }

    public Number(int number) {
        super(Number.create(number));
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    /**
     * Create a new number resulting from an addition between this object and a constant,
     *
     * @param number the constant to add
     * @return the symbolic result of the addition
     */
    public Number add(int number) {
        return new Number(this.number + number);
    }

    /**
     * Create a new number resulting from an addition between this object and a symbolic constant,
     *
     * @param number the constant to add
     * @return the symbolic result of the addition
     */
    public Number add(Number number) {
        return new Number(this.number + number.number);
    }

    /**
     * Create a new number resulting from a subtraction between this object and a constant,
     *
     * @param number the constant to add
     * @return the symbolic result of the subtraction
     */
    public Number sub(int number) {
        return new Number(this.number - number);
    }

    /**
     * Create a new number resulting from a subtraction between this object and a symbolic constant,
     *
     * @param number the constant to add
     * @return the symbolic result of the subtraction
     */
    public Number sub(Number number) {
        return new Number(this.number - number.number);
    }

    /**
     * Create a new number resulting from a multiplication between this object and a constant,
     *
     * @param number the constant to add
     * @return the symbolic result of the multiplication
     */
    public Number mul(int number) {
        return new Number(this.number * number);
    }

    /**
     * Create a new number resulting from a multiplication between this object and a symbolic constant.
     *
     * @param number the constant to add
     * @return the symbolic result of the multiplication
     */
    public Number mul(Number number) {
        return new Number(this.number * number.number);
    }

    /**
     * Create a new number resulting from a division between this object and a constant.
     * Be aware of rounding with integer divisions.
     *
     * @param number the constant to add
     * @return the symbolic result of the division
     */
    public Number div(int number) {
        return new Number(this.number / number);
    }

    /**
     * Create a new number resulting from a division between this object and a symbolic constant.
     * Be aware of rounding with integer divisions.
     *
     * @param number the constant to add
     * @return the symbolic result of the division
     */
    public Number div(Number number) {
        return new Number(this.number / number.number);
    }

    private static long create(int number) {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_symbol_create_number(number, longByReference);
        return longByReference.getValue();
    }
}
