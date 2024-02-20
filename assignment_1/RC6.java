// import java.util.Arrays;

// public class RC6 {
//     private static int w = 32;
//     private static int r = 20;
//     private static int Pw = 0xB7E15163;
//     private static int Qw = 0x9E3779b9;
//     private static int[] S = new int[r * 2 + 4];
//     private static byte[] output;
//     private static int counter = 0;
//     private static int plainTextLength;

//     private static int rotateLeft(int n, int x){
//         return ((n << x) | (n >>> (w - x)));
//     }

//     private static int rotateRight(int n, int x){
//         return ((n >>> x) | (n << (w - x)));
//     }

//     private static byte[] convertToHex(int regA,int regB, int regC, int regD){
//         int[] data = new int[4];
//         byte[] text = new byte[w / 2];
//         data[0] = regA;
//         data[1] = regB;
//         data[2] = regC;
//         data[3] = regD;

//         for(int i = 0;i < text.length;i++){
//             text[i] = (byte)((data[i/4] >>> (i%4)*8) & 0xff);
//         }

//         return text;
//     }

//     private static void mergeArrays(byte[] array){
//         for (int i = 0; i < array.length; i++){
//             output[counter] = array[i];
//             counter++;
//         }
//     }

//     private static byte[] fillBufferZeroes(byte[] plainText){
//         int length = 16 - plainText.length % 16;
//         byte[] block = new byte[plainText.length + length];
//         for (int i = 0; i < plainText.length; i++){
//             block[i] = plainText[i];
//         }
//         for(int i = plainText.length; i < plainText.length + length; i++){
//             block[i] = 0;
//         }
//         return block;
//     }

//     private static byte[] clearPadding(byte[] cipherText){
//         byte[] answer = new byte[getBounds(cipherText)];
//         for(int i = 0; i < cipherText.length; i++){
//             if(cipherText[i] == 0) break;
//             answer[i] = cipherText[i];
//         }

//         return answer;
//     }

//     private static int getBounds(byte[] cipherText){
//         for(int i = 0; i < cipherText.length; i++){
//             if(cipherText[i] == 0){
//                 return i;
//             }
//         }
//         return cipherText.length;
//     }

//     private static byte[] encryptBlock(byte[] plainText){


//         int regA, regB, regC, regD;
//         int index = 0, temp1, temp2, swap;

//         regA = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8| (plainText[index++] & 0xff) << 16| (plainText[index++] & 0xff)<<24);
//         regB = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8| (plainText[index++] & 0xff) << 16| (plainText[index++] & 0xff)<<24);
//         regC = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8| (plainText[index++] & 0xff) << 16| (plainText[index++] & 0xff)<<24);
//         regD = ((plainText[index++] & 0xff) | (plainText[index++] & 0xff) << 8| (plainText[index++] & 0xff) << 16| (plainText[index++] & 0xff)<<24);

//         regB = regB + S[0];
//         regD = regD + S[1];

//         for(int i = 1; i <= r ; i++){
//             temp1 = rotateLeft(regB * (regB * 2 + 1), 5);
//             temp2 = rotateLeft(regD * (regD * 2 + 1), 5);
//             regA = (rotateLeft(regA ^ temp1, temp2)) + S[i * 2];
//             regC = (rotateLeft(regC ^ temp2, temp1)) + S[i * 2 + 1];

//             swap = regA;
//             regA = regB;
//             regB = regC;
//             regC = regD;
//             regD = swap;
//         }

//         regA = regA + S[r * 2 + 2];
//         regC = regC + S[r * 2 + 3];

//         return convertToHex(regA, regB, regC, regD);
//     }

//     private static byte[] decryptBlock(byte[] cipherText){

//         int regA, regB, regC, regD;
//         int index = 0, temp1, temp2, swap;

//         regA = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8| (cipherText[index++] & 0xff) << 16| (cipherText[index++] & 0xff)<<24);
//         regB = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8| (cipherText[index++] & 0xff) << 16| (cipherText[index++] & 0xff)<<24);
//         regC = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8| (cipherText[index++] & 0xff) << 16| (cipherText[index++] & 0xff)<<24);
//         regD = ((cipherText[index++] & 0xff) | (cipherText[index++] & 0xff) << 8| (cipherText[index++] & 0xff) << 16| (cipherText[index++] & 0xff)<<24);


//         regC = regC - S[r * 2 + 3];
//         regA = regA - S[r * 2 + 2];

//         for(int i = r; i >= 1 ; i--){
//             swap = regD;
//             regD = regC;
//             regC = regB;
//             regB = regA;
//             regA = swap;

//             temp2 = rotateLeft(regD * (regD * 2 + 1), 5);
//             temp1 = rotateLeft(regB * (regB * 2 + 1), 5);
//             regC =  rotateRight(regC - S[i * 2 + 1], temp1) ^ temp2;
//             regA =  rotateRight(regA -  + S[i * 2], temp2) ^ temp1;
//         }

//         regD = regD - S[1];
//         regB = regB - S[0];
//         return convertToHex(regA, regB, regC, regD);
//     }

//     public static byte[] encrypt(byte[] plainText, byte[] userKey){
//         int blocks_number = plainText.length / 16 + 1;
//         int block_counter = 0;
//         plainTextLength = plainText.length;
//         output = new byte[16*blocks_number];
//         keyShedule(userKey);
//         for(int i = 0; i < blocks_number; i++){
//             if(blocks_number == i + 1){
//                 mergeArrays(encryptBlock(fillBufferZeroes(Arrays.copyOfRange(plainText, block_counter , plainText.length))));
//                 break;
//             }
//             mergeArrays(encryptBlock(Arrays.copyOfRange(plainText, block_counter ,block_counter+16)));
//             block_counter += 16;
//         }
//         counter = 0;
//         return output;
//     }

//     public static byte[] decrypt(byte[] cipherText, byte[] userKey){
//         int blocks_number = cipherText.length / 16 + 1;
//         int block_counter = 0;
//         output = new byte[16*blocks_number];
//         keyShedule(userKey);

//         for(int i = 0; i < blocks_number; i++){
//             if(blocks_number == i + 1){
//                 mergeArrays(decryptBlock(fillBufferZeroes(Arrays.copyOfRange(cipherText, block_counter ,cipherText.length))));
//                 break;
//             }
//             mergeArrays(decryptBlock(Arrays.copyOfRange(cipherText, block_counter ,block_counter+16)));
//             block_counter += 16;
//         }
//         counter = 0;

//         return clearPadding(output);
//     }

//     private static void keyShedule(byte[] key){
//         int bytes = w / 8;
//         int c = key.length / bytes;
//         int[] L = new int[c];
//         int index = 0;

//         for(int i = 0; i < c; i++){
//             L[i] = ((key[index++]) & 0xff | (key[index++] & 0xff) << 8 | (key[index++] & 0xff) << 16 | (key[index++] & 0xff) << 24);
//         }
//         S[0] = Pw;

//         for(int i = 1; i <= 2*r+3; i++){
//             S[i] = S[i-1] + Qw;
//         }

//         int A = 0, B = 0, i = 0,j = 0;
//         int v = 3 * Math.max(c, 2*r+4);

//         for(int k = 1;k <= v; k++){
//             A = S[i] = rotateLeft(S[i] + A + B, 3);
//             B = L[j] = rotateLeft(L[j] + A + B, A+B);
//             i = (i + 1) % (2 * r + 4);
//             j = (j + 1) % c;
//         }
//     }
// }

// import java.util.Arrays;

// public class RC6 {
//     private static final int w = 32;
//     private static final int r = 20;
//     private static final int Pw = 0xb7e15163;
//     private static final int Qw = 0x9e3779b9;
//     private int[] S;

//     public RC6(byte[] key) {
//         int c = key.length / (w / 8);
//         int[] L = new int[c];
//         S = new int[2 * r + 4];

//         for (int i = 0; i < key.length; i += 4) {
//             L[i / 4] = ((key[i] & 0xFF) << 24) | ((key[i + 1] & 0xFF) << 16) | ((key[i + 2] & 0xFF) << 8) | (key[i + 3] & 0xFF);
//         }

//         S[0] = Pw;
//         for (int i = 1; i < S.length; i++) {
//             S[i] = S[i - 1] + Qw;
//         }

//         int A, B, i, j;
//         A = B = i = j = 0;
//         int v = 3 * Math.max(c, 2 * r + 4);

//         for (int s = 0; s < v; s++) {
//             A = S[i] = rotateLeft(S[i] + A + B, 3);
//             B = L[j] = rotateLeft(L[j] + A + B, A + B);
//             i = (i + 1) % (2 * r + 4);
//             j = (j + 1) % c;
//         }
//     }

//     public byte[] encrypt(byte[] plaintext) {
//         int[] pt = toIntArray(plaintext);
//         int A = pt[0], B = pt[1], C = pt[2], D= pt[3];

//         B += S[0];
//         D += S[1];

//         for (int i=1; i<=r; i++) {
//             int t= rotateLeft(B*(2*B+1),5);
//             int u= rotateLeft(D*(2*D+1),5);
            
//             A= rotateLeft(A^t,u)+S[2*i];
//             C= rotateLeft(C^u,t)+S[2*i+1];
            
//             int tempD= D;
//             D= C;
//             C= B;
//             B= A;
//             A= tempD;
//           }
        
//           A+=S[2*r+2];
//           C+=S[2*r+3];
        
//           return toByteArray(new int[]{A,B,C,D});
//     }

//     public byte[] decrypt(byte[] ciphertext) {
//         int[] ct= toIntArray(ciphertext);
        
//           int A= ct[0],B= ct[1],C= ct[2],D= ct[3];
        
//           C-=S[2*r+3];
//           A-=S[2*r+2];
        
//           for(int i=r;i>=1;i--){
//               int tempD=D;
//               D=C;
//               C=B;
//               B=A;
//               A=tempD;
              
//               int u=rotateLeft(D*(2*D+1),5);
//               int t=rotateLeft(B*(2*B+1),5);
              
//               C=rotateRight(C-S[2*i+1],t)^u;
//               A=rotateRight(A-S[2*i],u)^t;
//           }
        
//           C-=S[1];
//           A-=S[0];
        
//           return toByteArray(new int[]{A,B,C,D});
//     }

//     private static int rotateLeft(int val, int shift) {
//         return (val << shift) | (val >>> (w - shift));
//     }

//     private static int rotateRight(int val, int shift) {
//         return (val >>> shift) | (val << (w - shift));
//     }

//     private static byte[] toByteArray(int[] arr) {
//       byte[] result=new byte[arr.length*4];
      
//       for(int i=0;i<arr.length;i++){
//           result[i*4]=(byte)(arr[i]>>24);
//           result[i*4+1]=(byte)(arr[i]>>16);
//           result[i*4+2]=(byte)(arr[i]>>8);
//           result[i*4+3]=(byte)(arr[i]);
//       }
      
//       return result;

//     }

//     private static int[] toIntArray(byte[] arr) {
//       if(arr.length%4!=0){
//           throw new IllegalArgumentException("Invalid array length");
//       }
      
//       int[] result=new int[arr.length/4];
      
//       for(int i=0;i<result.length;i++){
//           result[i]=
//               ((arr[i*4]&0xFF)<<24)+
//               ((arr[i*4+1]&0xFF)<<16)+
//               ((arr[i*4+2]&0xFF)<<8)+
//               ((arr[i*4+3]&0xFF));
//       }
      
//       return result;

//     }
// }

// import java.util.Arrays;

// public class RC6 {
//     private static final int w = 32;
//     private static final int r = 20;
//     private static final int Pw = 0xb7e15163;
//     private static final int Qw = 0x9e3779b9;
//     private int[] S;

//     public RC6(byte[] key) {
//         int c = key.length / (w / 8);
//         int[] L = new int[c];
//         S = new int[2 * r + 4];

//         for (int i = 0; i < key.length; i += 4) {
//             L[i / 4] = ((key[i] & 0xFF) << 24) | ((key[i + 1] & 0xFF) << 16) | ((key[i + 2] & 0xFF) << 8) | (key[i + 3] & 0xFF);
//         }

//         S[0] = Pw;
//         for (int i = 1; i < S.length; i++) {
//             S[i] = S[i - 1] + Qw;
//         }

//         int A, B, i, j;
//         A = B = i = j = 0;
//         int v = 3 * Math.max(c, 2 * r + 4);

//         for (int s = 0; s < v; s++) {
//             A = S[i] = rotateLeft(S[i] + A + B, 3);
//             B = L[j] = rotateLeft(L[j] + A + B, A + B);
//             i = (i + 1) % (2 * r + 4);
//             j = (j + 1) % c;
//         }
//     }

//     public byte[] encrypt(byte[] plaintext) {
//         int[] pt = toIntArray(plaintext);
//         int A = pt[0], B = pt[1], C = pt[2], D= pt[3];

//         B += S[0];
//         D += S[1];

//         for (int i=1; i<=r; i++) {
//             int t= rotateLeft(B*(2*B+1),5);
//             int u= rotateLeft(D*(2*D+1),5);
            
//             A= rotateLeft(A^t,u)+S[2*i];
//             C= rotateLeft(C^u,t)+S[2*i+1];
            
//             int tempD= D;
//             D= C;
//             C= B;
//             B= A;
//             A= tempD;
//           }
        
//           A+=S[2*r+2];
//           C+=S[2*r+3];
        
//           return toByteArray(new int[]{A,B,C,D});
//     }

//     public byte[] decrypt(byte[] ciphertext) {
//         int[] ct= toIntArray(ciphertext);
        
//           int A= ct[0],B= ct[1],C= ct[2],D= ct[3];
        
//           C-=S[2*r+3];
//           A-=S[2*r+2];
        
//           for(int i=r;i>=1;i--){
//               int tempD=D;
//               D=C;
//               C=B;
//               B=A;
//               A=tempD;
              
//               int u=rotateLeft(D*(2*D+1),5);
//               int t=rotateLeft(B*(2*B+1),5);
              
//               C=rotateRight(C-S[2*i+1],t)^u;
//               A=rotateRight(A-S[2*i],u)^t;
//           }
        
//           C-=S[1];
//           A-=S[0];
        
//           return toByteArray(new int[]{A,B,C,D});
//     }

//     private static int rotateLeft(int val, int shift) {
//         return (val << shift) | (val >>> (w - shift));
//     }

//     private static int rotateRight(int val, int shift) {
//         return (val >>> shift) | (val << (w - shift));
//     }

//     private static byte[] toByteArray(int[] arr) {
//       byte[] result=new byte[arr.length*4];
      
//       for(int i=0;i<arr.length;i++){
//           result[i*4]=(byte)(arr[i]>>24);
//           result[i*4+1]=(byte)(arr[i]>>16);
//           result[i*4+2]=(byte)(arr[i]>>8);
//           result[i*4+3]=(byte)(arr[i]);
//       }
      
//       return result;

//     }

//     private static int[] toIntArray(byte[] arr) {
//       if(arr.length%4!=0){
//           throw new IllegalArgumentException("Invalid array length");
//       }
      
//       int[] result=new int[arr.length/4];
      
//       for(int i=0;i<result.length;i++){
//           result[i]=
//               ((arr[i*4]&0xFF)<<24)+
//               ((arr[i*4+1]&0xFF)<<16)+
//               ((arr[i*4+2]&0xFF)<<8)+
//               ((arr[i*4+3]&0xFF));
//       }
      
//       return result;

//     }
// }

import java.util.Arrays;

public class RC6 {
    private static final int w = 32;
    private static final int r = 20;
    private static final int lgw = 5; // lg(w)
    private static final int Pw = 0xb7e15163;
    private static final int Qw = 0x9e3779b9;
    private int[] S;

    public RC6(byte[] key) {
        int c = key.length / (w / 8);
        int[] L = new int[c];
        S = new int[2 * r + 4];

        for (int i = 0; i < key.length; i += 4) {
            L[i / 4] = ((key[i] & 0xFF) << 24) | ((key[i + 1] & 0xFF) << 16) | ((key[i + 2] & 0xFF) << 8) | (key[i + 3] & 0xFF);
        }

        S[0] = Pw;
        for (int i = 1; i < S.length; i++) {
            S[i] = S[i - 1] + Qw;
        }

        int A, B, i, j;
        A = B = i = j = 0;
        int v = 3 * Math.max(c, 2 * r + 4);

        for (int s = 0; s < v; s++) {
            A = S[i] = rotateLeft(S[i] + A + B, 3);
            B = L[j] = rotateLeft(L[j] + A + B, A + B);
            i = (i + 1) % (2 * r + 4);
            j = (j + 1) % c;
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        int[] pt = toIntArray(plaintext);
        int A = pt[0], B = pt[1], C = pt[2], D = pt[3];

        B += S[0];
        D += S[1];

        for (int i = 1; i <= r; i++) {
            int t = rotateLeft(B * (2 * B + 1), lgw);
            int u = rotateLeft(D * (2 * D + 1), lgw);

            A = rotateLeft(A ^ t, u) + S[2 * i];
            C = rotateLeft(C ^ u, t) + S[2 * i + 1];

            int tempD = D;
            D = C;
            C = B;
            B = A;
            A = tempD;
        }

        A += S[2 * r + 2];
        C += S[2 * r + 3];

        return toByteArray(new int[]{A, B, C, D});
    }

    public byte[] decrypt(byte[] ciphertext) {
        int[] ct = toIntArray(ciphertext);

        int A = ct[0], B = ct[1], C = ct[2], D = ct[3];

        C -= S[2 * r + 3];
        A -= S[2 * r + 2];

        for (int i = r; i >= 1; i--) {
            int tempD = D;
            D = C;
            C = B;
            B = A;
            A = tempD;

            int u = rotateLeft(D * (2 * D + 1), lgw);
            int t = rotateLeft(B * (2 * B + 1), lgw);

            C = rotateRight(C - S[2 * i + 1], t) ^ u;
            A = rotateRight(A - S[2 * i], u) ^ t;
        }

        C -= S[1];
        A -= S[0];

        return toByteArray(new int[]{A, B, C, D});
    }

    private static int rotateLeft(int val, int shift) {
        return (val << shift) | (val >>> (w - shift));
    }

    private static int rotateRight(int val, int shift) {
        return (val >>> shift) | (val << (w - shift));
    }

    private static byte[] toByteArray(int[] arr) {
        byte[] result = new byte[arr.length * 4];

        for (int i = 0; i < arr.length; i++) {
            result[i * 4] = (byte) (arr[i] >> 24);
            result[i * 4 + 1] = (byte) (arr[i] >> 16);
            result[i * 4 + 2] = (byte) (arr[i] >> 8);
            result[i * 4 + 3] = (byte) (arr[i]);
        }

        return result;
    }

    private static int[] toIntArray(byte[] arr) {
        if (arr.length % 4 != 0) {
            throw new IllegalArgumentException("Invalid array length");
        }

        int[] result = new int[arr.length / 4];

        for (int i = 0; i < result.length; i++) {
            result[i] =
                    ((arr[i * 4] & 0xFF) << 24) +
                            ((arr[i * 4 + 1] & 0xFF) << 16) +
                            ((arr[i * 4 + 2] & 0xFF) << 8) +
                            ((arr[i * 4 + 3] & 0xFF));
        }

        return result;
    }
}
