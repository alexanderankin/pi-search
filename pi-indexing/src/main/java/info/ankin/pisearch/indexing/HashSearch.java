package info.ankin.pisearch.indexing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * potentially, if this suffices, then maybe complicated algorithms are not necessary
 */
public class HashSearch {
    public static void main(String[] args) throws Throwable {
        // basicDemo();
        piDemo();
    }

    public static void piDemo() throws IOException {
        Searcher searcher = new Searcher(Files.readString(Paths.get("digits/pi-digits.txt"), StandardCharsets.UTF_8));
        searcher.initHashesForLength(4);
        String search = "1237";
        int result = searcher.search(search);
        System.out.println(result);

        String t = searcher.getText();
        String around = t.substring(result - 10, result) +
                " " +
                search +
                " " +
                t.substring(result + search.length(),
                        result + search.length() + 10);

        System.out.println("match for " + search + " around: " + around);
    }

    @SuppressWarnings("unused")
    public static void basicDemo() {
        Searcher searcher = new Searcher("abc def ghi xyz");

        searcher.initHashesForLength(3);

        int result = searcher.search("ghi");

        System.out.println(result);
    }

    public static abstract class IndexRepository {
        public abstract List<Integer> matchPositions(int hashCode, int length);

        // consider passing reference type not multiple primitives
        public abstract void addHash(int hashCode, int length, int position);

        public abstract boolean isLengthIndexed(int length);

        public abstract List<Integer> clearHashes(int length);
    }

    public static class InMemoryIndexRepository extends IndexRepository {
        // consider if key-ing map by hash is sufficient
        final Map<Integer, List<Integer>> hashes = new HashMap<>();
        final Set<Integer> lengths = new HashSet<>();

        @Override
        public List<Integer> matchPositions(int hashCode, int length) {
            return hashes.get(hashCode);
        }

        @Override
        public void addHash(int hashCode, int length, int position) {
            hashes.computeIfAbsent(hashCode, k -> new ArrayList<>()).add(position);
            // todo optimize
            lengths.add(length);
        }

        @Override
        public boolean isLengthIndexed(int length) {
            return lengths.contains(length);
        }

        @Override
        public List<Integer> clearHashes(int length) {
            return null;
        }
    }

    @SuppressWarnings("unused") // todo
    public static abstract class DatabaseIndexRepository extends IndexRepository {
    }

    public static class Searcher {
        final IndexRepository indexRepository = new InMemoryIndexRepository();
        final String text;

        public Searcher(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Searcher initHashesForLength(int length) {
            for (int i = 0; i < text.length() - length + 1; i++) {
                String substring = text.substring(i, i + length);
                indexRepository.addHash(substring.hashCode(), length, i);
            }
            return this;
        }

        public int search(String search) {
            if (!indexRepository.isLengthIndexed(search.length())) initHashesForLength(search.length());
            return indexRepository.matchPositions(search.hashCode(), search.length()).get(0);
        }
    }
}
