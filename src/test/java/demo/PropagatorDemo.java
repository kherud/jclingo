package demo;

import org.potassco.clingo.control.Control;
import org.potassco.clingo.propagator.PropagateInit;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveEventCallback;

public class PropagatorDemo implements Propagator {

    @Override
    public void init(PropagateInit init) {
        Propagator.super.init(init);
    }

    public static void main(String... args) {
        SolveEventCallback callback = new SolveEventCallback() {
            @Override
            public void onModel(Model model) {
                System.out.println(model);
            }
        };

        Control control = new Control("0");

        Propagator propagator = new PropagatorDemo();

        control.registerPropagator(propagator);

        control.add("pigeon", "1 { place(P,H) : H = 1..h } 1 :- P = 1..p.");
        control.ground("pigeon");
        control.solve(callback);

        control.close();
    }
}
