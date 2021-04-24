package org.potassco;

import org.potassco.enums.ModelType;
import org.potassco.jna.ClingoLibrary;

import com.sun.jna.Pointer;

public class Model {

	private Pointer pointer;
	private ClingoLibrary clingoLibrary;

	private Model() {
		this.clingoLibrary = ClingoLibrary.INSTANCE;
	}
	
	public Model(Pointer pointer) {
		this();
		this.pointer = pointer;
	}

	public ModelType type(Pointer p_type) {
		clingoLibrary.clingo_model_type(this.pointer, p_type);
	}
}
