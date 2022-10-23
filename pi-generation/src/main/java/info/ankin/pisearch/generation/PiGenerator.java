package info.ankin.pisearch.generation;

import ch.obermuhlner.math.big.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @see <a href="https://stackoverflow.com/a/46166848">original answer on SO</a>
 */
public class PiGenerator {
    /**
     * This can/should be obscenely large as it will (should) be made up of idle threads
     */
    private static final ForkJoinPool FJP = new ForkJoinPool(500);
    /**
     * @see <a href="https://stackoverflow.com/a/3334187">IEEE reference from SO</a>
     */
    private static final int MAX_DECIMALS_IN_FLOAT = 16;
    private static final BigInteger NEGATIVE_ONE = BigInteger.ONE.negate();
    private static final BigInteger SIX = BigInteger.valueOf(6);
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

    private static <T> CompletableFuture<T> toFjpCf(Future<T> future) {
        return CompletableFuture.supplyAsync(ThrowingCallable.supplier(future::get), FJP);
    }

    public boolean isUseCache() {
        return useCache;
    }

    public PiGenerator setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    public BigDecimal calculate(int k) {
        AtomicReference<BigDecimal> atomicReference = new AtomicReference<>(BigDecimal.ZERO);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);
        CountDownLatch countDownLatch = new CountDownLatch(k);
        for (int i = 0; i <= k; i++) {
            doCalc(i, executorService)
                    .thenAccept(b -> {
                        synchronized (atomicReference) {
                            atomicReference.updateAndGet(b::add);
                        }
                    })
                    .thenRun(countDownLatch::countDown);
        }

        await(countDownLatch);
        executorService.shutdown();
        return BigDecimal.ONE.divide(atomicReference.get().multiply(BigDecimal.valueOf(12)), mathContext);
    }

    // basically, @lombok.SneakyThrows
    private void await(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    CompletableFuture<BigDecimal> doCalc(int k, ExecutorService executorService) {
        BigInteger kBigInt = BigInteger.valueOf(k);
        CompletableFuture<BigInteger> numerator = toFjpCf(executorService.submit(() -> (k % 2 == 0 ? BigInteger.ONE : NEGATIVE_ONE)
                .multiply(factorial(kBigInt.multiply(SIX)))
                .multiply(BigInteger.valueOf(545140134L * k + 13591409))
        ));

        CompletableFuture<BigDecimal> m1 = toFjpCf(executorService.submit(() -> BigDecimalMath.pow(new BigDecimal(factorial(kBigInt)),
                new BigDecimal(BigInteger.valueOf(3)),
                mathContext)));

        CompletableFuture<BigDecimal> m2 = toFjpCf(executorService.submit(() -> BigDecimalMath.pow(new BigDecimal(BigInteger.valueOf(640320)),
                new BigDecimal(3 * k + 3.0 / 2.0),
                mathContext)));

        return CompletableFuture.allOf(numerator, m1, m2)
                .toCompletableFuture()
                .thenApplyAsync(v -> {
        BigDecimal d =
                new BigDecimal(
                        factorial(BigInteger.valueOf(3).multiply(kBigInt))
                )
                        .multiply(m1.join())
                        .multiply(m2.join(), mathContext);

        return
                new BigDecimal(numerator.join())
                        .divide(d, mathContext);
                }, executorService);
    }

    /**
     * calculate the factorial of N
     *
     * @param n the number to multiply by its previous positive numbers
     * @return product of all positive integers less than or equal to the input
     */
    BigInteger factorial(BigInteger n) {
        if (!isUseCache())
            return doFactorial(n);
        return map.computeIfAbsent(n, this::doFactorial);
    }

    /**
     * recursive function for computing a factorial.
     * It calls {@link #factorial(BigInteger)},
     * which means that the {@link Map} used must be safe to concurrently modify.
     *
     * @param n a larger input
     * @return n multiplied by the previous positive number's {@link #factorial(BigInteger)}
     */
    BigInteger doFactorial(BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        } else {
            return n.multiply(factorial(n.subtract(BigInteger.ONE)));
        }
    }

    interface ThrowingCallable<T> {
        static <T> Supplier<T> supplier(ThrowingCallable<T> tc) {
            return tc::callUnsafe;
        }

        T call() throws Throwable;

        default T callUnsafe() {
            try {
                return call();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
