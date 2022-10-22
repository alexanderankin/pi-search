package info.ankin.pisearch.generation;

import org.junit.jupiter.api.Test;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PiGeneratorTest {

    @Test
    void test_tenIterations() {
        Object result = new PiGenerator(MathContext.DECIMAL128).calculate(10);
        System.out.println(result);

        assertEquals(String.valueOf(result).substring(0, 17), "3.141592653589793");
    }

}
