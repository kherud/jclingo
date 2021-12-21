package demo;

import org.potassco.clingo.configuration.Configuration;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.statistics.Statistics;
import org.potassco.clingo.statistics.StatisticsMap;

public class StatisticsDemo {

    private final static SolveEventCallback callback = new SolveEventCallback() {
        @Override
        public void onModel(Model model) {
            System.out.println("Model: " + model);
        }
    };

    public static void main(String... args) {
        Control control = new Control("0");

        Configuration configuration = control.getConfiguration();

        configuration.set("stats", "2");

        control.add("a :- not b. b :- not a.");
        control.ground();

        control.solve(callback).wait(-1.);

        Statistics statistics = control.getStatistics();

        // print a json
        System.out.println(statistics);

        printStatistics(statistics, 0);

        control.close();
    }

    private static void printStatistics(Statistics statistics, int depth) {
        switch (statistics.getType()) {
            case VALUE:
                System.out.printf("%s%s\n", " ".repeat(depth), statistics.get());
                break;
            case ARRAY:
                for (int i = 0; i < statistics.size(); i++) {
                    printStatistics(statistics.get(i), depth + 2);
                }
                break;
            case MAP:
                for (String key : ((StatisticsMap) statistics).keys()) {
                    System.out.printf("%s%s\n", " ".repeat(depth), key);
                    printStatistics(statistics.get(key), depth + 2);
                }
                break;
        }
    }

}
