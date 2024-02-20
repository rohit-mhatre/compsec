import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RC7 {

    private int w, r, b, log_w;
    private long modulo;
    private int[] S;
    private int[] L;

    public RC7(int W, int R, int B) {
        w = W;
        r = R;
        b = B;
        log_w = (int) (Math.log(w) / Math.log(2));
        modulo = (long) Math.pow(2, w);
        S = new int[2 * r + 4];
    }

    private void rc_constraints(int w, int[] p, int[] q) {
        p[0] = (int) Math.ceil(((Math.E - 2) * Math.pow(2, w)));
        q[0] = (int) ((1.618033988749895 - 1) * Math.pow(2, w)); // Golden Ratio
    }

    private int left_rot(int a, int b, int w) {
        b <<= w - log_w;
        b >>= w - log_w;
        return (a << b) | (a >>> (w - b));
    }

    private int right_rot(int a, int b, int w) {
        b <<= w - log_w;
        b >>= w - log_w;
        return (a >>> b) | (a << (w - b));
    }

    private String little_endian(String str) {
        StringBuilder endian = new StringBuilder();

        if (str.length() % 2 == 0) {
            for (int i = str.length() - 1; i >= 0; i -= 2) {
                endian.append(str.charAt(i - 1));
                endian.append(str.charAt(i));
            }
        } else {
            str = "0" + str;
            for (int i = str.length() - 1; i >= 0; i -= 2) {
                endian.append(str.charAt(i - 1));
                endian.append(str.charAt(i));
            }
        }

        return endian.toString();
    }

    private String hex_to_string(int A, int B, int C, int D) {
        String strA, strB, strC, strD, result;

        strA = String.format("%04x", A);
        strA = little_endian(strA);

        strB = String.format("%04x", B);
        strB = little_endian(strB);

        strC = String.format("%04x", C);
        strC = little_endian(strC);

        strD = String.format("%04x", D);
        strD = little_endian(strD);

        result = strA + strB + strC + strD;

        return result;
    }

    private void key_schedule(String key) {
        int w_bytes = (int) Math.ceil((float) w / 8);
        int c = (int) Math.ceil((float) b / w_bytes);

        int[] p = new int[1];
        int[] q = new int[1];
        rc_constraints(w, p, q);

        L = new int[c];
        for (int i = 0; i < c; i++) {
            String subkey = key.substring(w_bytes * 2 * i, w_bytes * 2 * (i + 1));
                    System.out.println("Subkey " + i + ": " + subkey);
                    if(subkey.isEmpty()) {
            System.out.println("Substring is empty at index " + i);
        }
            L[i] = Integer.parseInt(little_endian(subkey), 16);
        }

        S[0] = p[0];
        for (int i = 1; i <= (2 * r + 3); i++) {
            S[i] = (S[i - 1] + q[0]) % (int) modulo;
        }

        int A = 0, B = 0, i = 0, j = 0;
        int v = 3 * Math.max(c, (2 * r + 4));
        for (int s = 1; s <= v; s++) {
            A = S[i] = left_rot((S[i] + A + B) % (int) modulo, 3, w);
            B = L[j] = left_rot((L[j] + A + B) % (int) modulo, (A + B), w);
            i = (i + 1) % (2 * r + 4);
            j = (j + 1) % c;
        }
    }

    public String encrypt(String text) {
        String result = "";

        int A, B, C, D;
        A = Integer.parseInt(little_endian(text.substring(0, 8)), 16);
        B = Integer.parseInt(little_endian(text.substring(8, 16)), 16);
        C = Integer.parseInt(little_endian(text.substring(16, 24)), 16);
        D = Integer.parseInt(little_endian(text.substring(24, 32)), 16);

        int t, u, temp;

        B += S[0];
        D += S[1];
        for (int i = 1; i <= r; ++i) {
            t = left_rot((B * (2 * B + 1)) % (int) modulo, log_w, w);
            u = left_rot((D * (2 * D + 1)) % (int) modulo, log_w, w);
            A = left_rot((A ^ t), u, w) + S[2 * i];
            C = left_rot((C ^ u), t, w) + S[2 * i + 1];
            temp = A;
            A = B;
            B = C;
            C = D;
            D = temp;
        }

        A += S[2 * r + 2];
        C += S[2 * r + 3];

        result = hex_to_string(A, B, C, D);

        return result;
    }

    public String decrypt(String text) {
        String result = "";

        int A, B, C, D;
        A = Integer.parseInt(little_endian(text.substring(0, 8)), 16);
        B = Integer.parseInt(little_endian(text.substring(8, 16)), 16);
        C = Integer.parseInt(little_endian(text.substring(16, 24)), 16);
        D = Integer.parseInt(little_endian(text.substring(24, 32)), 16);

        int t, u, temp;
        C -= S[2 * r + 3];
        A -= S[2 * r + 2];
        for (int i = r; i >= 1; --i) {
            temp = D;
            D = C;
            C = B;
            B = A;
            A = temp;
            u = left_rot((D * (2 * D + 1)) % (int) modulo, log_w, w);
            t = left_rot((B * (2 * B + 1)) % (int) modulo, log_w, w);
            C = right_rot((C - S[2 * i + 1]) % (int) modulo, t, w) ^ u;
            A = right_rot((A - S[2 * i]) % (int) modulo, u, w) ^ t;
        }
        D -= S[1];
        B -= S[0];

        result = hex_to_string(A, B, C, D);

        return result;
    }

    public String run(String mode, String text, String key) {
        String result = "";

        key_schedule(key);

        if (mode.startsWith("Encryption")) {
            String encryption = encrypt(text);
            for (int i = 0; i < encryption.length(); i += 2) {
                result += encryption.charAt(i);
                result += encryption.charAt(i + 1);
                result += " ";
            }
        } else if (mode.startsWith("Decryption")) {
            String decryption = decrypt(text);
            for (int i = 0; i < decryption.length(); i += 2) {
                result += decryption.charAt(i);
                result += decryption.charAt(i + 1);
                result += " ";
            }
        }

        return result;
    }

    // public static void main(String[] args) {
    //     if (args.length != 2) {
    //         System.out.println("Usage: java RC6 input_file output_file");
    //         return;
    //     }

    //     String inputFile = args[0];
    //     String outputFile = args[1];
    //     try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
    //          BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
    //         String mode = br.readLine();
    //         if (mode == null) {
    //             System.out.println("Invalid input file format. Mode is missing.");
    //             return;
    //         }

    //         String text = br.readLine();
    //         if (text == null) {
    //             System.out.println("Invalid input file format. Text is missing.");
    //             return;
    //         }

    //         String key = br.readLine();
    //         if (key == null) {
    //             System.out.println("Invalid input file format. Key is missing.");
    //             return;
    //         }

    //         // Remove whitespaces from text and key
    //         text = text.replace("plaintext: ", "").replace("ciphertext: ", "");
    //         key = key.replace("userkey: ", "");
    //         text = text.replaceAll("\\s", "");
    //         key = key.replaceAll("\\s", "");

    //         // Perform RC6 encryption or decryption based on the mode
    //         RC7 rc7 = new RC7(32, 20, key.length() / 2); // Adjust parameters accordingly
    //         String result = "";
    //         if (mode.equalsIgnoreCase("Encryption")) {
    //             result = rc7.(text, key);
    //             bw.write("Ciphertext: " + result);
    //         } else if (mode.equalsIgnoreCase("Decryption")) {
    //             result = rc7.decrypt(text, key);
    //             bw.write("Plaintext: " + result);
    //         } else {
    //             System.out.println("Invalid mode. Please specify 'Encryption' or 'Decryption'.");
    //             return;
    //         }
    //         System.out.println("Result written to " + outputFile);

    //     } catch (IOException e) {
    //         System.out.println("Error reading/writing the file: " + e.getMessage());
    //     }
    // }

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
        text = text.replace("plaintext: ", "");
        text = text.replace("ciphertext: ", "");
        key = key.replace("userkey: ", "");
        text = text.replaceAll("\\s", "");
        key = key.replaceAll("\\s", "");
        //you gotta remove this broski
        System.out.println(text);
        System.out.println(key);


        // Perform RC6 encryption or decryption based on the mode
        RC7 rc7 = new RC7(32, 20, key.length() / 2); // Adjust parameters accordingly
        String result = "";
        if (mode.equalsIgnoreCase("Encryption")) {
            result = rc7.run("Encryption", text, key);
            bw.write("Ciphertext: " + result);
        } else if (mode.equalsIgnoreCase("Decryption")) {
            result = rc7.run("Decryption", text, key);
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