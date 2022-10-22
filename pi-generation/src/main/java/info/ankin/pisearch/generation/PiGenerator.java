package info.ankin.pisearch.generation;

import ch.obermuhlner.math.big.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @see <a href="https://stackoverflow.com/a/46166848">original answer on SO</a>
 */
public class PiGenerator {
    /**
     * @see <a href="https://stackoverflow.com/a/3334187">IEEE reference from SO</a>
     */
    private static final int MAX_DECIMALS_IN_FLOAT = 16;
    private static final BigInteger NEGATIVE_ONE = BigInteger.ONE.negate();
    private final NavigableMap<Integer, Integer> map;
    private final MathContext mathContext;

    public PiGenerator() {
        this(new ConcurrentSkipListMap<>(),
                new MathContext(MAX_DECIMALS_IN_FLOAT + 1, RoundingMode.HALF_UP));
    }

    public PiGenerator(int precision) {
        this(new ConcurrentSkipListMap<>(), new MathContext(precision, RoundingMode.HALF_UP));
    }

    public PiGenerator(NavigableMap<Integer, Integer> map, MathContext mathContext) {
        this.map = map;
        this.mathContext = mathContext;
    }

    public double calculate(int k) {
        double result = 0;
        for (int i = 0; i <= k; i++) {
            result = result + doCalc(i);
        }
        return 1 / (result * 12);
    }

    double doCalc(int k) {
        BigInteger numerator = (k % 2 == 0 ? BigInteger.ONE : NEGATIVE_ONE)
                .multiply(BigInteger.valueOf(factorial(6 * k)))
                .multiply(BigInteger.valueOf((545140134L * k + 13591409)));
        BigDecimal d =
                new BigDecimal(
                        factorial(BigInteger.valueOf(3).multiply(BigInteger.valueOf(k)).intValue())
                )
                        .multiply(BigDecimalMath.pow(new BigDecimal(BigInteger.valueOf(factorial(k))),
                                new BigDecimal(BigInteger.valueOf(3)),
                                mathContext))
                        .multiply(BigDecimalMath.pow(new BigDecimal(BigInteger.valueOf(640320)),
                                new BigDecimal(3 * k + 3.0 / 2.0),
                                mathContext), mathContext);

        //noinspection ConstantConditions
        if (2 < 1) {
            double denominator = factorial(3 * k) * Math.pow(factorial(k), 3) * Math.pow(640320, 3 * k + 3.0 / 2.0);
            if (d.doubleValue() != denominator) {
                String plainDiff = new BigDecimal(d.doubleValue() - denominator).toPlainString();
                String mantissa = mant(d.doubleValue()) + " vs " + mant(denominator);

                String mantissaDiff = BigDecimal.valueOf(mant(d.doubleValue()) - mant(denominator)).toPlainString();
                System.out.println("nope: " + mantissaDiff + ", or: " + mantissa + "(" + plainDiff.substring(0, Math.min(100, plainDiff.length() - 1)) + ")");
            }
        }

        return
                new BigDecimal(numerator)
                        // .divide(BigDecimal.valueOf(denominator), new MathContext(BigDecimal.valueOf(denominator).precision(), RoundingMode.HALF_UP)).doubleValue();
                        .divide(d, new MathContext(d.precision(), RoundingMode.HALF_UP)).doubleValue();
    }

    private double mant(double denominator) {
        return denominator / Math.pow(2, Math.getExponent(denominator));
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
