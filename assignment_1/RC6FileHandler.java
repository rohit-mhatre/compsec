// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;

// public class RC6FileHandler {
//     public static void main(String[] args) {
//         if (args.length != 2) {
//             System.out.println("Usage: java RC6FileHandler <input_file> <output_file>");
//             System.exit(1);
//         }

//         String inputFilename = args[0];
//         String outputFilename = args[1];

//         try (BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
//              BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {

//             String operation = reader.readLine();
//             if (operation == null || (!operation.startsWith("Decryption") && !operation.startsWith("Encryption"))) {
//                 System.out.println("Error: Invalid operation specified in the input file.");
//                 System.exit(1);
//             }

//             String dataLine = reader.readLine();
//             if (dataLine == null || !dataLine.startsWith(operation.startsWith("Decryption") ? "ciphertext:" : "plaintext:")) {
//                 System.out.println("Error: Data line not found or invalid.");
//                 System.exit(1);
//             }

//             String userkeyLine = reader.readLine();
//             if (userkeyLine == null || !userkeyLine.startsWith("userkey:")) {
//                 System.out.println("Error: User key line not found or invalid.");
//                 System.exit(1);
//             }

//             String[] dataArray = dataLine.substring(dataLine.indexOf(":") + 1).trim().split(" ");
//             byte[] data = new byte[dataArray.length];
//             for (int i = 0; i < dataArray.length; i++) {
//                 data[i] = (byte) Integer.parseInt(dataArray[i], 16);
//             }

//             String key = userkeyLine.substring(userkeyLine.indexOf(":") + 1).trim();

//             int W = 32; // Word size in bits
//             int R = 20; // Number of rounds
//             int B = key.length() / 2; // Key size in bytes

//             RC6 rc6 = new RC6(W, R, B);
//             rc6.keySchedule(key);

//             byte[] result;
//             String resultString;
//             if (operation.startsWith("Decryption")) {
//                 resultString = rc6.decrypt(new String(data));
//             } else {
//                 resultString = rc6.encrypt(new String(data));
//             }
//             result = resultString.getBytes();
//             if (operation.startsWith("Decryption")) {
//                 writer.write("plaintext: ");
//             } else {
//                 writer.write("ciphertext: ");
//             }

//             for (byte b : result) {
//                 writer.write(String.format("%02X ", b));
//             }

//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class RC6FileHandler {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String operation = reader.readLine().trim();
            String line2 = reader.readLine().trim();
            String line3 = reader.readLine().trim();

            // Split line 2 and line 3 by colon and trim spaces
            String[] line2Parts = line2.split(":");
            String[] line3Parts = line3.split(":");

            String text = line2Parts[1].trim();
            byte[] userKey = parseHex(line3Parts[1].trim().split(" "));

            byte[] result;
            if (operation.equalsIgnoreCase("Encryption")) {
                result = RC6.encrypt(text.getBytes(), userKey);
                writer.write("ciphertext: " + bytesToHex(result));
            } else if (operation.equalsIgnoreCase("Decryption")) {
                result = RC6.decrypt(parseHex(text.split(" ")), userKey);
                writer.write("plaintext: " + bytesToHex(result));
            } else {
                System.out.println("Invalid operation specified in the input file.");
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to convert byte array to hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x ", b));
        }
        return sb.toString().trim();
    }

    // Helper method to convert hexadecimal string array to byte array
    private static byte[] parseHex(String[] hexStrings) {
        byte[] bytes = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexStrings[i], 16);
        }
        return bytes;
    }
}
