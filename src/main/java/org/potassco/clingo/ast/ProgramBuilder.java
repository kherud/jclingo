package org.potassco.clingo.ast;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.control.Control;

/**
 * Object to build non-ground programs.
 * <p>
 * A `ProgramBuilder` is an AutoCloseable and should be used with Java's `try with` statement.
 * Alternatively it must be manually closed.
 */
public class ProgramBuilder implements AutoCloseable {

    private final Pointer builder;

    /**
     * @param control The {@link Control} object to attach the builder to.
     */
    public ProgramBuilder(Control control) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_control_program_builder(control.getPointer(), pointerByReference));
        this.builder = pointerByReference.getValue();
        Clingo.check(Clingo.INSTANCE.clingo_program_builder_begin(builder));
    }

    /**
     * End building a program.
     */
    @Override
    public void close() {
        Clingo.check(Clingo.INSTANCE.clingo_program_builder_end(builder));
    }

    /**
     * Adds a statement in form of an `AST` node to the program.
     * @param ast The statement to add.
     */
    public void add(Ast ast) {
        Clingo.check(Clingo.INSTANCE.clingo_program_builder_add(builder, ast.getPointer()));
    }
}
