package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import org.potassco.ast.enums.Type;
import org.potassco.ast.structs.Ast;
import org.potassco.jna.AstTest.OnStatementData;

public class AstTest {

	private class Ast {
		// TODO
	}

	private class OnStatementData extends Structure {
		public Location loc;
		public Ast atom;
		public Pointer builder;

		public OnStatementData(Location loc, Ast atom, Pointer builder) {
			super();
			this.loc = loc;
			this.atom = atom;
			this.builder = builder;
		}

		protected List<String> getFieldOrder() {
			return Arrays.asList("loc", "atom", "builder");
		}

	}

	/**
	 * https://potassco.org/clingo/c-api/5.5/ast_8c-example.html
	 */
	@Test
	public void test() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, 0L);
        Pointer builder = BaseClingo.controlProgramBuilder(control);
        Location dataLoc = new Location("<rewrite>", "<rewrite>", 0, 0, 0, 0);
        // initilize atom to add
        long symbol = BaseClingo.symbolCreateId("enable", true);
        Pointer term = BaseClingo.astBuild(Type.SYMBOLIC_TERM.ordinal(), dataLoc, symbol);
        Pointer atom = BaseClingo.astBuild(Type.SYMBOLIC_TERM.ordinal(), term);

        // begin building a program
        BaseClingo.programBuilderBegin(builder);
        // get the AST of the program
        AstCallback onStatement;
        OnStatementData onStatementData = new OnStatementData(null, null, null);
		BaseClingo.astParseString("a :- not b. b :- not a.", onStatement, onStatementData, null, null, 20);
        // finish building a program
		BaseClingo.programBuilderEnd(builder);
        // add the external statement: #external enable.
		BaseClingo.controlAdd(control, name, null, "#external enable.");
        // ground the base part
        if (!clingo_control_ground(ctl, parts, 1, NULL, NULL)) { goto error; }
        // get the program literal coresponding to the external atom
        if (!clingo_control_symbolic_atoms(ctl, &atoms)) { goto error; }
        if (!clingo_symbolic_atoms_find(atoms, sym, &atm_it)) { goto error; }
        if (!clingo_symbolic_atoms_literal(atoms, atm_it, &atm)) { goto error; }
        // solve with external enable = false
	}

}
