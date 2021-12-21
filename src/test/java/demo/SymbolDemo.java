package demo;

import org.potassco.clingo.symbol.Function;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;

public class SymbolDemo {

    public static void main(String... args) {
        // create a number, identifier (function without arguments), and a function symbol
        Number number = new Number(42);
        Function id = new Function("x");
        Function function = new Function("x", number, id);

        Symbol[] symbols = new Symbol[]{number, id, function};

        // print the symbols along with their hash values
        for (Symbol symbol : symbols) {
            System.out.printf("The hash of %s is %d\n", symbol.toString(), symbol.hash());
        }

        Symbol[] arguments = function.getArguments();
        assert arguments.length == 2;

        for (int i = 0; i < arguments.length; i++) {
            // equal to comparison
            System.out.printf("%s is %s to %s\n", symbols[i], symbols[i].equals(arguments[i]) ? "equal" : "not equal", arguments[i]);

            for (int j = 0; j < arguments.length; j++) {
                // compare to comparison
                int c = symbols[i].compareTo(arguments[j]);
                System.out.printf("%s is %s %s\n", symbols[i], c == 0 ? "equal to" : c > 0 ? "greater than" : "less than", arguments[j]);
            }
        }
    }
}
