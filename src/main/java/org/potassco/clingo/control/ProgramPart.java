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
    }

}
