package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Structure;

/**
 * Represents a predicate signature.
 *
 * Signatures have a name and an arity, and can be positive or negative (to
 * represent classical negation).
 *
 * @author Josef Schneeberger {@link clingo_h#clingo_signature_t}
 */
public class SignatureSt extends Structure {
	public String name;
	public int arity;
	public int positive;

	protected List<String> getFieldOrder() {
		return Arrays.asList("name", "arity", "positive");
	}
}
