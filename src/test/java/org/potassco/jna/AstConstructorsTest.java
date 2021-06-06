package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.jna.ClingoLibrary.AstCallback;
import org.potassco.ast.enums.Type;
import org.potassco.cpp.clingo_h;
import org.potassco.enums.SolveMode;

import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class AstConstructorsTest extends CheckModels {

	/** {@link clingo_h#g_clingo_ast_constructors} */
	@Test
	public void testAstTypes() {
		NativeLibrary lib = NativeLibrary.getInstance("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll");
		Pointer paddr = lib.getGlobalVariableAddress("g_clingo_ast_constructors");
		AstConstructors astContstructors = new AstConstructors(paddr);
		AstConstructor ac1 = new AstConstructor(astContstructors.constructors);
		AstConstructor[] array = (AstConstructor[]) ac1.toArray(astContstructors.size.intValue());
		for (AstConstructor ac : array) {
			System.out.println(ac.name);
			AstArgument aa = new AstArgument(ac.arguments);
			AstArgument[] aaArray = (AstArgument[]) aa.toArray(ac.size());
			for (AstArgument aarg : aaArray) {
				System.out.println("  "
						+ aarg.attribute + "="
						+ org.potassco.ast.enums.Attribute.fromOrdinal(aarg.attribute)
						+ " " + aarg.type + "="
						+ org.potassco.ast.enums.AttributeType.fromValue(aarg.type));
			}
		}
	}

}
