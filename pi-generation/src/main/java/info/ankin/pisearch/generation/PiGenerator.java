package info.ankin.pisearch.generation;

import ch.obermuhlner.math.big.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;

/**
 * @see <a href="https://stackoverflow.com/a/46166848">original answer on SO</a>
 */
public class PiGenerator {
    /**
     * @see <a href="https://stackoverflow.com/a/3334187">IEEE reference from SO</a>
     */
    private static final int MAX_DECIMALS_IN_FLOAT = 16;
    private static final BigInteger NEGATIVE_ONE = BigInteger.ONE.negate();
    /**
     * Optional cache
     */
    private final NavigableMap<BigInteger, BigInteger> map;
    /**
     * {@link MathContext} which specifies precision for {@link BigDecimal} operations
     */
    private final MathContext mathContext;
    /**
     * Configuration setting for whether {@link #map} is enabled - todo use null instead
     */
    private boolean useCache;

    public PiGenerator() {
        this(new ConcurrentSkipListMap<>(),
                new MathContext(MAX_DECIMALS_IN_FLOAT + 1, RoundingMode.HALF_UP));
    }

    public PiGenerator(int precision) {
        this(new ConcurrentSkipListMap<>(), new MathContext(precision, RoundingMode.HALF_UP));
    }

    public PiGenerator(NavigableMap<BigInteger, BigInteger> map, MathContext mathContext) {
        this.map = map;
        this.mathContext = mathContext;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public PiGenerator setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    public BigDecimal calculate(int k) {
        BigDecimal result = BigDecimal.ZERO;
        for (int i = 0; i <= k; i++) {
            result = result.add(doCalc(i));
        }
        return BigDecimal.ONE.divide(result.multiply(BigDecimal.valueOf(12)), mathContext);
    }

    BigDecimal doCalc(int k) {
        BigInteger numerator = (k % 2 == 0 ? BigInteger.ONE : NEGATIVE_ONE)
                .multiply(factorial(6 * k))
                .multiply(BigInteger.valueOf(545140134L * k + 13591409));
        BigDecimal d =
                new BigDecimal(
                        factorial(BigInteger.valueOf(3).multiply(BigInteger.valueOf(k)).intValue())
                )
                        .multiply(BigDecimalMath.pow(new BigDecimal(factorial(k)),
                                new BigDecimal(BigInteger.valueOf(3)),
                                mathContext))
                        .multiply(BigDecimalMath.pow(new BigDecimal(BigInteger.valueOf(640320)),
                                new BigDecimal(3 * k + 3.0 / 2.0),
                                mathContext), mathContext);

        return
                new BigDecimal(numerator)
                        .divide(d, new MathContext(d.precision(), RoundingMode.HALF_UP));
    }

    /**
     * calculate the factorial of N by delegating to {@link #bigIntFactorial(BigInteger)}
     * <p>
     * This method takes an int so that it is an easier api to work with.
     *
     * @param n the number to multiply by its previous positive numbers
     * @return product of all positive integers less than or equal to the input
     */
    BigInteger factorial(int n) {
        return bigIntFactorial(BigInteger.valueOf(n));
    }

    /**
     * like {@link #factorial(int)} except takes a {@link BigInteger}
     * <p>
     * Because this method may get very large inputs,
     * we can optionally use a {@link Map#computeIfAbsent(Object, Function)} cache.
     *
     * @param n a larger input
     * @return product of all positive integers less than or equal to the input
     */
    BigInteger bigIntFactorial(BigInteger n) {
        if (!isUseCache())
            return doFactorial(n);
        return map.computeIfAbsent(n, this::doFactorial);
    }

    /**
     * recursive function for computing a factorial.
     * It calls {@link #bigIntFactorial(BigInteger)},
     * which means that the {@link Map} used must be safe to concurrently modify.
     *
     * @param n a larger input
     * @return n multiplied by the previous positive number's {@link #bigIntFactorial(BigInteger)}
     */
    BigInteger doFactorial(BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        } else {
            return n.multiply(bigIntFactorial(n.subtract(BigInteger.ONE)));
        }
    }

}
