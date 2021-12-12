package org.potassco.clingo.ast;

import com.sun.jna.Pointer;
import org.potassco.clingo.Clingo;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

/**
 * A sequence holding strings.
 */
public class StringSequence implements ErrorChecking {

    private final Pointer ast;
    private final AttributeType attributeType = AttributeType.STRING_ARRAY;

    public StringSequence(Pointer ast) {
        this.ast = ast;
    }

    /**
     * Get the value at the given index.
     * @param index the target index
     * @return the resulting value
     */
    public String get(int index) {
        int size = size();
        if (index < 0)
            index += size;
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        String[] stringByReference = new String[1];
        checkError(Clingo.INSTANCE.clingo_ast_attribute_get_string_at(ast, attributeType.getValue(), new NativeSize(index), stringByReference));
        return stringByReference[0];
    }

    /**
     * Get the size of the array
     * @return the resulting size
     */
    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        checkError(Clingo.INSTANCE.clingo_ast_attribute_size_string_array(ast, attributeType.getValue(), nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }
}
