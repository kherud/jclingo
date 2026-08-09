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

package org.potassco.clingo.internal;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

/**
 * Hands out slices of one buffer that is reused from call to call, so that a long series of calls passing arrays to
 * clingo does not allocate per call.
 * <p>
 * {@link #reset()} makes the whole buffer available again and must only be called once nothing handed out before is
 * still in use, which for clingo's arguments means after the call using them has returned. Not thread safe - like the
 * confined arena it allocates from.
 */
public final class ScratchAllocator implements SegmentAllocator {

    private final Arena arena;

    private MemorySegment buffer;
    private long offset;
    private long required;

    public ScratchAllocator(Arena arena, long initialSize) {
        this.arena = arena;
        this.buffer = arena.allocate(initialSize);
    }

    /**
     * Makes the buffer available again, growing it if the last round did not fit.
     */
    public void reset() {
        offset = 0;
        if (required > buffer.byteSize()) {
            buffer = arena.allocate(Math.max(required, 2 * buffer.byteSize()));
        }
        required = 0;
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        long start = (offset + byteAlignment - 1) & -byteAlignment;
        required = start + byteSize;
        if (required > buffer.byteSize()) {
            // does not fit; serve this one from the arena and grow the buffer on the next reset
            return arena.allocate(byteSize, byteAlignment);
        }
        offset = required;
        return buffer.asSlice(start, byteSize);
    }
}
