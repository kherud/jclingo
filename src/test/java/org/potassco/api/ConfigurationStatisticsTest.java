package org.potassco.api;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ConfigurationStatisticsTest {

	@Test
	public void testConfiguration() {
		Clingo clingo = new Clingo(); 
		String[] arguments = {"-t", "2"};
		Control control = clingo.control(arguments);
		Configuration configuration = control.configuration();
		List<String> keys = configuration.getKeys();
		assertTrue(keys.contains("solver"));
	}

}
