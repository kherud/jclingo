import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.SymbolType;
import org.potassco.clingo.theory.TheoryAtom;
import org.potassco.clingo.theory.TheoryAtoms;
import org.potassco.clingo.theory.TheoryElement;
import org.potassco.clingo.theory.TheoryTerm;
import org.potassco.clingo.theory.TheoryTermType;


public class AtomsTest {

    private Control control;
    private final String testTheory;

    public AtomsTest() throws IOException {
        this.testTheory = Files.readString(
                // wrapped in file here to avoid Windows file system problems
                new File(getClass().getResource("atoms-theory.lp").getFile()).toPath()
        );
    }

    @Before
    public void setup() {
        control = new Control();
    }

    @After
    public void tearDown() {
        control.close();
    }

    @Test
    public void testSymbolicAtom() {
        control.add("p(1). {p(2)}. #external p(3).");
        control.ground();

        SymbolicAtoms symbolicAtoms = control.getSymbolicAtoms();

        SymbolicAtom p1 = symbolicAtoms.get(new Function("p", new Number(1)));
        Assert.assertNotNull(p1);
        Assert.assertTrue(p1.isFact());
        Assert.assertFalse(p1.isExternal());
        Assert.assertTrue(p1.getLiteral() >= 1);
        Assert.assertEquals(p1.getSymbol(), new Function("p", new Number(1)));
        Assert.assertTrue(p1.match(new Signature("p", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 2, true)));
        Assert.assertFalse(p1.match(new Signature("b", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 1, false)));

        SymbolicAtom p2 = symbolicAtoms.get(new Function("p", new Number(2)));
        Assert.assertNotNull(p2);
        Assert.assertFalse(p2.isFact());
        Assert.assertFalse(p2.isExternal());
        Assert.assertTrue(p2.getLiteral() >= 2);
        Assert.assertEquals(p2.getSymbol(), new Function("p", new Number(2)));
        Assert.assertTrue(p2.match(new Signature("p", 1, true)));
        Assert.assertFalse(p2.match(new Signature("p", 2, true)));
        Assert.assertFalse(p2.match(new Signature("b", 1, true)));
        Assert.assertFalse(p2.match(new Signature("p", 1, false)));

        SymbolicAtom p3 = symbolicAtoms.get(new Function("p", new Number(3)));
        Assert.assertNotNull(p3);
        Assert.assertFalse(p3.isFact());
        Assert.assertTrue(p3.isExternal());
        Assert.assertTrue(p3.getLiteral() >= 2);
        Assert.assertEquals(p3.getSymbol(), new Function("p", new Number(3)));
        Assert.assertTrue(p3.match(new Signature("p", 1, true)));
        Assert.assertFalse(p3.match(new Signature("p", 2, true)));
        Assert.assertFalse(p3.match(new Signature("b", 1, true)));
        Assert.assertFalse(p3.match(new Signature("p", 1, false)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testSymbolicAtomNonExisting() {
        control.add("p(1). {p(2)}. #external p(3).");
        control.ground();

        SymbolicAtoms symbolicAtoms = control.getSymbolicAtoms();

        symbolicAtoms.get(new Function("p", new Number(4)));
    }

    @Test
    public void testSymbolicAtoms() {
        control.add("p(1). {p(2)}. #external p(3). q(1). -p(1). {q(2)}. #external q(3).");
        control.ground();

        SymbolicAtoms symbolicAtoms = control.getSymbolicAtoms();

        List<Signature> signatures = symbolicAtoms.getSignatures();
        signatures.sort(Signature::compareTo);
        Assert.assertEquals(3, signatures.size());

        Assert.assertEquals(new Signature("p", 1, true), signatures.get(0));
        Assert.assertEquals(new Signature("q", 1, true), signatures.get(1));
        Assert.assertEquals(new Signature("p", 1, false), signatures.get(2));

        List<SymbolicAtom> ps = new ArrayList<>();
        symbolicAtoms.iterator(new Signature("p", 1)).forEachRemaining(ps::add);
        Assert.assertEquals(3, ps.size());

        for (SymbolicAtom p : ps) {
            Assert.assertEquals(SymbolType.FUNCTION, p.getSymbol().getType());
            Assert.assertEquals("p", ((Function) p.getSymbol()).getName());
            Assert.assertTrue(((Function) p.getSymbol()).isPositive());
            Assert.assertEquals(1, ((Function) p.getSymbol()).getArguments().length);
        }

        List<SymbolicAtom> nps = new ArrayList<>();
        symbolicAtoms.iterator(new Signature("p", 1, false)).forEachRemaining(nps::add);
        Assert.assertEquals(1, nps.size());

        for (SymbolicAtom np : nps) {
            Assert.assertEquals(SymbolType.FUNCTION, np.getSymbol().getType());
            Assert.assertEquals("p", ((Function) np.getSymbol()).getName());
            Assert.assertFalse(((Function) np.getSymbol()).isPositive());
            Assert.assertEquals(1, ((Function) np.getSymbol()).getArguments().length);
            Assert.assertEquals(new Number(1), ((Function) np.getSymbol()).getArguments()[0]);
        }

        Assert.assertEquals(7, symbolicAtoms.size());
        List<SymbolicAtom> allAtoms = new ArrayList<>();
        symbolicAtoms.iterator().forEachRemaining(allAtoms::add);
        Assert.assertEquals(7, allAtoms.size());

        Assert.assertTrue(symbolicAtoms.contains(new Function("p", new Number(1))));
        Assert.assertTrue(symbolicAtoms.contains(new Function("p", false, new Number(1))));
        Assert.assertTrue(symbolicAtoms.contains(new Function("q", new Number(2))));
        Assert.assertFalse(symbolicAtoms.contains(new Function("q", false, new Number(2))));
    }

    @Test
    public void testTheoryTerms() {
        control.add(testTheory);
        control.add("&a { 1,a,f(a),{1},(1,),[1] }.");
        control.ground();

        TheoryAtoms theoryAtoms = control.getTheoryAtoms();
        Assert.assertTrue(theoryAtoms.size() > 0);
        TheoryAtom theoryAtom = theoryAtoms.getTheoryAtom(0);
        TheoryElement[] elements = theoryAtom.getElements();
        Assert.assertTrue(elements.length > 0);
        TheoryTerm[] terms = elements[0].getTerms();

        Assert.assertEquals(6, terms.length);
        Assert.assertEquals("1", terms[0].toString());
        Assert.assertEquals("a", terms[1].toString());
        Assert.assertEquals("f(a)", terms[2].toString());
        Assert.assertEquals("{1}", terms[3].toString());
        Assert.assertEquals("(1,)", terms[4].toString());
        Assert.assertEquals("[1]", terms[5].toString());

        Assert.assertEquals(TheoryTermType.NUMBER, terms[0].getType());
        Assert.assertEquals(1, terms[0].getNumber());

        Assert.assertEquals(TheoryTermType.SYMBOL, terms[1].getType());
        Assert.assertEquals("a", terms[1].getName());

        Assert.assertEquals(TheoryTermType.FUNCTION, terms[2].getType());
        Assert.assertEquals("f", terms[2].getName());
        Assert.assertArrayEquals(new TheoryTerm[]{terms[1]}, terms[2].getArguments());

        Assert.assertEquals(TheoryTermType.SET, terms[3].getType());
        Assert.assertArrayEquals(new TheoryTerm[]{terms[0]}, terms[3].getArguments());

        Assert.assertEquals(TheoryTermType.TUPLE, terms[4].getType());
        Assert.assertArrayEquals(new TheoryTerm[]{terms[0]}, terms[4].getArguments());

        Assert.assertEquals(TheoryTermType.LIST, terms[5].getType());
        Assert.assertArrayEquals(new TheoryTerm[]{terms[0]}, terms[5].getArguments());

        Assert.assertNotEquals(terms[0].hashCode(), terms[1].hashCode());
        Assert.assertEquals(terms[0].hashCode(), terms[5].getArguments()[0].hashCode());
        Assert.assertNotEquals(terms[0].compareTo(terms[1]), terms[1].compareTo(terms[0]));
    }

    @Test
    public void testTheoryElement() {
        control.add(testTheory);
        control.add("{a; b}.");
        control.add("&a { 1; 2,3: a,b }.");
        control.ground();

        TheoryAtom theoryAtom = control.getTheoryAtoms().getTheoryAtom(0);
        TheoryElement[] elements = theoryAtom.getElements();
        Arrays.sort(elements, Comparator.comparingInt(o -> o.getTerms().length));

        Assert.assertEquals(2, elements.length);
        Assert.assertEquals("1", elements[0].toString());
        Assert.assertEquals("2,3: a,b", elements[1].toString());

        Assert.assertEquals(0, elements[0].getConditions().length);
        Assert.assertEquals(2, elements[1].getConditions().length);
        Assert.assertTrue(Arrays.stream(elements[1].getConditions()).allMatch(literal -> literal >= 1));
        Assert.assertTrue(elements[1].getConditionId() >= 1);

        Assert.assertEquals(elements[0], elements[0]);
        Assert.assertNotEquals(elements[0], elements[1]);
        Assert.assertNotEquals(elements[0].hashCode(), elements[1].hashCode());
        Assert.assertNotEquals(elements[0].compareTo(elements[1]), elements[1].compareTo(elements[0]));
    }

    @Test
    public void testTheoryAtom() {
        control.add(testTheory);
        control.add("&a {}.");
        control.add("&b {} = 1.");
        control.ground();

        List<TheoryAtom> theoryAtoms = new ArrayList<>();
        control.getTheoryAtoms().iterator().forEachRemaining(theoryAtoms::add);
        theoryAtoms.sort(Comparator.comparing(o -> o.getTerm().getName()));

        Assert.assertEquals(2, theoryAtoms.size());
        TheoryAtom a = theoryAtoms.get(0);
        TheoryAtom b = theoryAtoms.get(1);
        Assert.assertEquals("&a{}", a.toString());
        Assert.assertEquals("&b{}=1", b.toString());

        Assert.assertTrue(a.getLiteral() >= 1);
        Assert.assertThrows(NoSuchElementException.class, a::getGuard);
        Assert.assertNotNull(b.getGuard());

        Assert.assertEquals("=", b.getGuard().getConnective());
        Assert.assertEquals("1", b.getGuard().getTheoryTerm().toString());

        Assert.assertEquals(0, a.getElements().length);

        Assert.assertEquals(a, a);
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a.compareTo(b), b.compareTo(a));
    }
}
