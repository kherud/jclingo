package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.ast.enums.Type;

import com.sun.jna.Pointer;

public class AstTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/ast_8c-example.html
	 */
	@Test
	public void test() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
        Pointer builder = BaseClingo.controlProgramBuilder(control);
        LocationSt dataLoc = new LocationSt("<rewrite>", "<rewrite>", 0, 0, 0, 0);
        // initilize atom to add
        long symbol = BaseClingo.symbolCreateId("enable", true);
        Pointer term = BaseClingo.astBuild(Type.SYMBOLIC_TERM.ordinal(), dataLoc, symbol);
        Pointer atom = BaseClingo.astBuild(Type.SYMBOLIC_TERM.ordinal(), term);

        // begin building a program
        BaseClingo.programBuilderBegin(builder);
        // get the AST of the program
        AstCallback onStatement;
        OnStatementDataSt onStatementData = new OnStatementDataSt(null, null, null);
//		BaseClingo.astParseString("a :- not b. b :- not a.", onStatement, onStatementData, null, null, 20);
        // finish building a program
		BaseClingo.programBuilderEnd(builder);
        // add the external statement: #external enable.
		BaseClingo.controlAdd(control, name, null, "#external enable.");
        // ground the base part
//        if (!clingo_control_ground(ctl, parts, 1, NULL, NULL)) { goto error; }
        // get the program literal coresponding to the external atom
//        if (!clingo_control_symbolic_atoms(ctl, &atoms)) { goto error; }
//        if (!clingo_symbolic_atoms_find(atoms, sym, &atm_it)) { goto error; }
//        if (!clingo_symbolic_atoms_literal(atoms, atm_it, &atm)) { goto error; }
        // solve with external enable = false
	}

}
