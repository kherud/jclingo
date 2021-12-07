package org.potassco.clingo.api.types;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.platform.EnumConverter;

public class ClingoTypeMapper extends DefaultTypeMapper {

    public ClingoTypeMapper() {
        addTypeConverter(ErrorCode.class, new EnumConverter<>(ErrorCode.class));
    }

}
