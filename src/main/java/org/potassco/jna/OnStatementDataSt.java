package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class OnStatementDataSt extends Structure {

	public LocationSt loc;
	public AstSt atom;
	public Pointer builder;

	public OnStatementDataSt(LocationSt loc, AstSt atom, Pointer builder) {
		super();
		this.loc = loc;
		this.atom = atom;
		this.builder = builder;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("loc", "atom", "builder");
	}

}
