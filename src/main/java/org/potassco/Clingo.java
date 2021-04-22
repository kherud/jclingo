package org.potassco;

import org.potassco.jna.ClingoLibrary;

import com.sun.jna.ptr.IntByReference;

public class Clingo {
	private ClingoLibrary clingoLibrary;

	public Clingo() {
		this.clingoLibrary = ClingoLibrary.INSTANCE;
	}

	public String version() {
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        IntByReference patch = new IntByReference();
        clingoLibrary.clingo_version(major, minor, patch);
		return major.getValue() + "." + minor.getValue() + "." + patch.getValue();
		
	}

	public Control control(String name, String data) {
		return new Control(name, data, clingoLibrary);
	}

}
