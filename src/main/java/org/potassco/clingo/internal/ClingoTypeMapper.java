package org.potassco.clingo.internal;

import com.sun.jna.DefaultTypeMapper;

public class ClingoTypeMapper extends DefaultTypeMapper {

    public ClingoTypeMapper() {
        addTypeConverter(JnaEnum.class, new EnumConverter());
    }

}
