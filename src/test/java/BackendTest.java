import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.backend.Backend;
import org.potassco.clingo.backend.BackendType;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Symbol;
import org.potassco.clingo.theory.TheoryAtom;
import org.potassco.clingo.theory.TheoryElement;
import org.potassco.clingo.theory.TheorySequenceType;

public class BackendTest {

	@Test
	public void testAddTheory() {
		try (Control control = new Control()) {
			try (Backend backend = control.getBackend()) {
				int num1 = backend.addTheoryTermNumber(1);
				int num2 = backend.addTheoryTermNumber(2);
				int text = backend.addTheoryTermString("x");
				int fun = backend.addTheoryTermFunction("f", new int[] { num1, num2, text });
				int seq = backend.addTheoryTermSequence(TheorySequenceType.SET, new int[] { num1, num2, text });
				int lst = backend.addTheoryTermSequence(TheorySequenceType.LIST, new int[] { num1, num2 });
				int tup = backend.addTheoryTermSequence(TheorySequenceType.TUPLE, new int[] { num1, num2 });
				int funSeq = backend.addTheoryTermFunction("f", new int[] { seq });

				Assert.assertEquals(num1, backend.addTheoryTermNumber(1));
				Assert.assertEquals(num2, backend.addTheoryTermNumber(2));
				Assert.assertEquals(text, backend.addTheoryTermString("x"));
				Assert.assertEquals(num1, backend.addTheoryTermSymbol(Symbol.fromString("1")));
				Assert.assertEquals(num2, backend.addTheoryTermSymbol(Symbol.fromString("2")));
				Assert.assertEquals(fun, backend.addTheoryTermSymbol(Symbol.fromString("f(1,2,x)")));

				int elem = backend.addTheoryElement(
						new int[] { num1, num2, seq, fun, lst, tup },
						new int[] { 1, -2, 3 }
				);

				backend.addTheoryAtom(fun, new int[0], 0);
				backend.addTheoryAtomWithGuard(fun, new int[0], "=", num1, 0);
				backend.addTheoryAtom(
						backend.addTheoryTermSymbol(Symbol.fromString("g(1,2)")),
						new int[0],
						0
				);
				backend.addTheoryAtom(funSeq, new int[] { elem }, 0);
			}

			List<String> theoryAtoms = new ArrayList<>();
			TheoryElement[] theoryElements = null;
			for (TheoryAtom atom : control.getTheoryAtoms()) {
				theoryAtoms.add(atom.toString());
				theoryElements = atom.getElements();
			}
			Assert.assertEquals(
					List.of(
							"&f(1,2,x){}",
							"&f(1,2,x){}=1",
							"&g(1,2){}",
							"&f({1,2,x}){1,2,{1,2,x},f(1,2,x),[1,2],(1,2): #aux(1),not #aux(2),#aux(3)}"
					),
					theoryAtoms
			);
			Assert.assertTrue(theoryElements != null && theoryElements.length > 0);
			Assert.assertArrayEquals(
					new int[] { 1, -2, 3 },
					theoryElements[theoryElements.length - 1].getConditions()
			);
		}
	}

	/**
	 * An empty body is legal in a weight rule, and so is an empty minimize statement.
	 */
	@Test
	public void testEmptyWeightedLiterals() {
		try (Control control = new Control()) {
			try (Backend backend = control.getBackend()) {
				int atom = backend.addAtom(new Function("a"));
				backend.addWeightRule(new int[] { atom }, 0, new WeightedLiteral[0], false);
				backend.addMinimize(new WeightedLiteral[0], 0);
			}
			control.ground();
			try (SolveHandle handle = control.solve(SolveMode.YIELD)) {
				Assert.assertTrue(handle.hasNext());
				Assert.assertEquals("a", handle.next().toString());
			}
		}
	}

	/**
	 * Obtaining the backend starts a batch of statements, so the same object is handed out until it is closed. Closing
	 * it twice must not end the batch twice.
	 */
	@Test
	public void testBackendReuse() {
		try (Control control = new Control()) {
			Backend backend = control.getBackend();
			Assert.assertSame(backend, control.getBackend());
			backend.close();
			backend.close();
			Assert.assertTrue(backend.isClosed());
			Backend next = control.getBackend();
			Assert.assertNotSame(backend, next);
			next.close();
		}
	}

	/**
	 * Registers one of clingo's own backends and checks that it writes the grounding.
	 */
	@Test
	public void testRegisterBackend() throws IOException {
		Path file = Files.createTempFile("jclingo", ".aspif");
		try (Control control = new Control()) {
			control.registerBackend(BackendType.ASPIF, file);
			control.add("a. b :- a.");
			control.ground();
			control.solveResult();
		}
		finally {
			// the backend only flushes once the control is gone
			Assert.assertTrue(Files.readString(file).startsWith("asp 1 0 0"));
			Files.deleteIfExists(file);
		}
	}
}
