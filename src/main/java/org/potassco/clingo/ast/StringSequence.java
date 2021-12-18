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

import com.sun.jna.Pointer;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

/**
 * A sequence holding strings.
 */
public class StringSequence {

    private final Pointer ast;
    private final AttributeType attributeType = AttributeType.STRING_ARRAY;

    public StringSequence(Pointer ast) {
        this.ast = ast;
    }

    /**
     * Get the value at the given index.
     * @param index the target index
     * @return the resulting value
     */
    public String get(int index) {
        int size = size();
        if (index < 0)
            index += size;
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_string_at(ast, attributeType.getValue(), new NativeSize(index), stringByReference));
        return stringByReference[0];
    }

    /**
     * Get the size of the array
     * @return the resulting size
     */
    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_size_string_array(ast, attributeType.getValue(), nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }
}
