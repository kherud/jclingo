import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.potassco.clingo.backend.Backend;
import org.potassco.clingo.control.Control;
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
}
