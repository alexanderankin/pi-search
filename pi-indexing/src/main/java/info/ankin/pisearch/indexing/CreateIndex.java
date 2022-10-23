package info.ankin.pisearch.indexing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateIndex {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("digits/pi-digits.txt");
        System.out.println(Files.readString(path));
    }
}
