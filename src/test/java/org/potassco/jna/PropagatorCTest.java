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
import com.sun.jna.Structure;

/**
 * @see <a href="https://potassco.org/clingo/c-api/5.5/propagator_8c-example.html">propagator.c</a>
 * 
 * @author Josef Schneeberger
 *
 */
public class PropagatorCTest {
	
	public class PropagatorData extends Structure {
		public int[] pigeons;
		public SizeT pigeonsSize;
		public long[] states;
		public long stateSize;

		public PropagatorData() {
			super();
		}

		public PropagatorData(Pointer p) {
			super(p);
		}

		protected List<String> getFieldOrder() {
			return Arrays.asList("pigeons", "pigeonsSize", "states", "stateSize");
		}
	
	}
	
	class State extends Structure {
		public int[] holes;
		public SizeT size;

		public State() {
			super();
		}

		public State(Pointer p) {
			super(p);
		}

		public State(int size) {
			super();
			this.size = new SizeT(size);
			this.holes = new int[size];
//			allocateMemory();
		}

		protected List<String> getFieldOrder() {
			return Arrays.asList("holes", "size");
		}
	
	}

	@Test
	public void test() {
		String name = "pigeon";
//		String[] params = {"h", "p"};
		String[] params = null;
		String program = "1 { place(P,H) : H = 1..h } 1 :- P = 1..p. #const h=8. #const p=9. ";
		
		// Part with arguments
//		long holes = BaseClingo.symbolCreateNumber(8);
//		long pigeons = BaseClingo.symbolCreateNumber(9);
//		PartSt[] parts = new PartSt[1];
//		long[] args = new long[2];
//		args[0] = holes;
//		args[1] = pigeons;
//		parts[0] = new PartSt(name, args, args.length);
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
		
		// create a propagator 
		// using the default implementation for the model check
		PropagatorInitCallback init = new PropagatorInitCallback() {	
			@Override
			public byte callback(Pointer init, Pointer data) {
				PropagatorData pData = new PropagatorData(data);
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
				if (pData.states != null) {
				    // in principle the number of threads can increase between solve calls by changing the configuration
					// this case is not handled (elegantly) here
					if (threads > pData.stateSize) {
						// generate exception
					}
					return 1;
				} else {
					// allocate memory for exactly one state per thread
					pData.states = new long[threads];
					pData.stateSize = threads;
				}
				// the propagator monitors place/2 atoms and detects conflicting assignments
				// first get the symbolic atoms handle
				Pointer atoms = BaseClingo.propagateInitSymbolicAtoms(init);
				// create place/2 signature to filter symbolic atoms with
				long sig = BaseClingo.signatureCreate("place", 2, true);
				// get an iterator after the last place/2 atom
				// (atom order corresponds to grounding order (and is unpredictable))
				long atomsItEnd = BaseClingo.symbolicAtomsEnd(atoms);
				// loop over the place/2 atoms in two passes
				// the first pass determines the maximum placement literal
				// the second pass allocates memory for data structures based on the first pass
				for (int pass = 0; pass < 2; ++pass) {
					// get an iterator to the first place/2 atom
					long atomsIterator = BaseClingo.symbolicAtomsBegin(atoms, sig);
//					long atomsIterator = BaseClingo.symbolicAtomsBegin(atoms, new Signature());
					if (pass == 1) {
						// allocate memory for the assignment literal -> hole mapping
//						Memory mem = new Memory(max + 1);
						int s = max + 1;
						pData.pigeonsSize = new SizeT(s);
						pData.pigeons = new int[s];
					}
					while (true) {
							//  int h;
							//  bool equal;
							//  clingo_literal_t lit;
							//  clingo_symbol_t sym;
						// stop iteration if the end is reached
						boolean equal = BaseClingo.symbolicAtomsIteratorIsEqualTo(atoms, atomsIterator, atomsItEnd);
						if (equal) { break; }
						// get the solver literal for the placement atom
						int lit = BaseClingo.symbolicAtomsLiteral(atoms, atomsIterator);
						lit = BaseClingo.propagateInitSolverLiteral(init, lit);
						if (pass == 0) {
							// determine the maximum literal
							//    assert(lit > 0); - writes error to log
							if (lit > max) { max = lit; }
						} else {
							// extract the hole number from the atom
							long sym = BaseClingo.symbolicAtomsSymbol(atoms, atomsIterator);
							int h = getArg(sym, 1);
							// initialize the assignemnt literal -> hole mapping
							pData.pigeons[lit] = h;
							// watch the assignment literal
							BaseClingo.propagateInitAddWatch(init, lit);
							// update the total number of holes
							if (h + 1 > holes) { holes = h + 1; }
						}
						// advance to the next placement atom
						atomsIterator = BaseClingo.symbolicAtomsNext(atoms, atomsIterator);
					}
				}
				// initialize the per solver thread state information
				for (int i = 0; i < threads; ++i) {
					// initially no pigeons are assigned to any holes
					// so the hole -> literal mapping is initialized with zero
					// which is not a valid literal
					State s = new State();
//					pData.states[i] = new State();
//					Memory mem = new Memory(s.holes.length * holes);
					s.holes = new int[s.holes.length * holes];
					s.size = new SizeT(holes);
				}
				return 1;
			}

			private int getArg(long sym, int offset) {
				// get the arguments of the function symbol
				long[] args = BaseClingo.symbolArguments(sym);
				// get the requested numeric argument
				return BaseClingo.symbolNumber(args[offset]);
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
		// TODO: use ConfigurationTree to check configuration variants (Parts, etc.)
		boolean modelExists = true;
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
		}
	}

}
