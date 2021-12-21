package demo;

import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;

public class SolveAsyncDemo {

    private static final String program = "" +
            "#const n = 17." +
            "1 { p(X); q(X) } 1 :- X = 1..n." +
            ":- not n+1 { p(1..n); q(1..n) }.";

    public static void main(String... args) {
        Control control = new Control("0");

        // add a logic program to the base part
        control.add(program);

        // ground the base part
        control.ground();

        SolveEventCallback callback = new SolveEventCallback() {
            @Override
            public void onModel(Model model) {
                System.out.println("Model: " + model);
            }
        };

        // solve using a model callback
        SolveHandle handle = control.solve(callback, SolveMode.ASYNC);

        // // let's approximate pi
        double x, y;
        int samples = 0;
        int incircle = 0;
        while (!handle.wait(1e-9)) {
            samples++;
            x = Math.random();
            y = Math.random();
            if (x * x + y * y <= 1.) incircle++;
        }

        System.out.printf("pi = %g\n", 4.0 * incircle / samples);

        control.close();
    }
}
