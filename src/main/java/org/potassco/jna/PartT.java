package org.potassco.jna;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class PartT extends Structure {
    public String name;
    public Pointer params;
    public SizeT size;
    protected List<String> getFieldOrder() {
        return Arrays.asList("name", "params", "size");
    }
}