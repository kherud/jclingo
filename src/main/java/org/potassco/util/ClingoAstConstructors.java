package org.potassco.util;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public class ClingoAstConstructors {

	public static void main(String[] args) {
		NativeLibrary lib = NativeLibrary.getInstance("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll");
		Pointer paddr = lib.getGlobalVariableAddress("g_clingo_ast_constructors");
		Pointer[] a = paddr.getPointerArray(0);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
