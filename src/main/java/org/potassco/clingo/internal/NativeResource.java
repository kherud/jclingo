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

/**
 * A clingo handle that has to be released explicitly.
 * <p>
 * Closing is idempotent and always happens in the same order: the native handle is freed first, only then the arena
 * holding the memory clingo was given - the other way round a solver thread could still be running in an upcall stub
 * whose memory just went away.
 */
public abstract class NativeResource implements AutoCloseable {

    protected final MemorySegment handle;

    /**
     * Holds everything clingo may access for as long as this resource lives, e.g. upcall stubs and callback structs.
     */
    protected final Arena arena;

    private final LeakDetector.Registration leak;
    private boolean closed;

    protected NativeResource(MemorySegment handle, Arena arena) {
        this.handle = handle;
        this.arena = arena;
        this.leak = LeakDetector.register(this, getClass().getSimpleName());
    }

    /**
     * @return the native handle this object wraps, useful for interoperating with other clingo bindings
     */
    public MemorySegment getPointer() {
        return handle;
    }

    /**
     * Releases the native handle. Called at most once, before the arena is closed.
     */
    protected abstract void free();

    /**
     * @return whether this resource has been closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @throws IllegalStateException if this resource has already been closed
     */
    protected void checkOpen() {
        if (closed) {
            throw new IllegalStateException(getClass().getSimpleName() + " has already been closed");
        }
    }

    @Override
    public final void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            free();
        }
        finally {
            if (arena != null) {
                arena.close();
            }
            leak.close();
        }
    }
}
