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

import static org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_solve_result.clingo_solve_result_exhausted;
import static org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_solve_result.clingo_solve_result_interrupted;
import static org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_solve_result.clingo_solve_result_satisfiable;
import static org.lorislab.clingo4j.api.c.ClingoLibrary.clingo_solve_result.clingo_solve_result_unsatisfiable;

/**
 *
 * @author Andrej Petras
 */
public class SolveResult {
    
    private final int value;
    
    public SolveResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public boolean isExhausted() {
        return (value & clingo_solve_result_exhausted.value) != 0;
    }

    public boolean isInterrupted() {
        return (value & clingo_solve_result_interrupted.value) != 0;
    }

    public boolean isSatisfiable() {
        return (value & clingo_solve_result_satisfiable.value) > 0;
    }

    public boolean isUnknown() {
        return (value & 3) == 0;
    }

    public boolean isUnsatisfiable() {
        return (value & clingo_solve_result_unsatisfiable.value) != 0;
    }
    
    public boolean isEqual(SolveResult r) {
        return value == r.value;
    }
    
    public boolean isNotEqual(SolveResult r) {
        return value != r.value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isSatisfiable()) {
            sb.append("SATISFIABLE");
            if (isExhausted()) {
                sb.append('+');
            }
        } else if (isUnsatisfiable()) {
            sb.append("UNSATISFIABLE");
        } else {
            sb.append("UNKNOWN");
        }
        if (isInterrupted()) {
            sb.append("/INTERRUPTED");
        }
        return sb.toString();
    }
 
}
