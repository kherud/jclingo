package demo;

import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.control.SymbolicAtoms;

public class SymbolicAtomsDemo {

    public static void main(String... args) {
        Control control = new Control("0");
        control.add("a. {b}. #external c.");
        control.ground();

        // get symbolic atoms
        SymbolicAtoms symbolicAtoms = control.getSymbolicAtoms();

        System.out.println("Symbolic atoms:");

        for (SymbolicAtom symbolicAtom : symbolicAtoms) {
            System.out.print("  " + symbolicAtom);

            // determine if the atom is fact or external
            if (symbolicAtom.isFact())
                System.out.print(", fact");
            if (symbolicAtom.isExternal())
                System.out.print(", external");
            System.out.println();
        }

        control.close();
    }
}
