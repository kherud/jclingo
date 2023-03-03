package org.potassco.clingo.theory;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public class ValueTypeConverter implements TypeConverter {
	@Override
	public Object toNative(Object value, ToNativeContext ctx) {
		if (value == null) {
			return ValueType.SYMBOL.getValue();
		} else {
			return ((ValueType)value).getValue();
		}
	}
	@Override
	public Object fromNative(Object value, FromNativeContext context) {
		return ValueType.fromValue(((Integer)value));
	}
	@Override
	public Class<?> nativeType() {
		return Integer.class;
	}
}
