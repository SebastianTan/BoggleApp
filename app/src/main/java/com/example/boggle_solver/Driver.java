import com.example.boggle_solver.Boggle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//read from file
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Driver {
	public static String[] ReadWithScanner() {
		String[] out;
		ArrayList<String> list = new ArrayList<>();
		try {
			// Create a File object
			File myObj = new File("../scrabble.txt");
			// Create a Scanner object for the file
			Scanner myReader = new Scanner(myObj);

			// Read line by line until end of file
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				list.add(data);
				//System.out.println(data);
			}

			// Close the scanner
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		out = list.toArray(new String[0]);
		return out;
	}
    public static void main(String[] args) {
        String content = "Hello, world!\nThis is a new line.";
        Path file = Paths.get("output.txt");
		String[] dictionary=ReadWithScanner();



        String board = "EDUUHEIOFTTSRBRMENNOEHIER";
        board = board.toUpperCase();
        Boggle boggle = new Boggle(board, 3);
        String[] boggleWords = boggle.solveBoggle(dictionary);

		for (int i = 0; i < 10; i++) {
			System.out.println(boggleWords[i]);
		}
		content=String.join("\n", boggleWords);
	

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
