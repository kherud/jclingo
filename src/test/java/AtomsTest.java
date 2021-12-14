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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class AtomsTest {

    private Control control;

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

        SymbolicAtom p1 = symbolicAtoms.getSymbolicAtom(new Function("p", new Number(1)));
        Assert.assertNotNull(p1);
        Assert.assertTrue(p1.isFact());
        Assert.assertFalse(p1.isExternal());
        Assert.assertTrue(p1.getLiteral() >= 1);
        Assert.assertEquals(p1.getSymbol(), new Function("p", new Number(1)));
        Assert.assertTrue(p1.match(new Signature("p", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 2, true)));
        Assert.assertFalse(p1.match(new Signature("b", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 1, false)));

        SymbolicAtom p2 = symbolicAtoms.getSymbolicAtom(new Function("p", new Number(2)));
        Assert.assertNotNull(p1);
        Assert.assertFalse(p1.isFact());
        Assert.assertFalse(p1.isExternal());
        Assert.assertTrue(p1.getLiteral() >= 2);
        Assert.assertEquals(p1.getSymbol(), new Function("p", new Number(2)));
        Assert.assertTrue(p1.match(new Signature("p", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 2, true)));
        Assert.assertFalse(p1.match(new Signature("b", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 1, false)));

        SymbolicAtom p3 = symbolicAtoms.getSymbolicAtom(new Function("p", new Number(1)));
        Assert.assertNotNull(p1);
        Assert.assertFalse(p1.isFact());
        Assert.assertTrue(p1.isExternal());
        Assert.assertTrue(p1.getLiteral() >= 3);
        Assert.assertEquals(p1.getSymbol(), new Function("p", new Number(3)));
        Assert.assertTrue(p1.match(new Signature("p", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 2, true)));
        Assert.assertFalse(p1.match(new Signature("b", 1, true)));
        Assert.assertFalse(p1.match(new Signature("p", 1, false)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testSymbolicAtomNonExisting() {
        control.add("p(1). {p(2)}. #external p(3).");
        control.ground();

        SymbolicAtoms symbolicAtoms = control.getSymbolicAtoms();

        symbolicAtoms.getSymbolicAtom(new Function("p", new Number(1)));
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
}
