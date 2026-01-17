import com.example.boggle_solver.Boggle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {
    public static void main(String[] args) {
        String content = "Hello, world!\nThis is a new line.";
        Path file = Paths.get("output.txt");

        String board = "EDUUHEIOFTTSRBRMENNOEHIER";
        board = board.toUpperCase();
        Boggle boggle = new Boggle(board, 3);
        String[] boggleWords = boggle.solveBoggle(dictionary);

        System.out.println(boggleWords.toString());

        try {
            // Write the content to the file. This overwrites the file if it exists.
            Files.write(file, content.getBytes());
            System.out.println("Data written to output.txt");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
