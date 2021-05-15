package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * https://potassco.org/clingo/c-api/5.5/application_8c-example.html
 * @author Josef Schneeberger
 *
 */
public class ApplicationCTest {

	@Test
	public void test() {
		ApplicationSt app = new ApplicationSt("example", "1.0.0", null, mainLoop, null, null, registerOptions, null);
		BaseClingo.main(app, null, null, options);
	}

}
