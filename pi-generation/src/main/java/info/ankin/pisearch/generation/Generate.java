package info.ankin.pisearch.generation;

public class Generate {
    public static void main(String[] args) {
        int precision = args.length > 0 ? Integer.parseInt(args[0], 10) : 20;
        int toGenerate = args.length > 1 ? Integer.parseInt(args[1], 10) : 10;
        boolean useCache = args.length <= 2 || Boolean.parseBoolean(args[2]);
        System.err.println("printing PI with " + precision + " precision, after " + toGenerate + " iterations, useCache: " + useCache);
        PiGenerator piGenerator = new PiGenerator(precision)
                .setUseCache(useCache);
        System.out.println(piGenerator.calculate(toGenerate));
    }

}
