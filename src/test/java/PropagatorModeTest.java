import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.propagator.PropagateControl;
import org.potassco.clingo.propagator.PropagateInit;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.propagator.PropagatorCheckMode;
import org.potassco.clingo.propagator.PropagatorUndoMode;
import org.potassco.clingo.solving.SolveResult;

public class PropagatorModeTest implements Propagator {

	private int numCheck = 0;
	private int numUndo = 0;

	@Test
	public void testPropagatorControl() {
		Control control = new Control();
		control.add("{a; b}.");
		control.ground();
		control.registerPropagator(this, false);
		SolveResult solveResult = control.solve().getSolveResult();

		Assert.assertTrue(solveResult.satisfiable());
		Assert.assertTrue(numCheck >= 3);
		Assert.assertEquals(numCheck, numUndo + 1);

		control.close();
	}

	@Override
	public void init(PropagateInit init) {
		init.setCheckMode(PropagatorCheckMode.FIXPOINT);
		init.setUndoMode(PropagatorUndoMode.ALWAYS);
		Assert.assertEquals(PropagatorCheckMode.FIXPOINT, init.getCheckMode());
		Assert.assertEquals(PropagatorUndoMode.ALWAYS, init.getUndoMode());
	}

	@Override
	public void check(PropagateControl control) {
		numCheck++;
	}

	@Override
	public void undo(PropagateControl control, int[] changes) {
		numUndo++;
	}
}
