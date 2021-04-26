package org.potassco;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.Size;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class SignatureTest {

	@org.junit.Test
	public void test1() {
		Clingo clingo = new Clingo();
		try {
			String name = "test";
			int arity = 2;
			boolean positive = true;
			Pointer signature = clingo.signatureCreate(name, arity, positive);
			assertEquals(name, clingo.signatureName(signature));
			assertEquals(arity, clingo.signatureArity(signature));
			assertEquals(positive, clingo.signatureIsPositive(signature));
			assertEquals(!positive, clingo.signatureIsNegative(signature));
			assertTrue(clingo.signatureIsEqualTo(signature, clingo.signatureCreate("test", 2, true)));
			assertTrue(clingo.signatureIsLessThan(signature, clingo.signatureCreate("test", 3, true)));
			int hash = clingo.signatureHash(signature);
			assertEquals(hash , clingo.signatureHash(signature)); // returns the same hash
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
