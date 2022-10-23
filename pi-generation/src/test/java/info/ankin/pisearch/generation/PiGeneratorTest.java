package info.ankin.pisearch.generation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PiGeneratorTest {

    /**
     * <code>
     * for i in {1..100}; do ./gradlew :pi-generation:cleanTest :pi-generation:test --tests info.ankin.pisearch.generation.PiGeneratorTest.test_tenIterations -q && echo passed || break; done;
     * </code>
     */
    @Test
    void test_tenIterations() {
        Object result = new PiGenerator().calculate(10);
        System.out.println(result);

        assertEquals("3.1415926535897932", String.valueOf(result));
    }

    @Test
    void test_tenLines() {
        logMemoryUsage();
        String digits =
                "3.14159265358979323846264338327950288419716939937510" +
                        "58209749445923078164062862089986280348253421170679" +
                        "82148086513282306647093844609550582231725359408128" +
                        "48111745028410270193852110555964462294895493038196" +
                        "44288109756659334461284756482337867831652712019091" +
                        "45648566923460348610454326648213393607260249141273" +
                        "72458700660631558817488152092096282925409171536436" +
                        "78925903600113305305488204665213841469519415116094" +
                        "33057270365759591953092186117381932611793105118548" +
                        "07446237996274956735188575272489122793818301194912";
        PiGenerator piGenerator = new PiGenerator(digits.length() - 1).setUseCache(false);

        String myDigits = "";
        int iteration;
        for (iteration = 1; !myDigits.equals(digits); iteration++) {
            myDigits = String.valueOf(piGenerator.calculate(iteration));
            // System.out.println("doing iteration " + iteration + " and got " + myDigits.length() + " but needed: " + digits.length() + ". was: " + myDigits);

            if (myDigits.length() == digits.length()) {
                int similar = commonPrefixLength(digits, myDigits);
                // System.out.println("right length, number similar: " + similar);

                if (similar == digits.length() - 1) {
                    break;
                }
            }

            if (iteration > 40) fail("taking too many iterations");
        }

        // System.out.println("took " + iteration + " iterations to get to " + (digits.length() - 2) + " digits of pi");
        logMemoryUsage();

        // we expect it to take us this many iterations
        assertTrue(iteration > 30 && iteration <= 40, "iteration was supposed to be almost 40, was: " + iteration);
        System.out.println("took " + iteration + " iterations");
    }

    private void logMemoryUsage() {
        System.out.printf("used %.3fMB%n", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_000_000.0);
    }

    private int commonPrefixLength(String expected, String actual) {
        int i;
        for (i = 0; i < expected.length(); i++) {
            if (expected.charAt(i) != actual.charAt(i)) {
                return i;
            }
        }
        return i;
    }

}
