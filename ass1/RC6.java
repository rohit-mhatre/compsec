import java.util.Arrays;

public class RC6 {

    private static final int w = 32;
    private static final int r = 20;
    private static final int Pw = 0xB7E15163;
    private static final int Qw = 0x9E3779b9;
    private static int[] S = new int[r * 2 + 4];
    private static byte[] output;
    private static int counter = 0;
    private static int plainTextLength;

    private static int rotateLeft(int n, int x) {
        return ((n << x) | (n >>> (w - x)));
    }

    private static int rotateRight(int n, int x){
        return ((n >>> x) | (n << (w - x)));
    }

    private static byte[] convertToHex(int regA, int regB, int regC, int regD) {
        int[] data = {regA, regB, regC, regD};
        byte[] text = new byte[w / 2];

        for (int i = 0; i < text.length; i++) {
            text[i] = (byte) ((data[i / 4] >>> (i % 4) * 8) & 0xff);
        }

        return text;
    }

    private static void mergeArrays(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            output[counter] = array[i];
            counter++;
        }
    }

    private static byte[] fillBufferZeroes(byte[] plainText) {
        int length = 16 - plainText.length % 16;
        byte[] block = new byte[plainText.length + length];
        System.arraycopy(plainText, 0, block, 0, plainText.length);
        Arrays.fill(block, plainText.length, block.length, (byte) 0);
        return block;
    }

    private static byte[] clearPadding(byte[] cipherText) {
        int bounds = getBounds(cipherText);
        return Arrays.copyOf(cipherText, bounds);
    }

    private static int getBounds(byte[] cipherText) {
        for (int i = 0; i < cipherText.length; i++) {
            if (cipherText[i] == 0) {
                return i;
            }
        }
        return cipherText.length;
    }

    private static byte[] encryptBlock(byte[] plainText) {
        int regA, regB, regC, regD;
        int index = 0, temp1, temp2, swap;

        regA = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8 | (plainText[index++] & 0xff) << 16 | (plainText[index++] & 0xff) << 24);
        regB = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8 | (plainText[index++] & 0xff) << 16 | (plainText[index++] & 0xff) << 24);
        regC = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8 | (plainText[index++] & 0xff) << 16 | (plainText[index++] & 0xff) << 24);
        regD = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8 | (plainText[index++] & 0xff) << 16 | (plainText[index++] & 0xff) << 24);

        regB = regB + S[0];
        regD = regD + S[1];

        for (int i = 1; i <= r; i++) {
            temp1 = rotateLeft(regB * (regB * 2 + 1), 5);
            temp2 = rotateLeft(regD * (regD * 2 + 1), 5);
            regA = (rotateLeft(regA ^ temp1, temp2)) + S[i * 2];
            regC = (rotateLeft(regC ^ temp2, temp1)) + S[i * 2 + 1];

            swap = regA;
            regA = regB;
            regB = regC;
            regC = regD;
            regD = swap;
        }

        regA = regA + S[r * 2 + 2];
        regC = regC + S[r * 2 + 3];

        return convertToHex(regA, regB, regC, regD);
    }

    private static byte[] decryptBlock(byte[] cipherText) {
        int regA, regB, regC, regD;
        int index = 0, temp1, temp2, swap;

        regA = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8 | (cipherText[index++] & 0xff) << 16 | (cipherText[index++] & 0xff) << 24);
        regB = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8 | (cipherText[index++] & 0xff) << 16 | (cipherText[index++] & 0xff) << 24);
        regC = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8 | (cipherText[index++] & 0xff) << 16 | (cipherText[index++] & 0xff) << 24);
        regD = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8 | (cipherText[index++] & 0xff) << 16 | (cipherText[index++] & 0xff) << 24);

        regC = regC - S[r * 2 + 3];
        regA = regA - S[r * 2 + 2];

        for (int i = r; i >= 1; i--) {
            swap = regD;
            regD = regC;
            regC = regB;
            regB = regA;
            regA = swap;

            temp2 = rotateLeft(regD * (regD * 2 + 1), 5);
            temp1 = rotateLeft(regB * (regB * 2 + 1), 5);
            regC = rotateRight(regC - S[i * 2 + 1], temp1) ^ temp2;
            regA = rotateRight(regA - +S[i * 2], temp2) ^ temp1;
        }

        regD = regD - S[1];
        regB = regB - S[0];

        return convertToHex(regA, regB, regC, regD);
    }

    public static byte[] encrypt(byte[] plainText, byte[] userKey) {
        int blocks_number = plainText.length / 16 + 1;
        int block_counter = 0;
        plainTextLength = plainText.length;
        output = new byte[16 * blocks_number];
        keySchedule(userKey);
        for (int i = 0; i < blocks_number; i++) {
            if (blocks_number == i + 1) {
                mergeArrays(encryptBlock(fillBufferZeroes(Arrays.copyOfRange(plainText, block_counter, plainText.length))));
                break;
            }
            mergeArrays(encryptBlock(Arrays.copyOfRange(plainText, block_counter, block_counter + 16)));
            block_counter += 16;
        }
        counter = 0;
        return output;
    }

    public static byte[] decrypt(byte[] cipherText, byte[] userKey) {
        int blocks_number = cipherText.length / 16 + 1;
        int block_counter = 0;
        output = new byte[16 * blocks_number];
        keySchedule(userKey);

        for (int i = 0; i < blocks_number; i++) {
            if (blocks_number == i + 1) {
                mergeArrays(decryptBlock(fillBufferZeroes(Arrays.copyOfRange(cipherText, block_counter, cipherText.length))));
                break;
            }
            mergeArrays(decryptBlock(Arrays.copyOfRange(cipherText, block_counter, block_counter + 16)));
            block_counter += 16;
        }
        counter = 0;

        return clearPadding(output);
    }

    private static void keySchedule(byte[] key) {
        int bytes = w / 8;
        int c = key.length / bytes;
        int[] L = new int[c];
        int index = 0;

        for (int i = 0; i < c; i++) {
            L[i] = ((key[index++]) & 0xff | (key[index++] & 0xff) << 8 | (key[index++] & 0xff) << 16 | (key[index++] & 0xff) << 24);
        }
        S[0] = Pw;

        for (int i = 1; i <= 2 * r + 4; i++) {
            S[i] = S[i - 1] + Qw;
        }

        int A = 0, B = 0, i = 0, j = 0;
        int v = 3 * Math.max(c, 2 * r + 4);

        for (int k = 1; k <= v; k++) {
            A = S[i] = rotateLeft(S[i] + A + B, 3);
            B = L[j] = rotateLeft(L[j] + A + B, A + B);
            i = (i + 1) % (2 * r + 4);
            j = (j + 1) % c;
        }
    }
}
