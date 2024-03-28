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
    private final AstAttribute attribute;

    public StringSequence(Pointer ast, AstAttribute attribute) {
        this.ast = ast;
        this.attribute = attribute;
    }

    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_size_string_array(ast, attribute.ordinal(), nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }

    public String[] get() {
        int size = size();
        String[] elements = new String[size];
        for (int i = 0; i < size; i++)
            elements[i] = get(i);
        return elements;
    }


    public String get(int index) {
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_string_at(ast, attribute.ordinal(), new NativeSize(index), stringByReference));
        return stringByReference[0];
    }

    public void insert(int index, String string) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_insert_string_at(this.ast, attribute.ordinal(), new NativeSize(index), string));
    }

    public void set(int index, String string) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_string_at(this.ast, attribute.ordinal(), new NativeSize(index), string));
    }

    public void delete(int index) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_delete_string_at(ast, attribute.ordinal(), new NativeSize(index)));
    }

    public void set(StringSequence sequence) {
        String[] insertions = sequence.get();
        while (size() > 0) {
            delete(0);
        }
        for (int i = 0; i < insertions.length; i++)
            insert(i, insertions[i]);
    }

    public AstAttribute getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return "[" + String.join(", ", get()) + "]";
    }
}
