package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.jna.ClingoLibrary.AstCallback;
import org.potassco.ast.enums.Type;
import org.potassco.enums.SolveMode;

import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class AstConstructorsTest extends CheckModels {
	@Test
	public void testAstTypes() {
		NativeLibrary lib = NativeLibrary.getInstance("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll");
		Pointer paddr = lib.getGlobalVariableAddress("g_clingo_ast_constructors");
//		int tl = Type.values().length;
//		int o = Type.THEORY_DEFINITION.ordinal();
		AstConstructors ac = new AstConstructors(paddr);
		Pointer p1 = ac.constructors;
		int s = ac.size.intValue();
		AstConstructor ac1 = new AstConstructor(p1);
		AstConstructor[] array = (AstConstructor[]) ac1.toArray(s);
	}

/*
	@Test
	public void testAstTypes2() {
		NativeLibrary lib = NativeLibrary.getInstance("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll");
		Pointer paddr = lib.getGlobalVariableAddress("g_clingo_ast_constructors");
//		int tl = Type.values().length;
//		int o = Type.THEORY_DEFINITION.ordinal();
		AstConstructors ac = new AstConstructors(paddr);
		Pointer p1 = ac.constructors;
		int s1 = ac.size.intValue();
		// Returning an Array of struct
		Structure[] acon = (AstConstructor[]) new AstConstructor().toArray(s1);
//		weiter
		Pointer[] p2 = p1.getPointerArray(0, s1*3);
		AstConstructor[] a = new AstConstructor[s1];
		for (int i = 0; i < a.length; i++) {
			
		}
//		Pointer[] a = paddr.getPointerArray(0, o+1);
		for (int i = 0; i < a.length; i++) {
			Pointer ap = a[i].arguments;
//			int as = a[i].size;
//			SizeByReference sbr = new SizeByReference(as);
//			SizeT s = sbr.getValue();
//			long ai1 = as.getInt(0);
//			long ai2 = as.getNativeLong(0).intValue();
			Pointer[] aaa = ap.getPointerArray(0, 1);
//			Pointer cp = ac.constructors[i];
//			AstConstructor c = new AstConstructor(cp); 
//			a[i].get
//			int j = a[i].getInt(0);
//			byte b = a[i].getByte(0);
//			long l = a[i].getLong(0);
			System.out.println("");
		}
	}
*/
}
