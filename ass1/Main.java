import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments. Usage: java Main input_filename output_filename");
            return;
        }

        String inputFilename = args[0];
        String outputFilename = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFilename))) {

            String mode = reader.readLine();
            String dataLine = reader.readLine();
            String keyLine = reader.readLine();
                            dataLine = dataLine.split(": ")[1];
                dataLine = dataLine.replaceAll("\\s", "");
                System.out.println(dataLine);
                keyLine = keyLine.split(": ")[1];
                keyLine = keyLine.replaceAll("\\s", "");
                System.out.println(keyLine);

            if (mode.equals("Encryption")) {
                String ciphertext = encrypt(dataLine, keyLine);
                writer.println("ciphertext: " + ciphertext);
            } else if (mode.equals("Decryption")) {
                String plaintext = decrypt(dataLine, keyLine);
                writer.println("plaintext: " + plaintext);
            } else {
                System.out.println("Invalid mode specified in the input file.");
                return;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found.");
        }
    }

    private static String encrypt(String plaintext, String key) {
        try {
            byte[] text_byte = plaintext.getBytes();
            byte[] key_byte = key.getBytes();

            if (key_byte.length >= 4) {
                byte[] enc = RC6.encrypt(text_byte, key_byte);
                return bytesToHex(enc);
            } else {
                System.out.println("Key symbols length should be >= 4\n");
                return "";
            }
        } catch (Exception e) {
            System.out.println("Encryption error: " + e.getMessage());
            return "";
        }
    }

    private static String decrypt(String ciphertext, String key) {
        try {
            byte[] encrypt_byte = hexStringToByteArray(ciphertext);
            byte[] key_byte = key.getBytes();

            if (key_byte.length >= 4) {
                byte[] dec = RC6.decrypt(encrypt_byte, key_byte);
                return new String(dec);
            } else {
                System.out.println("Key symbols length should be >= 4\n");
                return "";
            }
        } catch (Exception e) {
            System.out.println("Decryption error: " + e.getMessage());
            return "";
        }
    }

    // private static String bytesToHex(byte[] bytes) {
    //     char[] hexArray = "0123456789abcdef".toCharArray();
    //     char[] hexChars = new char[bytes.length * 2];
    //     for (int j = 0; j < bytes.length; j++) {
    //         int v = bytes[j] & 0xFF;
    //         hexChars[j * 2] = hexArray[v >>> 4];
    //         hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    //     }
    //     return new String(hexChars);
    // }

    // public static byte[] hexStringToByteArray(String s) {
    //     int len = s.length();
    //     byte[] data = new byte[len / 2];
    //     for (int i = 0; i < len; i += 2) {
    //         data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
    //                 + Character.digit(s.charAt(i + 1), 16));
    //     }
    //     return data;
    // }
    private static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}

public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        int firstDigit = Character.digit(s.charAt(i), 16);
        int secondDigit = Character.digit(s.charAt(i + 1), 16);
        if (firstDigit < 0 || secondDigit < 0) {
            throw new IllegalArgumentException("Invalid hexadecimal string");
        }
        int combined = (firstDigit << 4) + secondDigit;
        data[i / 2] = (byte) combined;
    }
    return data;
}

}
