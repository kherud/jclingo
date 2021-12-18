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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;
import org.potassco.clingo.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a node in the abstract syntax tree.
 *
 * The attributes of an `AST` are tied to its type.
 *
 * Furthermore, AST nodes implement comparison operators and are
 * ordered structurally ignoring the location. Their string representation
 * corresponds to their gringo representation. In fact, the string
 * representation of any AST obtained from `parse_files` and `parse_string`
 * can be parsed again. Note that it is possible to construct ASTs
 * that are not parsable, though.
 */
public class Ast implements Comparable<Ast> {

    private final Pointer ast;

    public Ast(Pointer ast) {
        this.ast = ast;
    }

    public String getString() {
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_string(ast, AttributeType.STRING.getValue(), stringByReference));
        return stringByReference[0];
    }

    public void setString(String string) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_string(ast, AttributeType.STRING.getValue(), string));
    }

    public int getNumber() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, AttributeType.NUMBER.getValue(), intByReference));
        return intByReference.getValue();
    }

    public void setNumber(int number) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, AttributeType.NUMBER.getValue(), number));
    }

    public Symbol getSymbol() {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_symbol(ast, AttributeType.AST.getValue(), longByReference));
        return Symbol.fromLong(longByReference.getValue());
    }

    public void setSymbol(long symbol) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_symbol(ast, AttributeType.AST.getValue(), symbol));
    }

    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, AttributeType.LOCATION.getValue(), locationByReference));
        return locationByReference;
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, AttributeType.LOCATION.getValue(), location));
    }

    public Ast getOptionalAst() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_optional_ast(ast, AttributeType.OPTIONAL_AST.getValue(), pointerByReference));
        if (pointerByReference.getValue() == null)
            throw new NoSuchElementException("there is no optional ast");
        return new Ast(pointerByReference.getValue());
    }

    public void setOptionalAst(Ast ast) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_optional_ast(this.ast, AttributeType.OPTIONAL_AST.getValue(), ast.getPointer()));
    }

    // TODO: implement
    public void setStringArray() {
        throw new IllegalStateException("not yet implemented");
    }

    public Ast getAst() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, AttributeType.AST.getValue(), pointerByReference));
        return new Ast(pointerByReference.getValue());
    }

    // TODO: implement
    public void setAstArray() {
        throw new IllegalStateException("not yet implemented");
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_to_string_size(ast, nativeSizeByReference));
        int stringSize = (int) nativeSizeByReference.getValue();
        byte[] stringBytes = new byte[stringSize];
        Clingo.check(Clingo.INSTANCE.clingo_ast_to_string(ast, stringBytes, new NativeSize(stringSize)));
        return Native.toString(stringBytes);
    }

    /**
     * @return Return a shallow copy of the ast.
     */
    public Ast copy() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_copy(ast, pointerByReference));
        return new Ast(pointerByReference.getValue());
    }

//    public Ast update(String key) {
//
//    }
//    TODO: how to implement this?!
//    public List<String> getKeys() {
//        Clingo.INSTANCE.g_clingo_ast_constructors
//    }

    /**
     * @return The type of the node.
     */
    public AstType getType() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_get_type(ast, intByReference));
        return AstType.fromOrdinal(intByReference.getValue());
    }

    /**
     * Unpool the AST returning a list of ASTs without pool terms.
     *
     * @param unpoolType how to unpool
     * @return list of asts
     */
    public List<Ast> unpool(UnpoolType unpoolType) {
        List<Ast> returnValues = new ArrayList<>();
        AstCallback callback = (Ast ast) -> {
            Clingo.INSTANCE.clingo_ast_acquire(ast.getPointer());
            returnValues.add(ast);
        };
        Clingo.check(Clingo.INSTANCE.clingo_ast_unpool(ast, unpoolType.getValue(), callback, null));
        return returnValues;
    }

    /**
     * @return Return a deep copy of the ast.
     */
    public Ast deepCopy() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_deep_copy(ast, pointerByReference));
        return new Ast(pointerByReference.getValue());
    }

    /**
     * Parse the given program and return an abstract syntax tree for each
     * statement via a callback.
     * @param program String representation of the program.
     * @param callback Callable taking an ast as argument.
     */
    public static void parseString(String program, AstCallback callback) {
        Ast.parseString(program, callback, null, 0);
    }

    /**
     * Parse the given program and return an abstract syntax tree for each
     * statement via a callback.
     * @param program String representation of the program.
     * @param callback Callable taking an ast as argument.
     * @param logger Function to intercept messages normally printed to standard error.
     * @param messageLimit The maximum number of messages passed to the logger.
     */
    public static void parseString(String program, AstCallback callback, LoggerCallback logger, int messageLimit) {
        Clingo.INSTANCE.clingo_ast_parse_string(program, callback, null, logger, null, messageLimit);
    }

    /**
     * Decrement the reference count of an AST node.
     * The node is deleted if the reference count reaches zero.
     */
    public void release() {
        Clingo.INSTANCE.clingo_ast_release(ast);
    }

    /**
     * Equality compare two AST nodes.
     * @param other the right-hand-side AST
     * @return the result of the compariso
     */
    public boolean equals(Ast other) {
        return Clingo.INSTANCE.clingo_ast_equal(ast, other.getPointer()) > 0;
    }

    /**
     * Less than compare two AST nodes.
     * @param other the right-hand-side AST
     * @return the result of the compariso
     */
    public boolean isLess(Ast other) {
        return Clingo.INSTANCE.clingo_ast_less_than(ast, other.getPointer()) > 0;
    }

    @Override
    public int compareTo(Ast other) {
        return equals(other) ? 0 : isLess(other) ? -1 : 1;
    }

    public Pointer getPointer() {
        return ast;
    }

}
