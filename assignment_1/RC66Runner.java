import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RC66Runner {

    private static final int RC6_W = 32;
    private static final int RC6_R = 20;

    public static void usage() {
        System.out.println("Usage: ");
        System.out.println("java RC66Runner input.txt output.txt");
    }

    public static boolean parseRC6Textfile(BufferedReader reader, StringBuilder mode,
                                           StringBuilder text, StringBuilder userkey) throws IOException {
        String line;
        int linenum = 0;
        boolean err = false;

        while ((line = reader.readLine()) != null && !err) {
            switch (linenum) {
                case 0:
                    if (line.startsWith("Encryption") || line.startsWith("Decryption")) {
                        mode.append(line);
                    } else {
                        err = true;
                    }
                    linenum++;
                    break;
                case 1:
                    if (line.startsWith("plaintext: ")) {
                        text.append(line.substring("plaintext: ".length()));
                    } else if (line.startsWith("ciphertext: ")) {
                        text.append(line.substring("ciphertext: ".length()));
                    } else {
                        err = true;
                    }
                    linenum++;
                    break;
                case 2:
                    if (line.startsWith("userkey: ")) {
                        userkey.append(line.substring("userkey: ".length()));
                    } else {
                        err = true;
                    }
                    linenum++;
                    break;
                default:
                    break;
            }
        }

        return !err;
    }

    public static void removeWhitespace(StringBuilder str) {
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                str.setCharAt(j++, str.charAt(i));
            }
        }
        str.delete(j, str.length());
    }

    public static int keyLength(String key) {
        return key.length() / 2;
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Incorrect number of arguments");
            usage();
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];
        StringBuilder mode = new StringBuilder();
        StringBuilder text = new StringBuilder();
        StringBuilder userkey = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            if (!parseRC6Textfile(reader, mode, text, userkey)) {
                System.out.println("Error parsing input file");
                return;
            }

            removeWhitespace(text);
            removeWhitespace(userkey);

            RC66 rc6 = new RC66(RC6_W, RC6_R, keyLength(userkey.toString()));
            String result = rc6.run(mode.toString(), text.toString(), userkey.toString());

            if (mode.toString().startsWith("Encryption")) {
                writer.write("ciphertext: " + result + "\n");
            } else if (mode.toString().startsWith("Decryption")) {
                writer.write("plaintext: " + result + "\n");
            }

        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}