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
    private static final BigDecimal NEG_ONE = BigDecimal.ONE.negate();
    private static final BigDecimal BD_3 = new BigDecimal(3);
    private static final BigDecimal BD_6 = new BigDecimal(6);
    private static final BigDecimal BD_12 = new BigDecimal(12);
    private static final BigDecimal BD_3_Over_2 = new BigDecimal("3.0").divide(new BigDecimal(2), RoundingMode.UNNECESSARY);
    private static final BigDecimal CH_CONST_1 = new BigDecimal(545140134);
    private static final BigDecimal CH_CONST_2 = new BigDecimal(13591409);
    private static final Raiseable CH_CONST_3 = new Raiseable(new BigDecimal(640320));
    private final NavigableMap<BigInteger, BigInteger> map = new ConcurrentSkipListMap<>();
    private final int precision;
    private final MathContext mathContext;

    public PiGenerator() {
        this(1074); // default double precision
    }

    public PiGenerator(int precision) {
        this(new MathContext(precision, RoundingMode.HALF_UP));
    }

    public PiGenerator(MathContext mathContext) {
        this.precision = mathContext.getPrecision();
        this.mathContext = mathContext;
    }

    public BigDecimal calculate(int k) {
        BigDecimal result = new BigDecimal(0);
        for (int i = 0; i <= k; i++) {
            result = result.add(doCalc(i));
        }
        return BigDecimal.ONE.divide(result.multiply(BD_12), mathContext);
    }

    BigDecimal doCalc(int k) {
        BigDecimal kBd = new BigDecimal(k);
        BigDecimal numerator =
        NEG_ONE.pow(k)
                .multiply(factorial(6 * k))
                .multiply(CH_CONST_1.multiply(kBd).add(CH_CONST_2));
                ;

        BigDecimal denominator = factorial(3 * k)
                .multiply(factorial(k)).pow(3)
                .multiply(CH_CONST_3.pow(kBd.multiply(BD_3).add(BD_3_Over_2), mathContext));
                // Math.pow(640320, 3 * k + 3.0 / 2.0);

        return numerator.divide(denominator, mathContext);
    }

    BigDecimal factorial(int n) {
        return new BigDecimal(intFactorial(BigInteger.valueOf(n)));
    }

    BigInteger intFactorial(BigInteger n) {
        return map.computeIfAbsent(n, this::doFactorial);
    }

    BigInteger doFactorial(BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        } else {
            return n.multiply(intFactorial(n.subtract(BigInteger.ONE)));
        }
    }

    public static class Raiseable {
        private final BigDecimal base;

        public Raiseable(BigDecimal base) {
            this.base = base;
        }

        public BigDecimal getBase() {
            return base;
        }

        public BigDecimal pow(BigDecimal exponent, MathContext mathContext) {
            return BigDecimalMath.pow(base, exponent, mathContext);
        }
    }

}
