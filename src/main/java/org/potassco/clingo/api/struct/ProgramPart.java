package org.potassco.clingo.api.struct;

import com.sun.jna.Structure;
import org.potassco.clingo.api.types.NativeSize;

import java.util.Arrays;
import java.util.List;

public class ProgramPart extends Structure {
    public String name;
    public long[] params;
    public NativeSize size;

    public ProgramPart(String name, long... params) {
        this.name = name;
        this.params = params.length == 0 ? new long[1] : params;
        this.size = new NativeSize(params.length);
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("name", "params", "size");
    }
}
