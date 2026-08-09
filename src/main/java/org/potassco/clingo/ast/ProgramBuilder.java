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

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.control.Control;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeResource;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

/**
 * Object to build non-ground programs.
 * <p>
 * A <code>ProgramBuilder</code> is an AutoCloseable and should be used with Java's <code>try with</code> statement.
 * Alternatively it must be manually closed.
 */
public class ProgramBuilder extends NativeResource {

    /**
     * @param control The {@link Control} object to attach the builder to.
     */
    public ProgramBuilder(Control control) {
        super(init(control), null);
        Clingo.check(ClingoH.clingo_program_builder_begin(handle));
    }

    private static MemorySegment init(Control control) {
        return Out.getPointer(out -> ClingoH.clingo_program_builder_init(control.getPointer(), out));
    }

    /**
     * End building a program. Repeated calls have no effect.
     */
    @Override
    protected void free() {
        Clingo.check(ClingoH.clingo_program_builder_end(handle));
    }

    /**
     * Adds a statement in form of an {@link Ast} node to the program.
     * @param ast The statement to add.
     */
    public void add(Ast ast) {
        checkOpen();
        Clingo.check(ClingoH.clingo_program_builder_add(handle, ast.getPointer()));
    }
}
