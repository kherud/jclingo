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

public class AstSequence {

    private final Pointer ast;
    private final Attribute attribute;

    public AstSequence(Pointer ast, Attribute attribute) {
        this.ast = ast;
        this.attribute = attribute;
    }

    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.INSTANCE.clingo_ast_attribute_size_ast_array(ast, attribute.ordinal(), nativeSizeByReference);
        return (int) nativeSizeByReference.getValue();
    }

    public Ast get(int index) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.INSTANCE.clingo_ast_attribute_get_ast_at(ast, attribute.ordinal(), new NativeSize(index), pointerByReference);
        return Ast.create(pointerByReference.getValue());
    }

    public void set(AstSequence astSequence) {

    }

    public Pointer[] getPointer() {
        int size = size();
        Pointer[] pointers = new Pointer[size];
        PointerByReference pointerByReference = new PointerByReference();
        for (int i = 0; i < size; i++) {
            Clingo.INSTANCE.clingo_ast_attribute_get_ast_at(ast, attribute.ordinal(), new NativeSize(i), pointerByReference);
            pointers[i] = pointerByReference.getValue();
        }
        return pointers;
    }
}
