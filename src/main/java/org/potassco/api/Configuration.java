package org.potassco.api;

import java.util.LinkedList;
import java.util.List;

import org.potassco.enums.ConfigurationType;
import org.potassco.jna.BaseClingo;
import org.potassco.jna.SizeT;

import com.sun.jna.Pointer;

public class Configuration {

	private Pointer reference;
	private int key;

	public Configuration(Pointer reference, int key) {
		this.reference = reference;
		this.key = key;
	}

	private int type() {
		return BaseClingo.configurationType(this.reference, this.key);
	}
	
//	public List<String> getKeys() {
//		List<String> result = new LinkedList<String>();
//		ConfigurationType t = type();
//		for (long j = 0L; j < BaseClingo.configurationMapSize(this.reference, this.key).longValue(); j++) {
//			String name = BaseClingo.configurationMapSubkeyName(this.reference, this.key, new SizeT(j));
//			result.add(name);
//		}
//		return result;
//	}
	
}
