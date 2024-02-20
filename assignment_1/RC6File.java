// // import java.io.BufferedReader;
// // import java.io.BufferedWriter;
// // import java.io.FileReader;
// // import java.io.FileWriter;
// // import java.io.IOException;

// // public class RC6File {

// //     public static void main(String[] args) {
// //         if (args.length != 2) {
// //             System.out.println("Usage: java Main <input_file> <output_file>");
// //             return;
// //         }

// //         String inputFile = args[0];
// //         String outputFile = args[1];

// //         try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
// //              BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

// //             String operation = reader.readLine().trim();
// //             System.out.println("Operation: " + operation);

// //             String line2 = reader.readLine().replaceAll("\\s","");
// //             String line3 = reader.readLine().replaceAll("\\s","");
            
// //             System.out.println("Line 2: " + line2);
// //             System.out.println("Line 3: " + line3);

// //             // Split line 2 and line 3 by colon and trim spaces
// //             String[] line2Parts = line2.split(":");
// //             String[] line3Parts = line3.split(":");
            
// //             System.out.println("Text: " + line2Parts[1].trim());
// //             System.out.println("Key: " + line3Parts[1].trim());

// //             String text = line2Parts[1].trim();
// // byte[] userKey = parseHex(line3.trim().split(":")[1].trim());


// //             byte[] result = null;
// //             if (operation.equalsIgnoreCase("Encryption")) {
// //                 result = RC6.encrypt(text.getBytes(), userKey);
// //                 writer.write("ciphertext: " + bytesToHex(result));
// //             } else if (operation.equalsIgnoreCase("Decryption")) {
// // result = RC6.decrypt(parseHex(text), userKey);

// //                 writer.write("plaintext: " + bytesToHex(result));
// //             } else {
// //                 System.out.println("Invalid operation specified in the input file.");
// //                 return;
// //             }

// //             System.out.println("Result: " + bytesToHex(result));

// //         } catch (IOException e) {
// //             e.printStackTrace();
// //         }
// //     }

// //     // Helper method to convert byte array to hexadecimal string
// //     private static String bytesToHex(byte[] bytes) {
// //         StringBuilder sb = new StringBuilder();
// //         for (byte b : bytes) {
// //             sb.append(String.format("%02x ", b));
// //         }
// //         return sb.toString().trim();
// //     }

// //     // Helper method to convert hexadecimal string array to byte array
// //     private static byte[] parseHex(String hexString) {
// //     // Remove any leading or trailing whitespace
// //     hexString = hexString.trim();
    
// //     // Remove any spaces from the string
// //     hexString = hexString.replaceAll("\\s", "");
    
// //     // Check if the length of the string is odd
// //     if (hexString.length() % 2 != 0) {
// //         throw new IllegalArgumentException("Invalid hexadecimal string length.");
// //     }

// //     // Create a byte array to hold the bytes
// //     byte[] bytes = new byte[hexString.length() / 2];

// //     // Parse each pair of characters as hexadecimal numbers
// //     for (int i = 0; i < hexString.length(); i += 2) {
// //         String pair = hexString.substring(i, i + 2);
// //         bytes[i / 2] = (byte) Integer.parseInt(pair, 16);
// //     }

// //     return bytes;
// // }

// // }

// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileReader;
// import java.io.FileWriter;

// public class RC6File {
//     public static void main(String[] args) {
//         if (args.length != 2) {
//             System.out.println("Usage: java RC6FileRunner <input_file> <output_file>");
//             return;
//         }

//         String inputFile = args[0];
//         String outputFile = args[1];

//         try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
//              BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

//             String operation = reader.readLine().trim();
//             String line2 = reader.readLine().replaceAll("\\s", "");
//             String line3 = reader.readLine().replaceAll("\\s", "");

//             String[] line2Parts = line2.split(":");
//             String[] line3Parts = line3.split(":");

//             String text = line2Parts[1].trim();
//             byte[] userKey = parseHex(line3Parts[1].trim());

//             RC6 rc6 = new RC6(userKey);

//             byte[] result = null;
//             if (operation.equalsIgnoreCase("Encryption")) {
//                 result = rc6.encrypt(parseHex(text));
//                 writer.write("ciphertext: " + bytesToHex(result));
//             } else if (operation.equalsIgnoreCase("Decryption")) {
//                 result = rc6.decrypt(parseHex(text));
//                 writer.write("plaintext: " + bytesToHex(result));
//             } else {
//                 System.out.println("Invalid operation specified in the input file.");
//                 return;
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     // Helper method to convert byte array to hexadecimal string
//     private static String bytesToHex(byte[] bytes) {
//         StringBuilder sb = new StringBuilder();
//         for (byte b : bytes) {
//             sb.append(String.format("%02x ", b));
//         }
//         return sb.toString().trim();
//     }

//     // Helper method to convert hexadecimal string to byte array
//     private static byte[] parseHex(String hexString) {
//         hexString = hexString.trim().replaceAll("\\s", "");
//         if (hexString.length() % 2 != 0) {
//             throw new IllegalArgumentException("Invalid hexadecimal string length.");
//         }
//         byte[] bytes = new byte[hexString.length() / 2];
//         for (int i = 0; i < hexString.length(); i += 2) {
//             String pair = hexString.substring(i, i + 2);
//             bytes[i / 2] = (byte) Integer.parseInt(pair, 16);
//         }
//         return bytes;
//     }
// }

// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileReader;
// import java.io.FileWriter;

// public class RC6File {
//     public static void main(String[] args) {
//         if (args.length != 2) {
//             System.out.println("Usage: java RC6FileRunner <input_file> <output_file>");
//             return;
//         }

//         String inputFile = args[0];
//         String outputFile = args[1];

//         try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
//              BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

//             String operation = reader.readLine().trim();
//             String line2 = reader.readLine();
//             String line3 = reader.readLine();

//             String[] line2Parts = line2.split(":");
//             String[] line3Parts = line3.split(":");

//             String text = line2Parts[1].trim().replaceAll("\\s", "");
//             byte[] userKey = parseHex(line3Parts[1].trim().replaceAll("\\s", ""));

//             RC6 rc6 = new RC6(userKey);

//             byte[] result = null;
//             if (operation.equalsIgnoreCase("Encryption")) {
//                 result = rc6.encrypt(parseHex(text));
//                 writer.write("ciphertext: " + bytesToHex(result));
//             } else if (operation.equalsIgnoreCase("Decryption")) {
//                 result = rc6.decrypt(parseHex(text));
//                 writer.write("plaintext: " + bytesToHex(result));
//             } else {
//                 System.out.println("Invalid operation specified in the input file.");
//                 return;
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     // Helper method to convert byte array to hexadecimal string
//     private static String bytesToHex(byte[] bytes) {
//         StringBuilder sb = new StringBuilder();
//         for (byte b : bytes) {
//             sb.append(String.format("%02x ", b));
//         }
//         return sb.toString().trim();
//     }

//     // Helper method to convert hexadecimal string to byte array
//     private static byte[] parseHex(String hexString) {
//         hexString = hexString.trim().replaceAll("\\s", "");
//         if (hexString.length() % 2 != 0) {
//             throw new IllegalArgumentException("Invalid hexadecimal string length.");
//         }
//         byte[] bytes = new byte[hexString.length() / 2];
//         for (int i = 0; i < hexString.length(); i += 2) {
//             String pair = hexString.substring(i, i + 2);
//             bytes[i / 2] = (byte) Integer.parseInt(pair, 16);
//         }
//         return bytes;
//     }
// }

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class RC6File {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RC6FileRunner <input_file> <output_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String operation = reader.readLine().trim();
            String line2 = reader.readLine();
            String line3 = reader.readLine();

            String[] line2Parts = line2.split(":");
            String[] line3Parts = line3.split(":");

            String text = line2Parts[1].trim().replaceAll("\\s", "");
            byte[] userKey = parseHex(line3Parts[1].trim().replaceAll("\\s", ""));

            RC6 rc6 = new RC6(userKey);

            byte[] result = null;
            if (operation.equalsIgnoreCase("Encryption")) {
                result = rc6.encrypt(parseHex(text));
                writer.write("ciphertext: " + bytesToHex(result));
            } else if (operation.equalsIgnoreCase("Decryption")) {
                result = rc6.decrypt(parseHex(text));
                writer.write("plaintext: " + bytesToHex(result));
            } else {
                System.out.println("Invalid operation specified in the input file.");
                return;
            }

        } catch (Exception e) {
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

    // Helper method to convert hexadecimal string to byte array
    private static byte[] parseHex(String hexString) {
        hexString = hexString.trim().replaceAll("\\s", "");
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hexadecimal string length.");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            String pair = hexString.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(pair, 16);
        }
        return bytes;
    }
}
