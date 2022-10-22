package info.ankin.pisearch.generation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PiGeneratorTest {

    @Test
    void test_tenIterations() {
        Object result = new PiGenerator().calculate(10);
        System.out.println(result);

        assertEquals("3.1415926535897936", String.valueOf(result));
    }

}
