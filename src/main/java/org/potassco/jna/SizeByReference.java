package org.potassco.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class SizeByReference extends ByReference {
	public SizeByReference() {
		this(0);
	}

	public SizeByReference(long value) {
		super(Native.SIZE_T_SIZE);
		setValue(value);
	}

	public void setValue(long value) {
		Pointer p = getPointer();
		switch (Native.SIZE_T_SIZE) {
		case 2:
			p.setShort(0, (short) value);
			break;
		case 4:
			p.setInt(0, (int) value);
			break;
		case 8:
			p.setLong(0, value);
			break;
		default:
			throw new RuntimeException("Unsupported size: " + Native.SIZE_T_SIZE);
		}
	}

	public SizeT getValue() {
		Pointer p = getPointer();
		switch (Native.SIZE_T_SIZE) {
		case 2:
			return new SizeT(p.getShort(0) & 0xFFFFL);
		case 4:
			return new SizeT(p.getInt(0) & 0xFFFFFFFFL);
		case 8:
			return new SizeT(p.getLong(0));
		default:
			throw new RuntimeException("Unsupported size: " + Native.SIZE_T_SIZE);
		}
	}
}