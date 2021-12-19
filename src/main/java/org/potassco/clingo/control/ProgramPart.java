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

package org.potassco.clingo.control;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.symbol.Symbol;

@Structure.FieldOrder({"name", "params", "size"})
public class ProgramPart extends Structure {
    public String name;
    public long[] params;
    public NativeSize size;

    public ProgramPart(String name, Symbol... params) {
        this.name = name;
        // TODO: why does this only work with `1 +` is JNA `Arrays of length zero not allowed` related?
        this.params = new long[1 + params.length];
        for (int i = 0; i < params.length; i++) {
            this.params[i] = params[i].getLong();
        }
        this.size = new NativeSize(params.length);
    }

    public ProgramPart(Pointer pointer) {
        super(pointer);
        read();
    }

}
