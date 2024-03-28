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

package org.potassco.clingo.backend;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * A Literal with an associated weight.
 */
public class WeightedLiteral extends Structure {
    public int literal;
    public int weight;

    public WeightedLiteral() {

    }

    public WeightedLiteral(Pointer pointer) {
        super(pointer);
        read();
    }

    public WeightedLiteral(int literal, int weight) {
        super();
        this.literal = literal;
        this.weight = weight;
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("literal", "weight");
    }

}
