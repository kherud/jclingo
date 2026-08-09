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

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

/**
 * A boolean an option can be bound to.
 * <p>
 * Before the flag is registered with {@link ApplicationOptions#addFlag} its value lives in this object; afterwards it
 * lives where clingo writes it.
 */
public class Flag {

    private boolean value;
    private MemorySegment target;

    public boolean get() {
        return target == null ? value : target.get(ValueLayout.JAVA_BOOLEAN, 0);
    }

    public void set(boolean value) {
        if (target == null) {
            this.value = value;
        }
        else {
            target.set(ValueLayout.JAVA_BOOLEAN, 0, value);
        }
    }

    /**
     * Moves this flag into memory clingo can write to. The allocator has to outlive the option parsing.
     */
    MemorySegment bind(SegmentAllocator allocator) {
        target = allocator.allocate(ValueLayout.JAVA_BOOLEAN);
        target.set(ValueLayout.JAVA_BOOLEAN, 0, value);
        return target;
    }
}
