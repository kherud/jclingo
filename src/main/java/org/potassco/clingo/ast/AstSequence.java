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
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AstSequence {

    private final Pointer ast;
    private final AstAttribute attribute;

    public AstSequence(Pointer ast, AstAttribute attribute) {
        this.ast = ast;
        this.attribute = attribute;
    }

    public AstSequence(Pointer ast, AstAttribute attribute, Ast[] elements) {
        this.ast = ast;
        this.attribute = attribute;
        assert size() == 0;
        for (int i = 0; i < elements.length; i++) {
            insert(i, elements[i]);
        }
    }

    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_size_ast_array(ast, attribute.ordinal(), nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }

    public Ast[] get() {
        int size = size();
        Ast[] elements = new Ast[size];
        for (int i = 0; i < size; i++)
            elements[i] = get(i);
        return elements;
    }


    public Ast get(int index) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast_at(ast, attribute.ordinal(), new NativeSize(index), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public void insert(int index, Ast ast) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_insert_ast_at(this.ast, attribute.ordinal(), new NativeSize(index), ast.getPointer()));
    }

    public void set(int index, Ast ast) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast_at(this.ast, attribute.ordinal(), new NativeSize(index), ast.getPointer()));
    }

    public void delete(int index) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_delete_ast_at(ast, attribute.ordinal(), new NativeSize(index)));
    }

    public void set(AstSequence sequence) {
        Ast[] insertions = sequence.get();
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
        return "[" + Arrays.stream(get()).map(Ast::toString).collect(Collectors.joining(", ")) + "]";
    }

    public Pointer[] getPointer() {
        int size = size();
        Pointer[] pointers = new Pointer[size];
        PointerByReference pointerByReference = new PointerByReference();
        for (int i = 0; i < size; i++) {
            Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast_at(ast, attribute.ordinal(), new NativeSize(i), pointerByReference));
            pointers[i] = pointerByReference.getValue();
        }
        return pointers;
    }
}
