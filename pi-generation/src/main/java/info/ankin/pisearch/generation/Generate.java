package info.ankin.pisearch.generation;

public class Generate {
    public static void main(String[] args) {
        int toGenerate = 10;
        /*
        while (toGenerate-- > 0) {
            int nextInt = 0;
            System.out.println(nextInt);
        }
        */
        System.out.println(calculateLoop(toGenerate));
    }


    /**
     * https://stackoverflow.com/a/46166848
     */
    static double calculateLoop(int k) {
        double result = 0;
        for (int i = 0; i <= k; i++) {
            result = result + doCalc(i);
        }
        return 1 / result;
    }

    static double doCalc(int k) {
        double numerator = Math.pow(-1, k) * factorial(6 * k) * (545140134 * k + 13591409);
        double denominator = factorial(3 * k) * Math.pow(factorial(k), 3) * Math.pow(640320, 3 * k + 3.0 / 2.0);
        return 12.0 * numerator / denominator;
    }

    static double factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }
}
