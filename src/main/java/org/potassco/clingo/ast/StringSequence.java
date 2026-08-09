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

import java.lang.foreign.Arena;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

/**
 * A sequence holding strings.
 */
public class StringSequence {

    private final Ast owner;
    private final AstAttribute attribute;

    public StringSequence(Ast owner, AstAttribute attribute) {
        this.owner = owner;
        this.attribute = attribute;
    }

    public int size() {
        return (int) Out.getLong(out ->
                ClingoH.clingo_ast_attribute_size_string_array(owner.getPointer(), attribute.getValue(), out));
    }

    public String[] get() {
        int size = size();
        String[] elements = new String[size];
        for (int i = 0; i < size; i++)
            elements[i] = get(i);
        return elements;
    }


    public String get(int index) {
        return Out.getString(out ->
                ClingoH.clingo_ast_attribute_get_string_at(owner.getPointer(), attribute.getValue(), index, out));
    }

    public void insert(int index, String string) {
        try (Arena call = Arena.ofConfined()) {
            Clingo.check(ClingoH.clingo_ast_attribute_insert_string_at(owner.getPointer(), attribute.getValue(),
                    index, call.allocateFrom(string)));
        }
    }

    public void set(int index, String string) {
        try (Arena call = Arena.ofConfined()) {
            Clingo.check(ClingoH.clingo_ast_attribute_set_string_at(owner.getPointer(), attribute.getValue(),
                    index, call.allocateFrom(string)));
        }
    }

    public void delete(int index) {
        Clingo.check(ClingoH.clingo_ast_attribute_delete_string_at(owner.getPointer(), attribute.getValue(), index));
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
