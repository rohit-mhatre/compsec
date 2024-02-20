import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RC6 {

    // Your RC6 class implementation

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RC6 input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            String mode = br.readLine();
            if (mode == null) {
                System.out.println("Invalid input file format. Mode is missing.");
                return;
            }

            String text = br.readLine();
            if (text == null) {
                System.out.println("Invalid input file format. Text is missing.");
                return;
            }

            String key = br.readLine();
            if (key == null) {
                System.out.println("Invalid input file format. Key is missing.");
                return;
            }

            // Remove whitespaces from text and key
            text = text.replace("plaintext: ", "").replace("ciphertext: ", "");
            key = key.replace("userkey: ", "")
            text = text.replaceAll("\\s", "");
            key = key.replaceAll("\\s", "");

            // Perform RC6 encryption or decryption based on the mode
            RC7 rc7 = new RC7(32, 20, key.length() / 2); // Adjust parameters accordingly
            String result = "";
            if (mode.equalsIgnoreCase("Encryption")) {
                result = rc7.encrypt(text, key);
                bw.write("Ciphertext: " + result);
            } else if (mode.equalsIgnoreCase("Decryption")) {
                result = rc7.decrypt(text, key);
                bw.write("Plaintext: " + result);
            } else {
                System.out.println("Invalid mode. Please specify 'Encryption' or 'Decryption'.");
                return;
            }
            System.out.println("Result written to " + outputFile);

        } catch (IOException e) {
            System.out.println("Error reading/writing the file: " + e.getMessage());
        }
    }
}
