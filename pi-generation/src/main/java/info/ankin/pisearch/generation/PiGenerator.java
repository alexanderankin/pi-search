package info.ankin.pisearch.generation;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @see <a href="https://stackoverflow.com/a/46166848">original answer on SO</a>
 */
public class PiGenerator {
    private final NavigableMap<Integer, Integer> map = new ConcurrentSkipListMap<>();

    public double calculate(int k) {
        double result = 0;
        for (int i = 0; i <= k; i++) {
            result = result + doCalc(i);
        }
        return 1 / (result * 12);
    }

    double doCalc(int k) {
        double numerator = (k % 2 == 0 ? 1 : -1) * (double) factorial(6 * k) * (545140134 * k + 13591409);
        double denominator = factorial(3 * k) * Math.pow(factorial(k), 3) * Math.pow(640320, 3 * k + 3.0 / 2.0);
        return numerator / denominator;
    }

    int factorial(int n) {
        return intFactorial(n);
    }

    int intFactorial(int n) {
        return map.computeIfAbsent(n, this::doFactorial);
    }

    int doFactorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * intFactorial(n - 1);
        }
    }

}
