package org.potassco.clingo.internal;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.platform.EnumConverter;
import org.potassco.clingo.control.ErrorCode;
import org.potassco.clingo.control.WarningCode;

public class ClingoTypeMapper extends DefaultTypeMapper {

    public ClingoTypeMapper() {
        addTypeConverter(ErrorCode.class, new EnumConverter<>(ErrorCode.class));
        addTypeConverter(WarningCode.class, new EnumConverter<>(WarningCode.class));
    }

}
