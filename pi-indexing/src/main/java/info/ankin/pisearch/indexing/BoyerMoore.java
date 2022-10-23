package info.ankin.pisearch.indexing;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * <pre>
 *  Compilation:  javac BoyerMoore.java
 *  Execution:    java BoyerMoore pattern text
 *  Dependencies: StdOut.java
 *  </pre>
 * <p>
 * Reads in two strings, the pattern and the input text, and
 * searches for the pattern in the input text using the
 * bad-character rule part of the Boyer-Moore algorithm.
 * (does not implement the strong good suffix rule)
 * <p>
 * <pre>
 *  % java BoyerMoore abracadabra abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:               abracadabra
 *  </pre>
 * <p>
 * <pre>
 *  % java BoyerMoore rab abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:         rab
 *  </pre>
 * <p>
 * <pre>
 *  % java BoyerMoore bcara abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                                   bcara
 *  </pre>
 * <p>
 * <pre>
 *  % java BoyerMoore rabrabracad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                        rabrabracad
 *  </pre>
 * <p>
 * <pre>
 *  % java BoyerMoore abacad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern: abacad
 *  </pre>
 * <p>
 * The {@code BoyerMoore} class finds the first occurrence of a pattern string
 * in a text string.
 * <p>
 * This implementation uses the Boyer-Moore algorithm (with the bad-character
 * rule, but not the strong good suffix rule).
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/53substring">Section 5.3</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class BoyerMoore {
    /**
     * the radix
     */
    private final int R;
    /**
     * the bad-character skip array
     */
    private final int[] right;

    /**
     * store the pattern as a character array
     */
    private char[] pattern;
    /**
     * or as a string
     */
    private String pat;

    /**
     * Preprocesses the pattern string.
     *
     * @param pat the pattern string
     */
    public BoyerMoore(String pat) {
        this.R = 256;
        this.pat = pat;

        // position of rightmost occurrence of c in the pattern
        right = new int[R];
        for (int c = 0; c < R; c++)
            right[c] = -1;
        for (int j = 0; j < pat.length(); j++)
            right[pat.charAt(j)] = j;
    }

    /**
     * Preprocesses the pattern string.
     *
     * @param pattern the pattern string
     * @param R       the alphabet size
     */
    public BoyerMoore(char[] pattern, int R) {
        this.R = R;
        this.pattern = new char[pattern.length];
        System.arraycopy(pattern, 0, this.pattern, 0, pattern.length);

        // position of rightmost occurrence of c in the pattern
        right = new int[R];
        for (int c = 0; c < R; c++)
            right[c] = -1;
        for (int j = 0; j < pattern.length; j++)
            right[pattern[j]] = j;
    }

    /**
     * Takes a pattern string and an input string as command-line arguments;
     * searches for the pattern string in the text string; and prints
     * the first occurrence of the pattern string in the text string.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String pat = args.length > 0 ? args[0] : "somehow";
        String txt = args.length > 1 ? args[1] : "abc sometimes i think somehow this is a test";
        // char[] pattern = pat.toCharArray();
        // char[] text = txt.toCharArray();

        BoyerMoore boyermoore1 = new BoyerMoore(pat);
        // BoyerMoore boyermoore2 = new BoyerMoore(pattern, 256);
        int offset1 = boyermoore1.search(txt);
        // int offset2 = boyermoore2.search(text);

        // print results
        System.out.println("text:    " + txt);

        System.out.print("pattern: ");
        for (int i = 0; i < offset1; i++)
            System.out.print(" ");
        System.out.println(pat + " at " + offset1);

        // System.out.print("pattern: ");
        // for (int i = 0; i < offset2; i++)
        //     System.out.print(" ");
        // System.out.println(pat + " at " + offset2);
    }

    /**
     * Returns the index of the first occurrence of the pattern string
     * in the text string.
     *
     * @param txt the text string
     * @return the index of the first occurrence of the pattern string
     * in the text string; n if no such match
     */
    public int search(String txt) {
        int m = pat.length();
        int n = txt.length();
        int skip;
        for (int i = 0; i <= n - m; i += skip) {
            skip = 0;
            for (int j = m - 1; j >= 0; j--) {
                if (pat.charAt(j) != txt.charAt(i + j)) {
                    skip = Math.max(1, j - right[txt.charAt(i + j)]);
                    break;
                }
            }
            // found
            if (skip == 0) return i;
        }
        // not found
        return n;
    }

    /**
     * Returns the index of the first occurrence of the pattern string
     * in the text string.
     *
     * @param text the text string
     * @return the index of the first occurrence of the pattern string
     * in the text string; n if no such match
     */
    public int search(char[] text) {
        int m = pattern.length;
        int n = text.length;
        int skip;
        for (int i = 0; i <= n - m; i += skip) {
            skip = 0;
            for (int j = m - 1; j >= 0; j--) {
                if (pattern[j] != text[i + j]) {
                    skip = Math.max(1, j - right[text[i + j]]);
                    break;
                }
            }
            // found
            if (skip == 0) return i;
        }
        // not found
        return n;
    }

    //<editor-fold desc="Initial take of FS Algo">
    /**
     * java equivalent of page 7 of {@code 10.1007/3-540-44867-5_4}
     */
    int fspMethod(String text) {
        int start = 0;
        int m = pat.length();
        for (int i = m - 1; i >= 0; i--) {
            if (pat.charAt(i) != text.charAt(start + i)) {
                if (i == m -1 ) {
                    return hbcp(text.charAt(start + m - 1));
                } else {
                    return gsp(i);
                }
            }

            System.out.println();
        }
        return 0;
    }

    // bad character
    private int hbcp(char charAt) {
        return 0;
    }

    // good suffix
    private int gsp(int i) {
        return 0;
    }
    //</editor-fold>

    //<editor-fold desc="Take 2 of FS Algo">
    void fspFunction(String pattern, String text) {
        int n = text.length();
        int m = pattern.length();
        String tPrime = text + pattern;
        Function<Character, Integer> badChars = compute_badChars(pattern);
        Function<Integer, Integer> goodSuffix = compute_goodSuffix(pattern);
        AtomicInteger s = new AtomicInteger();

        Runnable whileBadChars = () -> {
            while (badChars.apply(tPrime.charAt(s.get() + m - 1))> 0) {
                s.set(s.get() + badChars.apply(tPrime.charAt(s.get() + m - 1)));
            }
        };

        whileBadChars.run();

        while (s.get() <= (n - m)) {
            int j = m - 2;
            while (j >= 0 && pattern.charAt(j) == tPrime.charAt(s.get() + j)) j -= 1;
            if (j < 0) System.out.println(s.get());
            s.set(s.get() + goodSuffix.apply(j + 1));

            whileBadChars.run();
        }
    }

    //<editor-fold desc="TODO figure out what these do">
    private Function<Integer, Integer> compute_goodSuffix(String pattern) {
        return character -> {
            return 0;
        };
    }

    private Function<Character, Integer> compute_badChars(String pattern) {
        return character -> {
            return 0;
        };
    }
    //</editor-fold>
    //</editor-fold>

}
