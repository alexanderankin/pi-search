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
        PiGenerator piGenerator = new PiGenerator();
        System.out.println(piGenerator.calculate(toGenerate));
    }

}
