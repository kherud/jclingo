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
import java.lang.foreign.SegmentAllocator;

import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.ffi.clingo_location;

/**
 * Represents a source code location marking its beginning and end.
 * <p>
 * Not all locations refer to physical files. By convention, such locations use a name put in angular brackets as
 * filename.
 *
 * @author Josef Schneeberger
 */
public record Location(String beginFile, String endFile, long beginLine, long endLine, long beginColumn,
                       long endColumn) {

    /**
     * Reads a location clingo handed out. The strings it points at are internalized by clingo, so the returned
     * location stays valid after the segment is gone.
     */
    public static Location fromNative(MemorySegment location) {
        return new Location(
                NativeStr.toJava(clingo_location.begin_file(location)),
                NativeStr.toJava(clingo_location.end_file(location)),
                clingo_location.begin_line(location),
                clingo_location.end_line(location),
                clingo_location.begin_column(location),
                clingo_location.end_column(location));
    }

    /**
     * @return this location as a {@code clingo_location}, allocated by {@code allocator}
     */
    public MemorySegment toNative(SegmentAllocator allocator) {
        MemorySegment location = clingo_location.allocate(allocator);
        clingo_location.begin_file(location, NativeStr.of(allocator, beginFile));
        clingo_location.end_file(location, NativeStr.of(allocator, endFile));
        clingo_location.begin_line(location, beginLine);
        clingo_location.end_line(location, endLine);
        clingo_location.begin_column(location, beginColumn);
        clingo_location.end_column(location, endColumn);
        return location;
    }

    @Override
    public String toString() {
        return beginFile + ":" + beginLine + ":" + beginColumn
               + "-" + (beginFile.equals(endFile) ? "" : endFile + ":") + endLine + ":" + endColumn;
    }
}
