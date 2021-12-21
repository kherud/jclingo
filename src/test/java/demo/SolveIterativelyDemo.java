package demo;

import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;

public class SolveIterativelyDemo {

    public static void main(String... args) {
        Control control = new Control("0");

        // add a logic program to the base part
        control.add("a :- not b. b :- not a.");

        // ground the base part
        control.ground();

        try (SolveHandle handle = control.solve(SolveMode.YIELD)) {
            while (handle.hasNext()) {
                // get the next model
                Model model = handle.next();
                System.out.println(model);
            }
        }

        control.close();
    }
}
