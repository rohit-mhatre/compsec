import java.math.BigInteger;
import java.util.Arrays;

public class RC66 {

    private int w;
    private int r;
    private int b;
    private int log_w;
    private BigInteger modulo;
    private int[] S;
    private int[] L;

    public RC6(int W, int R, int B) {
        w = W;
        r = R;
        b = B;
        log_w = (int) (Math.log(w) / Math.log(2));
        modulo = BigInteger.valueOf(2).pow(w);
        S = new int[2 * r + 4];
    }

    private void rc_constraints(int w, int[] p, int[] q) {
        p[0] = (int) Math.ceil((Math.E - 2) * Math.pow(2, w));
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
            for (int i = str.length() - 2; i >= 0; i -= 2) {
                endian.append(str.charAt(i + 1)).append(str.charAt(i));
            }
        } else {
            str = "0" + str;
            for (int i = str.length() - 2; i >= 0; i -= 2) {
                endian.append(str.charAt(i + 1)).append(str.charAt(i));
            }
        }

        return endian.toString();
    }

    private String hex_to_string(int A, int B, int C, int D) {
        return little_endian(String.format("%08X", A)) +
               little_endian(String.format("%08X", B)) +
               little_endian(String.format("%08X", C)) +
               little_endian(String.format("%08X", D));
    }

    public void key_schedule(String key) {
        int w_bytes = (int) Math.ceil((float) w / 8);
        int c = (int) Math.ceil((float) b / w_bytes);

        int[] p = new int[1];
        int[] q = new int[1];
        rc_constraints(w, p, q);

        L = new int[c];
        for (int i = 0; i < c; i++) {
            L[i] = new BigInteger(little_endian(key.substring(w_bytes * 2 * i, w_bytes * 2 * (i + 1))), 16).intValue();
        }

        S[0] = p[0];
        for (int i = 1; i <= (2 * r + 3); i++) {
            S[i] = (S[i - 1] + q[0]) % modulo.intValue();
        }

        int A = 0, B = 0, i = 0, j = 0;
        int v = 3 * Math.max(c, (2 * r + 4));
        for (int s = 1; s <= v; s++) {
            A = left_rot((S[i] + A + B) % modulo.intValue(), 3, w);
            B = left_rot((L[j] + A + B) % modulo.intValue(), (A + B), w);
            i = (i + 1) % (2 * r + 4);
            j = (j + 1) % c;
        }
    }

    public String encrypt(String text) {
        int A = new BigInteger(little_endian(text.substring(0, 8)), 16).intValue();
        int B = new BigInteger(little_endian(text.substring(8, 16)), 16).intValue();
        int C = new BigInteger(little_endian(text.substring(16, 24)), 16).intValue();
        int D = new BigInteger(little_endian(text.substring(24, 32)), 16).intValue();

        int t, u, temp;

        B += S[0];
        D += S[1];
        for (int i = 1; i <= r; ++i) {
            t = left_rot((B * (2 * B + 1)) % modulo.intValue(), log_w, w);
            u = left_rot((D * (2 * D + 1)) % modulo.intValue(), log_w, w);
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

        return hex_to_string(A, B, C, D);
    }

    public String decrypt(String text) {
        int A = new BigInteger(little_endian(text.substring(0, 8)), 16).intValue();
        int B = new BigInteger(little_endian(text.substring(8, 16)), 16).intValue();
        int C = new BigInteger(little_endian(text.substring(16, 24)), 16).intValue();
        int D = new BigInteger(little_endian(text.substring(24, 32)), 16).intValue();

        int t, u, temp;

        C -= S[2 * r + 3];
        A -= S[2 * r + 2];
        for (int i = r; i >= 1; --i) {
            temp = D;
            D = C;
            C = B;
            B = A;
            A = temp;
            u = left_rot((D * (2 * D + 1)) % modulo.intValue(), log_w, w);
            t = left_rot((B * (2 * B + 1)) % modulo.intValue(), log_w, w);
            C = right_rot((C - S[2 * i + 1]) % modulo.intValue(), t, w) ^ u;
            A = right_rot((A - S[2 * i]) % modulo.intValue(), u, w) ^ t;
        }
        D -= S[1];
        B -= S[0];

        return hex_to_string(A, B, C, D);
    }

    public String run(String mode, String text, String key) {
        StringBuilder result = new StringBuilder();

        key_schedule(key);

        if (mode.equalsIgnoreCase("Encryption")) {
            String encryption = encrypt(text);
            for (int i = 0; i < encryption.length(); i += 2) {
                result.append(encryption.charAt(i)).append(encryption.charAt(i + 1)).append(" ");
            }
        } else if (mode.equalsIgnoreCase("Decryption")) {
            String decryption = decrypt(text);
            for (int i = 0; i < decryption.length(); i += 2) {
                result.append(decryption.charAt(i)).append(decryption.charAt(i + 1)).append(" ");
            }
        }

        return result.toString().trim();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        S = null;
        L = null;
    }
}