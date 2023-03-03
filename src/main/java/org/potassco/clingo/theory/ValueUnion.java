package org.potassco.clingo.theory;

import java.util.Objects;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import org.potassco.clingo.symbol.Symbol;

public class ValueUnion extends Union {

	// all fields annotated with SuppressWarnings are required to be public by JNA
	@SuppressWarnings("WeakerAccess")
	public int int_number;
	@SuppressWarnings("WeakerAccess")
	public double double_number;
	@SuppressWarnings("WeakerAccess")
	public long symbol;

	public ValueUnion(int int_number) {
		super();
		this.int_number = int_number;
		setType(Integer.TYPE);
	}

	public ValueUnion(double double_number) {
		super();
		this.double_number = double_number;
		setType(Double.TYPE);
	}

	public ValueUnion(Symbol symbol) {
		super();
		this.symbol = symbol.getLong();
		setType(Long.TYPE);
	}

	@SuppressWarnings("WeakerAccess")
	public ValueUnion() {
		super();
	}

	ValueUnion(long symbol) {
		super();
		this.symbol = symbol;
		setType(Long.TYPE);
	}

	ValueUnion(Pointer peer) {
		super(peer);
	}

	public int getInt() {
		return int_number;
	}

	public double getDouble() {
		return double_number;
	}

	public long getSymbol() {
		return symbol;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ValueUnion that = (ValueUnion) o;
		return int_number == that.int_number && Double.compare(that.double_number, double_number) == 0 && symbol == that.symbol;
	}

	@Override
	public int hashCode() {
		return Objects.hash(int_number, double_number, symbol);
	}

	public static class ByReference extends ValueUnion implements Structure.ByReference { }

	public static class ByValue extends ValueUnion implements Structure.ByValue { }

}
