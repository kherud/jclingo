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
import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.clingo_ast_callback_t;

/**
 * Callback function to intercept AST nodes.
 */
@FunctionalInterface
public interface AstCallback {
    /**
     * Callback function to intercept AST nodes.
     *
     * @param ast the AST, borrowed for the duration of the call. Implementations that keep the node beyond that have to
     *            {@link Ast#retain() retain} it and close it later on.
     */
    void call(Ast ast);

    /**
     * @param callback the callback to bridge, may be {@code null}
     * @param arena    the arena the stub lives in; the callback only runs during the call it is passed to
     * @return a {@code clingo_ast_callback_t}, or {@link MemorySegment#NULL} if there is no callback
     */
    static MemorySegment toNative(AstCallback callback, Arena arena) {
        if (callback == null) {
            return MemorySegment.NULL;
        }
        clingo_ast_callback_t.Function bridge = (ast, data) -> {
            callback.call(Ast.borrow(ast));
            return true;
        };
        return Upcalls.stub(clingo_ast_callback_t.Function.class, bridge, clingo_ast_callback_t.descriptor(), arena,
                ErrorPolicy.synchronous());
    }
}
