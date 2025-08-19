import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.propagator.Assignment;
import org.potassco.clingo.propagator.PropagateControl;
import org.potassco.clingo.propagator.PropagateInit;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.symbol.Signature;

public class PropagatorAssertingClauseTest implements Propagator {

	private Control control;

	@Before
	public void setup() {
		control = new Control("0");
		control.add("start. {value}. {end}.");
		control.ground();
	}

	@After
	public void tearDown() {
		control.close();
		control = null;
	}

	@Test
	public void testDefault() {
		control.registerPropagator(new TestPropagator(false));
		control.solve(SolveMode.YIELD).getSolveResult();
	}

	@Test
	public void testLocked() {
		control.registerPropagator(new TestPropagator(true));
		control.solve(SolveMode.YIELD).getSolveResult();
	}

	private static final class TestPropagator implements Propagator {
		private int startLiteral = 0;
		private int endLiteral = 0;
		private int valueLiteral = 0;
		private final boolean lock;

		private TestPropagator(boolean lock) {
			this.lock = lock;
		}

		@Override
		public void init(PropagateInit init) {
			init.getSymbolicAtoms().iterator(new Signature("start", 0))
					.forEachRemaining(atom -> startLiteral = atom.getLiteral());
			init.getSymbolicAtoms().iterator(new Signature("end", 0))
					.forEachRemaining(atom -> endLiteral = atom.getLiteral());
			init.getSymbolicAtoms().iterator(new Signature("value", 0))
					.forEachRemaining(atom -> valueLiteral = atom.getLiteral());
			Stream.of(startLiteral, endLiteral, valueLiteral).sorted().forEach(literal -> {
				init.addWatch(literal);
				init.addWatch(-literal);
			});
		}

		@Override
		public void propagate(PropagateControl control, int[] changes) {
			Assignment assignment = control.getAssignment();
			if (assignment.isFalse(-valueLiteral) && assignment.isFalse(endLiteral)) {
				int decisionLevel = assignment.getDecisionLevel();
				int[] noGood = new int[] { startLiteral, -endLiteral, -valueLiteral };
				boolean result = control.addNoGood(noGood, false, lock);
				Assert.assertEquals(decisionLevel, assignment.getDecisionLevel());
				Assert.assertFalse(result);
			}
		}

		@Override
		public int decide(int threadId, Assignment assignment, int fallback) {
			if (assignment.isFree(endLiteral)) return -endLiteral;
			if (assignment.isFree(valueLiteral)) return -valueLiteral;
			return fallback;
		}
	}
}
