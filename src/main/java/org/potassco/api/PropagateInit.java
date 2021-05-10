package org.potassco.api;

import org.potassco.jna.BaseClingo;
import org.potassco.jna.SizeT;

import com.sun.jna.Pointer;

public class PropagateInit {

	private Pointer reference;

	public boolean addClause(int[] clause) {
		return BaseClingo.propagateInitAddClause(this.reference, clause, new SizeT(clause.length)) == 1;
	}
	
	public int addLiteral(boolean freeze) {
		return BaseClingo.propagateInitAddLiteral(this.reference, (byte) (freeze ? 1 : 0));
	}

	public boolean addWeightConstraint(int literal, Pointer literals, SizeT size, int bound, int type, byte compareEqual) {
		return BaseClingo.propagateInitAddWeightConstraint(this.reference, literal, literals, size, bound, type, compareEqual);
	}

	public void addMinimize(int literal, int weight, int priority) {
		BaseClingo.propagateInitAddMinimize(this.reference, literal, weight, priority);
	}
	
	public void addWatch(int solverLiteral, Integer threadId) {
		if (threadId == null) {
			BaseClingo.propagateInitAddWatch(this.reference, solverLiteral);
		} else {
			BaseClingo.propagateInitAddWatchToThread(this.reference, solverLiteral, threadId);
		}
	}
	
	public long solverLiteral(int aspifLiteral) {
		return BaseClingo.propagateInitSolverLiteral(this.reference, aspifLiteral);
	}

	public SymbolicAtoms symbolicAtoms() {
		return new SymbolicAtoms(BaseClingo.propagateInitSymbolicAtoms(this.reference));
	}

	public SymbolicAtoms theoryAtoms() {
		return new SymbolicAtoms(BaseClingo.propagateInitTheoryAtoms(this.reference));
	}
	
	public int numberOfThreads() {
		return BaseClingo.propagateInitNumberOfThreads(this.reference);
	}

	public void setCheckMode(int mode) {
		BaseClingo.propagateInitSetCheckMode(this.reference, mode);
	}

	public int getCheckMode() {
		return BaseClingo.propagateInitGetCheckMode(this.reference);
	}

	public Assignment assignment() {
		return new Assignment(BaseClingo.propagateInitAssignment(this.reference));
	}

	public boolean propagate() {
		return BaseClingo.propagateInitPropagate(this.reference);
	}

}
