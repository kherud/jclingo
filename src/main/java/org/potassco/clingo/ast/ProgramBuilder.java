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
import org.potassco.clingo.control.Control;

/**
 * Object to build non-ground programs.
 * <p>
 * A <code>ProgramBuilder</code> is an AutoCloseable and should be used with Java's <code>try with</code> statement.
 * Alternatively it must be manually closed.
 */
public class ProgramBuilder implements AutoCloseable {

    private final Pointer builder;

    /**
     * @param control The {@link Control} object to attach the builder to.
     */
    public ProgramBuilder(Control control) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_program_builder_init(control.getPointer(), pointerByReference));
        this.builder = pointerByReference.getValue();
        Clingo.check(Clingo.INSTANCE.clingo_program_builder_begin(builder));
    }

    /**
     * End building a program.
     */
    @Override
    public void close() {
        Clingo.check(Clingo.INSTANCE.clingo_program_builder_end(builder));
    }

    /**
     * Adds a statement in form of an {@link Ast} node to the program.
     * @param ast The statement to add.
     */
    public void add(Ast ast) {
        Clingo.check(Clingo.INSTANCE.clingo_program_builder_add(builder, ast.getPointer()));
    }
}
