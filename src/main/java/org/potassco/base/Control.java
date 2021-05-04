package org.potassco.base;

import org.potassco.cpp.clingo_h;
import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.enums.TruthValue;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.GroundCallbackT;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Intended to hold the control pointer and to free
 * the according memory in clingo.
 * clingo_control_new
 * clingo_control_free
 * 
 * @author Josef Schneeberger
 *
 */
public class Control implements AutoCloseable {
	private String name;
	private String logicProgram;
	private Pointer control;
	private ClingoLibrary clingoLibrary;

	private Control() {
		super();
		this.name = "base";
	}

	public Control(ClingoLibrary clingoLibrary) {
		this();
		this.clingoLibrary = clingoLibrary;
	}

	public Control(String name, String[] arguments, String logicProgram, ClingoLibrary clingoLibrary) {
		this(clingoLibrary);
		this.name = name;
		this.logicProgram = logicProgram;
//		controlNew(null);
//		add(name, arguments, logicProgram);
	}

	@Override
	public void close() throws Exception {
        clingoLibrary.clingo_control_free(this.control);
        System.out.println("Freed clingo control");
	}

}
