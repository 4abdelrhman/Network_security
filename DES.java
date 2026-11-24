import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DES {
    private static final int[] IP = {
        58,50,42,34,26,18,10,2, 60,52,44,36,28,20,12,4,
        62,54,46,38,30,22,14,6, 64,56,48,40,32,24,16,8,
        57,49,41,33,25,17,9,1, 59,51,43,35,27,19,11,3,
        61,53,45,37,29,21,13,5, 63,55,47,39,31,23,15,7
    };
    private static final int[] FP = {
        40,8,48,16,56,24,64,32, 39,7,47,15,55,23,63,31,
        38,6,46,14,54,22,62,30, 37,5,45,13,53,21,61,29,
        36,4,44,12,52,20,60,28, 35,3,43,11,51,19,59,27,
        34,2,42,10,50,18,58,26, 33,1,41,9,49,17,57,25
    };
    private static final int[] E = {
        32,1,2,3,4,5, 4,5,6,7,8,9, 8,9,10,11,12,13,
        12,13,14,15,16,17, 16,17,18,19,20,21, 20,21,22,23,24,25,
        24,25,26,27,28,29, 28,29,30,31,32,1
    };
    private static final int[] P = {
        16,7,20,21, 29,12,28,17, 1,15,23,26, 5,18,31,10,
        2,8,24,14, 32,27,3,9, 19,13,30,6, 22,11,4,25
    };
    private static final int[] PC1 = {
        57,49,41,33,25,17,9, 1,58,50,42,34,26,18,
        10,2,59,51,43,35,27, 19,11,3,60,52,44,36,
        63,55,47,39,31,23,15, 7,62,54,46,38,30,22,
        14,6,61,53,45,37,29, 21,13,5,28,20,12,4
    };
    private static final int[] PC2 = {
        14,17,11,24,1,5, 3,28,15,6,21,10, 23,19,12,4,
        26,8,16,7,27,20,13,2, 41,52,31,37,47,55,30,40,
        51,45,33,48,44,49,39,56, 34,53,46,42,50,36,29,32
    };
    private static final int[] SHIFTS = {
        1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1
    };
    private static final int[][] SBOX = {
        {
            14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7,
            0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8,
            4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0,
            15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13
        },
        {
            15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10,
            3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5,
            0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15,
            13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9
        },
        {
            10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8,
            13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1,
            13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7,
            1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12
        },
        {
            7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15,
            13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9,
            10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4,
            3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14
        },
        {
            2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9,
            14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6,
            4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14,
            11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3
        },
        {
            12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11,
            10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8,
            9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6,
            4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13
        },
        {
            4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1,
            13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6,
            1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2,
            6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12
        },
        {
            13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7,
            1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2,
            7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8,
            2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11
        }
    };

    private static long[] generateSubkeys(byte[] key) {
        long keyBits = bytesToLong(key);
        long permuted = permute(keyBits, 64, PC1);
        int c = (int)((permuted >>> 28) & 0x0FFFFFFF);
        int d = (int)(permuted & 0x0FFFFFFF);
        long[] subkeys = new long[16];

        for (int i = 0; i < 16; i++) {
            c = rotateLeft28(c, SHIFTS[i]);
            d = rotateLeft28(d, SHIFTS[i]);
            long combined = (((long)c) << 28) | (d & 0x0FFFFFFFL);
            subkeys[i] = permute(combined, 56, PC2);
        }
        return subkeys;
    }

    private static int rotateLeft28(int value, int amount) {
        return ((value << amount) | (value >>> (28 - amount))) & 0x0FFFFFFF;
    }

    private static int feistel(int right, long subkey) {
        long expanded = permute(right & 0xFFFFFFFFL, 32, E);
        long mixed = expanded ^ subkey;
        int substituted = 0;

        for (int i = 0; i < 8; i++) {
            int sixBits = (int)((mixed >>> (42 - 6 * i)) & 0x3F);
            int row = ((sixBits & 0x20) >>> 4) | (sixBits & 0x01);
            int col = (sixBits >>> 1) & 0x0F;
            substituted = (substituted << 4) | SBOX[i][row * 16 + col];
        }
        return (int)permute(substituted & 0xFFFFFFFFL, 32, P);
    }

    private static long desBlock(long block, long[] subkeys, boolean decrypt) {
        long permuted = permute(block, 64, IP);
        int left = (int)(permuted >>> 32);
        int right = (int)permuted;

        for (int round = 0; round < 16; round++) {
            int keyIndex = decrypt ? 15 - round : round;
            int newLeft = right;
            int newRight = left ^ feistel(right, subkeys[keyIndex]);
            left = newLeft;
            right = newRight;
        }

        long preOutput = (((long)right) << 32) | (left & 0xFFFFFFFFL);
        return permute(preOutput, 64, FP);
    }

    private static long permute(long input, int inputBits, int[] table) {
        long output = 0;
        for (int position : table) {
            output = (output << 1) | ((input >>> (inputBits - position)) & 1L);
        }
        return output;
    }

    private static long bytesToLong(byte[] block) {
        long value = 0;
        for (byte b : block) {
            value = (value << 8) | (b & 0xFFL);
        }
        return value;
    }

    private static byte[] longToBytes(long value) {
        byte[] block = new byte[8];
        for (int i = 7; i >= 0; i--) {
            block[i] = (byte)(value & 0xFF);
            value >>>= 8;
        }
        return block;
    }

    
    private static byte[] pad(byte[] input) {
        int padding = 8 - (input.length % 8);
        byte[] result = new byte[input.length + padding];
        System.arraycopy(input, 0, result, 0, input.length);
        for (int i = input.length; i < result.length; i++) result[i] = (byte)padding;
        return result;
    }

    private static byte[] unpad(byte[] input) {
        if (input.length == 0 || input.length % 8 != 0) {
            throw new IllegalArgumentException("Invalid padded data.");
        }
        int padding = input[input.length - 1] & 0xFF;
        if (padding < 1 || padding > 8 || padding > input.length) {
            throw new IllegalArgumentException("Invalid padding.");
        }
        for (int i = input.length - padding; i < input.length; i++) {
            if ((input[i] & 0xFF) != padding) throw new IllegalArgumentException("Invalid padding.");
        }
        byte[] result = new byte[input.length - padding];
        System.arraycopy(input, 0, result, 0, result.length);
        return result;
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (key.length != 8) throw new IllegalArgumentException("DES key must be exactly 8 bytes.");
        long[] subkeys = generateSubkeys(key);
        byte[] padded = pad(plaintext);
        byte[] ciphertext = new byte[padded.length];

        for (int offset = 0; offset < padded.length; offset += 8) {
            long block = bytesToLong(java.util.Arrays.copyOfRange(padded, offset, offset + 8));
            byte[] encrypted = longToBytes(desBlock(block, subkeys, false));
            System.arraycopy(encrypted, 0, ciphertext, offset, 8);
        }
        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (key.length != 8) throw new IllegalArgumentException("DES key must be exactly 8 bytes.");
        if (ciphertext.length == 0 || ciphertext.length % 8 != 0) {
            throw new IllegalArgumentException("Ciphertext length must be a positive multiple of 8 bytes.");
        }
        long[] subkeys = generateSubkeys(key);
        byte[] plaintextPadded = new byte[ciphertext.length];

        for (int offset = 0; offset < ciphertext.length; offset += 8) {
            long block = bytesToLong(java.util.Arrays.copyOfRange(ciphertext, offset, offset + 8));
            byte[] decrypted = longToBytes(desBlock(block, subkeys, true));
            System.arraycopy(decrypted, 0, plaintextPadded, offset, 8);
        }
        return unpad(plaintextPadded);
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) sb.append(String.format("%02X", b & 0xFF));
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        if (hex.length() % 2 != 0) throw new IllegalArgumentException("Hex must have an even length.");
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.println("=== DES Encryption and Decryption ===");
        System.out.print("Enter plaintext: ");
        String plaintext = scanner.nextLine();

        System.out.print("Enter an 8-character key: ");
        String keyText = scanner.nextLine();
        byte[] key = keyText.getBytes(StandardCharsets.UTF_8);
        if (key.length != 8) {
            System.out.println("Error: Key must be exactly 8 bytes in UTF-8 (use 8 ASCII characters).");
            return;
        }

        byte[] plainBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encrypt(plainBytes, key);
        System.out.println("\nCiphertext (hex): " + toHex(encrypted));

        byte[] decrypted = decrypt(encrypted, key);
        System.out.println("Decrypted text: " + new String(decrypted, StandardCharsets.UTF_8));
        System.out.println("Original text recovered: " +
                java.util.Arrays.equals(plainBytes, decrypted));
        scanner.close();
    }
}
