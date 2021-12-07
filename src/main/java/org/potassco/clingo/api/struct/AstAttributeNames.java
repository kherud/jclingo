package org.potassco.clingo.api.struct;

import com.sun.jna.Structure;

/**
 * Struct to map attributes to their string representation.
 * @author Josef Schneeberger
 */
public class AstAttributeNames extends Structure {
	private String[] names;
	private long size;
}
