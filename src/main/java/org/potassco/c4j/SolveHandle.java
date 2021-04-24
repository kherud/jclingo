/*
 * Copyright 2017 Andrej Petras.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.potassco.c4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
//import org.bridj.Pointer;
//import static org.lorislab.clingo4j.api.Clingo.LIB;
//import static org.lorislab.clingo4j.api.Clingo.handleError;
//import static org.lorislab.clingo4j.api.Clingo.handleRuntimeError;
//import org.lorislab.clingo4j.api.c.ClingoLibrary;
//import org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_solve_handle;
//import org.lorislab.clingo4j.util.PointerObject;

import org.potassco.ClingoException;

import com.sun.jna.Structure;

/**
 *
 * @author Andrej Petras
 */
public class SolveHandle extends Structure implements Iterable<Model>, Closeable {

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

    @Override
    public Iterator<Model> iterator() {
        Iterator<Model> iter = new Iterator<Model>() {

            private Model current;

            @Override
            public boolean hasNext() {
                try {
                    current = SolveHandle.this.next();
                    // close the solve handle
                    if (current == null) {
                        SolveHandle.this.close();
                    }
                    return current != null;
                } catch (ClingoException ex) {
                    handleRuntimeError(ex);
                }
                return false;
            }

            @Override
            public Model next() {
                if (current == null) {
                    if (!hasNext()) {
                        throw new NoSuchElementException("No more models in the solution.");
                    }
                }
                return current;
            }
        };
        return iter;
    }

    public Model next() throws ClingoException {
        resume();
        return getModel();
    }

    public void resume() throws ClingoException {
        handleError(LIB.clingo_solve_handle_resume(pointer), "Error solve handle resume");
    }

    public Model getModel() throws ClingoException {
        Pointer<Pointer<ClingoLibrary.clingo_model>> model = Pointer.allocatePointer(ClingoLibrary.clingo_model.class);
        handleError(LIB.clingo_solve_handle_model(pointer, model), "Error solve handle model");
        if (model.get() != null) {
            return new Model(model.get());
        }
        return null;
    }

	/*
    public SolveHandle(Pointer<clingo_solve_handle> pointer) {
        super(pointer);
    }

    public boolean waitHandle(double timeout) {
        Pointer<Boolean> ret = Pointer.allocateBoolean();
        LIB.clingo_solve_handle_wait(pointer, timeout, ret);
        return ret.get();
    }

    public boolean waitHandle() {
        return waitHandle(-1);
    }

    @Override
    public void close() {
        if (pointer != null && pointer.getIO() != null) {
            handleRuntimeError(LIB.clingo_solve_handle_close(pointer), "Error close the handle.");
        }
    }

    public SolveResult getSolveResult() throws ClingoException {
        Pointer<Integer> ret = Pointer.allocateInt();
        handleError(LIB.clingo_solve_handle_get(pointer, ret), "Solve handle get solve result!");
        return new SolveResult(ret.getInt());
    }
	*/
}
