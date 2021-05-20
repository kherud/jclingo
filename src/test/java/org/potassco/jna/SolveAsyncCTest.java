package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/**
 * https://potassco.org/clingo/c-api/5.5/solve-async_8c-example.html
 * @author Josef Schneeberger
 *
 */
public class SolveAsyncCTest {
	
	@Test
	public void test() {
		String name = "base";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, "#const n = 17."
				+ "1 { p(X); q(X) } 1 :- X = 1..n."
				+ ":- not n+1 { p(1..n); q(1..n) }.");
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
        // ground the base part
		BaseClingo.controlGround(control, parts, null, null);
		SolveEventCallback onEvent = new SolveEventCallback() {	
			@Override
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				int in = data.getInt(0);
				if (type == SolveEventType.FINISH.getValue()) {
//					atomic_flag *running = (atomic_flag*)data;
//					atomic_flag_clear(running);
				}
				return true;
			}
		};
		IntByReference running = new IntByReference(1);
		// create a solve handle with an attached vent handler
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.ASYNC, null, new SizeT(), onEvent, running.getPointer());
		// TODO clingo_solve_mode_async | clingo_solve_mode_yield
		// let's approximate pi
//		  do {
//		    ++samples;
//		    x = rand();
//		    y = rand();
//		    if (x * x + y * y <= (uint64_t)RAND_MAX * RAND_MAX) { incircle+= 1; }
//		  }
//		  while (atomic_flag_test_and_set(&running));
//		  printf("pi = %g\n", 4.0*incircle/samples);
		// get the solve result
		BaseClingo.solveHandleGet(handle);
	}

}
