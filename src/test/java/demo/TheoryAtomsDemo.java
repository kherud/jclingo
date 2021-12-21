package demo;

import org.potassco.clingo.backend.Backend;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.theory.TheoryAtom;
import org.potassco.clingo.theory.TheoryAtoms;

import java.util.NoSuchElementException;

public class TheoryAtomsDemo {

    private static final String theory = "" +
            "#theory t {\n" +
            "      term   { + : 1, binary, left };\n" +
            "      &a/0 : term, any;\n" +
            "      &b/1 : term, {=}, term, any\n" +
            "}.\n" +
            "x :- &a { 1+2 }.\n" +
            "y :- &b(3) { } = 17.";

    private static final SolveEventCallback onModel = new SolveEventCallback() {
        @Override
        public void onModel(Model model) {
            System.out.println(model);
        }
    };

    public static void main(String... args) {
        Control control = new Control("0");
        control.add(theory);
        control.ground();
        TheoryAtoms theoryAtoms = control.getTheoryAtoms();
        System.out.printf("number of grounded theory atoms: %d\n", theoryAtoms.size());

        int literal = 0;
        // verify that theory atom b has a guard
        for (TheoryAtom theoryAtom : theoryAtoms) {
            if (!theoryAtom.getTerm().getName().equals("b"))
                continue;
            // get the literal associated with the theory atom
            literal = theoryAtom.getLiteral();
            try {
                System.out.println("theory atom b/1 has a guard: " + theoryAtom.getGuard());
            } catch (NoSuchElementException e) {
                System.out.println("theory atom b/1 has no guard");
            }
            break;
        }

        // use the backend to assume that the theory atom is true
        // (note that only symbolic literals can be passed as assumptions to a solve call;
        // the backend accepts any aspif literal)
        assert literal != 0;
        // get the backend
        try (Backend backend = control.getBackend()) {
            // add the assumption
            backend.addAssume(new int[]{literal});
        }

        // solve using a model callback
        control.solve(onModel).wait(-1.);

        control.close();
    }
}
