package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.SolveMode;
import org.potassco.jna.PropagatorSt.PropagatorInitCallback;
import org.potassco.jna.PropagatorSt.PropagatorPropagateCallback;
import org.potassco.jna.PropagatorSt.PropagatorUndoCallback;

import com.sun.jna.Pointer;
import com.sun.jna.Callback;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

/**
 * @see <a href="https://potassco.org/clingo/c-api/5.5/propagator_8c-example.html">propagator.c</a>
 * 
 * @author Josef Schneeberger
 *
 */
public class PropagatorTest {
	
	class PropagatorData extends Structure {
		public IntByReference pigeons;
		public SizeT pigeonsSize;
		public State state;
		public long stateSize;
	
		public PropagatorData(IntByReference pigeons, SizeT pigeonsSize, State state, long stateSize) {
			super();
			this.pigeons = pigeons;
			this.pigeonsSize = pigeonsSize;
			this.state = state;
			this.stateSize = stateSize;
		}
	
		protected List<String> getFieldOrder() {
			return Arrays.asList("pigeons", "pigeonsSize", "state", "stateSize");
		}
	
	}
	
	class State extends Structure {
		public int holes;
		public SizeT size;
	
		protected List<String> getFieldOrder() {
			return Arrays.asList("holes", "size");
		}
	
	}

	@Test
	public void test() {
		String name = "pigeon";
		String[] params = {"h", "p"};
		String program = "1 { place(P,H) : H = 1..h } 1 :- P = 1..p.";
		
		// Part with arguments
		long holes = BaseClingo.symbolCreateNumber(8);
		long pigeons = BaseClingo.symbolCreateNumber(9);
		PartSt[] parts = new PartSt[1];
		long[] args = new long[2];
		args[0] = holes;
		args[1] = pigeons;
		parts[0] = new PartSt(name, args, args.length);
		
		// create a propagator 
		// using the default implementation for the model check
		PropagatorInitCallback init = new PropagatorInitCallback() {	
			@Override
			public byte callback(Pointer init, Pointer data) {
				PropagatorData pData = data.
				// the total number of holes pigeons can be assigned too
				int holes = 0;
				int threads = BaseClingo.propagateInitNumberOfThreads(init);
				// stores the (numeric) maximum of the solver literals capturing pigeon placements
				// note that the code below assumes that this literal is not negative
				// which holds for the pigeon problem but not in general
				int max = 0;
				//  clingo_symbolic_atoms_t const *atoms;
				//  clingo_signature_t sig;
				//  clingo_symbolic_atom_iterator_t atoms_it, atoms_ie;
				// ensure that solve can be called multiple times
				// for simplicity, the case that additional holes or pigeons to assign are grounded is not handled here
//  if (data->states != NULL) {
    // in principle the number of threads can increase between solve calls by changing the configuration
// this case is not handled (elegantly) here
//if (threads > data->states_size) {
//  clingo_set_error(clingo_error_runtime, "more threads than states");
//    }
//    return true;
//  }
  // allocate memory for exactly one state per thread
//  if (!(data->states = (state_t*)malloc(sizeof(*data->states) * threads))) {
//    clingo_set_error(clingo_error_bad_alloc, "allocation failed");
//    return false;
//  }
//  memset(data->states, 0, sizeof(*data->states) * threads);
//  data->states_size = threads;
  // the propagator monitors place/2 atoms and dectects conflicting assignments
  // first get the symbolic atoms handle
//  if (!clingo_propagate_init_symbolic_atoms(init, &atoms)) { return false; }
  // create place/2 signature to filter symbolic atoms with
//  if (!clingo_signature_create("place", 2, true, &sig)) { return false; }
  // get an iterator after the last place/2 atom
  // (atom order corresponds to grounding order (and is unpredictable))
//  if (!clingo_symbolic_atoms_end(atoms, &atoms_ie)) { return false; }
  // loop over the place/2 atoms in two passes
  // the first pass determines the maximum placement literal
  // the second pass allocates memory for data structures based on the first pass
//  for (int pass = 0; pass < 2; ++pass) {
    // get an iterator to the first place/2 atom
//if (!clingo_symbolic_atoms_begin(atoms, &sig, &atoms_it)) { return false; }
//if (pass == 1) {
  // allocate memory for the assignemnt literal -> hole mapping
//  if (!(data->pigeons = (int*)malloc(sizeof(*data->pigeons) * (max + 1)))) {
//    clingo_set_error(clingo_error_bad_alloc, "allocation failed");
//    return false;
//  }
//  data->pigeons_size = max + 1;
//}
//while (true) {
//  int h;
//  bool equal;
//  clingo_literal_t lit;
//  clingo_symbol_t sym;
  // stop iteration if the end is reached
//  if (!clingo_symbolic_atoms_iterator_is_equal_to(atoms, atoms_it, atoms_ie, &equal)) { return false; }
//  if (equal) { break; }
  // get the solver literal for the placement atom
//  if (!clingo_symbolic_atoms_literal(atoms, atoms_it, &lit)) { return false; }
//  if (!clingo_propagate_init_solver_literal(init, lit, &lit)) { return false; }
//  if (pass == 0) {
    // determine the maximum literal
//    assert(lit > 0);
//    if (lit > max) { max = lit; }
//  }
//  else {
    // extract the hole number from the atom
//    if (!clingo_symbolic_atoms_symbol(atoms, atoms_it, &sym)) { return false; }
//    if (!get_arg(sym, 1, &h)) { return false; }
    // initialize the assignemnt literal -> hole mapping
//    data->pigeons[lit] = h;
    // watch the assignment literal
//    if (!clingo_propagate_init_add_watch(init, lit)) { return false; }
    // update the total number of holes
//    if (h + 1 > holes)   { holes = h + 1; }
//  }
  // advance to the next placement atom
//  if (!clingo_symbolic_atoms_next(atoms, atoms_it, &atoms_it)) { return false; }
//}
				
				return 1;
			}
		};
		PropagatorPropagateCallback propagate = new PropagatorPropagateCallback() {
			@Override
			public byte callback(Pointer control, Pointer changes, SizeT size, Pointer data) {
				// TODO Auto-generated method stub
				return 1;
			}
		};
		PropagatorUndoCallback undo = new PropagatorUndoCallback() {
			@Override
			public void callback(Pointer control, Pointer changes, SizeT size, Pointer data) {
				// TODO Auto-generated method stub
				int i = 0;
			}
		};
		PropagatorSt prop = new PropagatorSt(init, propagate , undo , null, null);
//		PropagatorData propData = new PropagatorData(null, new SizeT(), null, 0L);

		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlRegisterPropagator(control, prop, /* propData */ null, false);
		BaseClingo.controlAdd(control, name, params, program);
		BaseClingo.controlGround(control, parts, null, null);
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), null, null);
		boolean modelExists = true;
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
		}
	}

}
