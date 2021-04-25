package org.potassco;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.potassco.jna.ClingoLibrary;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class SignatureTest {

	@org.junit.Test
	public void test1() {
		Clingo clingo = new Clingo();
		String name = "test";
		try {
			Pointer signature = clingo.signatureCreate(name, 2, true);
			assertEquals(name, clingo.signatureName(signature));
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
