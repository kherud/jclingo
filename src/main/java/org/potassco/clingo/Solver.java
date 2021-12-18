package org.potassco.clingo;

import org.potassco.clingo.configuration.args.Option;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.ModelType;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.symbol.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver {

    public List<AnswerSet> solve(String encoding, String instances, Option... options) {
        return solve(instances + encoding, options);
    }

    public List<AnswerSet> solve(String program, Option... options) {
        Control control = new Control();
        control.getConfiguration().set(options);
        control.add(program);
        control.ground();
        List<AnswerSet> answers = new ArrayList<>();
        try (SolveHandle solveHandle = control.solve(new int[0], null, SolveMode.YIELD)) {
            while (solveHandle.hasNext()) {
                Model model = solveHandle.next();
                Symbol[] symbols = model.getSymbols();
                ModelType type = model.getType();
                int[] cost = model.getCost();
                AnswerSet answer = new AnswerSet(Arrays.asList(symbols), type, cost);
                answers.add(answer);
            }
        }
        control.close();
        return answers;
    }
}
