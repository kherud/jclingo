package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.potassco.jna.ClingoLibrary.AstCallback;
import org.potassco.ast.enums.Type;
import org.potassco.enums.SolveMode;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class AstConstructorsTest extends CheckModels {
	public class AstConstructors extends Structure {
		public Pointer constructors;
		public SizeT size;

		public AstConstructors(Pointer p) {
			super(p);
			read();
		}

		protected List<String> getFieldOrder() {
			return Arrays.asList("constructors", "size");
		}

	}

	public class AstConstructor extends Structure {
		public String name;
		public Pointer arguments;
		public Pointer size;

		public AstConstructor() {
			super();
		}

		public AstConstructor(Pointer p) {
			super(p);
			read();
		}

		public AstConstructor(Pointer p1, Pointer p2, Pointer p3) {
			this.name = p1.getString(0);
			this.arguments = p2;
			this.size = p3;
		}

		protected List<String> getFieldOrder() {
			return Arrays.asList("name", "arguments", "size");
		}

	}

	public class AstArgument extends Structure {
		public int attribute;
		public int type;

		public AstArgument() {
			super();
		}

		public AstArgument(Pointer p) {
			super(p);
			read();
		}

		protected List<String> getFieldOrder() {
			return Arrays.asList("attribute", "type");
		}

	}

	@Test
	public void testAstTypes() {
		NativeLibrary lib = NativeLibrary.getInstance("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll");
		Pointer paddr = lib.getGlobalVariableAddress("g_clingo_ast_constructors");
//		int tl = Type.values().length;
//		int o = Type.THEORY_DEFINITION.ordinal();
		AstConstructors ac = new AstConstructors(paddr);
		Pointer p1 = ac.constructors;
		int s1 = ac.size.intValue();
		Pointer[] p2 = p1.getPointerArray(0, s1*3);
		AstConstructor[] a = new AstConstructor[s1];
		int j = 0;
		for (int i = 0; i < s1; i++) {
			a[i] = new AstConstructor(p2[j], p2[j++], p2[j++]);
			j++;
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

}
