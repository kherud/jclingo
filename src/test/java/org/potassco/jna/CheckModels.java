package org.potassco.jna;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.potassco.enums.ShowType;

import com.sun.jna.Pointer;

public class CheckModels {

	protected void checkModels(Pointer control, Pointer handle, String[] expectedStrings, int modelsCount) {
		boolean modelExits = true;
		int i = 0;
		while (modelExits) {
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				long mn = BaseClingo.modelNumber(model);
				long[] atoms = BaseClingo.modelSymbols(model, ShowType.SHOWN);
				for (int j = 0; j < atoms.length; j++) {
					String str = BaseClingo.symbolToString(atoms[j]);
					assertTrue(Arrays.stream(expectedStrings).anyMatch(str::equals));
					assertTrue(BaseClingo.modelContains(model, atoms[j]));
				}
				BaseClingo.solveHandleResume(handle);
				i++;
			} else {
				modelExits = false;
			}
		}
	    BaseClingo.solveHandleClose(handle);
	    // clean up
	    BaseClingo.controlFree(control);
		assertEquals(modelsCount, i);
	}

}
