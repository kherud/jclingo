package org.potassco.jna;

import com.sun.jna.IntegerType;

public class C_Int extends IntegerType {
	private static final long serialVersionUID = 1L;

	public C_Int() {
		this(0);
	}

	public C_Int(long value) {
		super(16, value, false);
	}
}
