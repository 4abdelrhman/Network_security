import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LFSRCipher {
    // Primitive polynomial table (m = 2 to 168).
    // Source: R. Ward and T. Molteno, "Table of Linear Feedback Shift Registers"
    // (University of Otago, 2007), using the entry with the fewest taps for each m.
    // Key = m, value = exponents of the polynomial (e.g. m = 4 -> {4, 3, 0} = x^4 +
    // x^3 + 1).
    private static final Map<Integer, int[]> TABLE = new HashMap<>();
    static {
        TABLE.put(2, new int[] { 2, 1, 0 });
        TABLE.put(3, new int[] { 3, 2, 0 });
        TABLE.put(4, new int[] { 4, 3, 0 });
        TABLE.put(5, new int[] { 5, 3, 0 });
        TABLE.put(6, new int[] { 6, 5, 0 });
        TABLE.put(7, new int[] { 7, 6, 0 });
        TABLE.put(8, new int[] { 8, 6, 5, 4, 0 });
        TABLE.put(9, new int[] { 9, 5, 0 });
        TABLE.put(10, new int[] { 10, 7, 0 });
        TABLE.put(11, new int[] { 11, 9, 0 });
        TABLE.put(12, new int[] { 12, 11, 8, 6, 0 });
        TABLE.put(13, new int[] { 13, 12, 10, 9, 0 });
        TABLE.put(14, new int[] { 14, 13, 11, 9, 0 });
        TABLE.put(15, new int[] { 15, 14, 0 });
        TABLE.put(16, new int[] { 16, 14, 13, 11, 0 });
        TABLE.put(17, new int[] { 17, 14, 0 });
        TABLE.put(18, new int[] { 18, 11, 0 });
        TABLE.put(19, new int[] { 19, 18, 17, 14, 0 });
        TABLE.put(20, new int[] { 20, 17, 0 });
        TABLE.put(21, new int[] { 21, 19, 0 });
        TABLE.put(22, new int[] { 22, 21, 0 });
        TABLE.put(23, new int[] { 23, 18, 0 });
        TABLE.put(24, new int[] { 24, 23, 21, 20, 0 });
        TABLE.put(25, new int[] { 25, 22, 0 });
        TABLE.put(26, new int[] { 26, 25, 24, 20, 0 });
        TABLE.put(27, new int[] { 27, 26, 25, 22, 0 });
        TABLE.put(28, new int[] { 28, 25, 0 });
        TABLE.put(29, new int[] { 29, 27, 0 });
        TABLE.put(30, new int[] { 30, 29, 26, 24, 0 });
        TABLE.put(31, new int[] { 31, 28, 0 });
        TABLE.put(32, new int[] { 32, 30, 26, 25, 0 });
        TABLE.put(33, new int[] { 33, 20, 0 });
        TABLE.put(34, new int[] { 34, 31, 30, 26, 0 });
        TABLE.put(35, new int[] { 35, 33, 0 });
        TABLE.put(36, new int[] { 36, 25, 0 });
        TABLE.put(37, new int[] { 37, 36, 33, 31, 0 });
        TABLE.put(38, new int[] { 38, 37, 33, 32, 0 });
        TABLE.put(39, new int[] { 39, 35, 0 });
        TABLE.put(40, new int[] { 40, 37, 36, 35, 0 });
        TABLE.put(41, new int[] { 41, 38, 0 });
        TABLE.put(42, new int[] { 42, 40, 37, 35, 0 });
        TABLE.put(43, new int[] { 43, 42, 38, 37, 0 });
        TABLE.put(44, new int[] { 44, 42, 39, 38, 0 });
        TABLE.put(45, new int[] { 45, 44, 42, 41, 0 });
        TABLE.put(46, new int[] { 46, 40, 39, 38, 0 });
        TABLE.put(47, new int[] { 47, 42, 0 });
        TABLE.put(48, new int[] { 48, 44, 41, 39, 0 });
        TABLE.put(49, new int[] { 49, 40, 0 });
        TABLE.put(50, new int[] { 50, 48, 47, 46, 0 });
        TABLE.put(51, new int[] { 51, 50, 48, 45, 0 });
        TABLE.put(52, new int[] { 52, 49, 0 });
        TABLE.put(53, new int[] { 53, 52, 51, 47, 0 });
        TABLE.put(54, new int[] { 54, 51, 48, 46, 0 });
        TABLE.put(55, new int[] { 55, 31, 0 });
        TABLE.put(56, new int[] { 56, 54, 52, 49, 0 });
        TABLE.put(57, new int[] { 57, 50, 0 });
        TABLE.put(58, new int[] { 58, 39, 0 });
        TABLE.put(59, new int[] { 59, 57, 55, 52, 0 });
        TABLE.put(60, new int[] { 60, 59, 0 });
        TABLE.put(61, new int[] { 61, 60, 59, 56, 0 });
        TABLE.put(62, new int[] { 62, 59, 57, 56, 0 });
        TABLE.put(63, new int[] { 63, 62, 0 });
        TABLE.put(64, new int[] { 64, 63, 61, 60, 0 });
        TABLE.put(65, new int[] { 65, 47, 0 });
        TABLE.put(66, new int[] { 66, 60, 58, 57, 0 });
        TABLE.put(67, new int[] { 67, 66, 65, 62, 0 });
        TABLE.put(68, new int[] { 68, 59, 0 });
        TABLE.put(69, new int[] { 69, 67, 64, 63, 0 });
        TABLE.put(70, new int[] { 70, 69, 67, 65, 0 });
        TABLE.put(71, new int[] { 71, 65, 0 });
        TABLE.put(72, new int[] { 72, 69, 63, 62, 0 });
        TABLE.put(73, new int[] { 73, 48, 0 });
        TABLE.put(74, new int[] { 74, 71, 70, 67, 0 });
        TABLE.put(75, new int[] { 75, 74, 72, 69, 0 });
        TABLE.put(76, new int[] { 76, 74, 72, 71, 0 });
        TABLE.put(77, new int[] { 77, 75, 72, 71, 0 });
        TABLE.put(78, new int[] { 78, 77, 76, 71, 0 });
        TABLE.put(79, new int[] { 79, 70, 0 });
        TABLE.put(80, new int[] { 80, 78, 76, 71, 0 });
        TABLE.put(81, new int[] { 81, 77, 0 });
        TABLE.put(82, new int[] { 82, 78, 76, 73, 0 });
        TABLE.put(83, new int[] { 83, 81, 79, 76, 0 });
        TABLE.put(84, new int[] { 84, 71, 0 });
        TABLE.put(85, new int[] { 85, 84, 83, 77, 0 });
        TABLE.put(86, new int[] { 86, 84, 81, 80, 0 });
        TABLE.put(87, new int[] { 87, 74, 0 });
        TABLE.put(88, new int[] { 88, 80, 79, 77, 0 });
        TABLE.put(89, new int[] { 89, 51, 0 });
        TABLE.put(90, new int[] { 90, 88, 87, 85, 0 });
        TABLE.put(91, new int[] { 91, 90, 86, 83, 0 });
        TABLE.put(92, new int[] { 92, 90, 87, 86, 0 });
        TABLE.put(93, new int[] { 93, 91, 0 });
        TABLE.put(94, new int[] { 94, 73, 0 });
        TABLE.put(95, new int[] { 95, 84, 0 });
        TABLE.put(96, new int[] { 96, 90, 87, 86, 0 });
        TABLE.put(97, new int[] { 97, 91, 0 });
        TABLE.put(98, new int[] { 98, 87, 0 });
        TABLE.put(99, new int[] { 99, 95, 94, 92, 0 });
        TABLE.put(100, new int[] { 100, 63, 0 });
        TABLE.put(101, new int[] { 101, 100, 95, 94, 0 });
        TABLE.put(102, new int[] { 102, 99, 97, 96, 0 });
        TABLE.put(103, new int[] { 103, 94, 0 });
        TABLE.put(104, new int[] { 104, 103, 94, 93, 0 });
        TABLE.put(105, new int[] { 105, 89, 0 });
        TABLE.put(106, new int[] { 106, 91, 0 });
        TABLE.put(107, new int[] { 107, 105, 99, 98, 0 });
        TABLE.put(108, new int[] { 108, 77, 0 });
        TABLE.put(109, new int[] { 109, 107, 105, 104, 0 });
        TABLE.put(110, new int[] { 110, 109, 106, 104, 0 });
        TABLE.put(111, new int[] { 111, 101, 0 });
        TABLE.put(112, new int[] { 112, 108, 106, 101, 0 });
        TABLE.put(113, new int[] { 113, 104, 0 });
        TABLE.put(114, new int[] { 114, 113, 112, 103, 0 });
        TABLE.put(115, new int[] { 115, 110, 108, 107, 0 });
        TABLE.put(116, new int[] { 116, 114, 111, 110, 0 });
        TABLE.put(117, new int[] { 117, 116, 115, 112, 0 });
        TABLE.put(118, new int[] { 118, 85, 0 });
        TABLE.put(119, new int[] { 119, 111, 0 });
        TABLE.put(120, new int[] { 120, 118, 114, 111, 0 });
        TABLE.put(121, new int[] { 121, 103, 0 });
        TABLE.put(122, new int[] { 122, 121, 120, 116, 0 });
        TABLE.put(123, new int[] { 123, 121, 0 });
        TABLE.put(124, new int[] { 124, 87, 0 });
        TABLE.put(125, new int[] { 125, 120, 119, 118, 0 });
        TABLE.put(126, new int[] { 126, 124, 122, 119, 0 });
        TABLE.put(127, new int[] { 127, 126, 0 });
        TABLE.put(128, new int[] { 128, 127, 126, 121, 0 });
        TABLE.put(129, new int[] { 129, 124, 0 });
        TABLE.put(130, new int[] { 130, 127, 0 });
        TABLE.put(131, new int[] { 131, 129, 128, 123, 0 });
        TABLE.put(132, new int[] { 132, 103, 0 });
        TABLE.put(133, new int[] { 133, 131, 125, 124, 0 });
        TABLE.put(134, new int[] { 134, 77, 0 });
        TABLE.put(135, new int[] { 135, 124, 0 });
        TABLE.put(136, new int[] { 136, 134, 133, 128, 0 });
        TABLE.put(137, new int[] { 137, 116, 0 });
        TABLE.put(138, new int[] { 138, 137, 131, 130, 0 });
        TABLE.put(139, new int[] { 139, 136, 134, 131, 0 });
        TABLE.put(140, new int[] { 140, 111, 0 });
        TABLE.put(141, new int[] { 141, 140, 135, 128, 0 });
        TABLE.put(142, new int[] { 142, 121, 0 });
        TABLE.put(143, new int[] { 143, 141, 140, 138, 0 });
        TABLE.put(144, new int[] { 144, 142, 140, 137, 0 });
        TABLE.put(145, new int[] { 145, 93, 0 });
        TABLE.put(146, new int[] { 146, 144, 143, 141, 0 });
        TABLE.put(147, new int[] { 147, 145, 143, 136, 0 });
        TABLE.put(148, new int[] { 148, 121, 0 });
        TABLE.put(149, new int[] { 149, 142, 140, 139, 0 });
        TABLE.put(150, new int[] { 150, 97, 0 });
        TABLE.put(151, new int[] { 151, 148, 0 });
        TABLE.put(152, new int[] { 152, 150, 149, 146, 0 });
        TABLE.put(153, new int[] { 153, 152, 0 });
        TABLE.put(154, new int[] { 154, 153, 149, 145, 0 });
        TABLE.put(155, new int[] { 155, 151, 150, 148, 0 });
        TABLE.put(156, new int[] { 156, 153, 151, 147, 0 });
        TABLE.put(157, new int[] { 157, 155, 152, 151, 0 });
        TABLE.put(158, new int[] { 158, 153, 152, 150, 0 });
        TABLE.put(159, new int[] { 159, 128, 0 });
        TABLE.put(160, new int[] { 160, 158, 157, 155, 0 });
        TABLE.put(161, new int[] { 161, 143, 0 });
        TABLE.put(162, new int[] { 162, 158, 155, 154, 0 });
        TABLE.put(163, new int[] { 163, 160, 157, 156, 0 });
        TABLE.put(164, new int[] { 164, 159, 158, 152, 0 });
        TABLE.put(165, new int[] { 165, 162, 157, 156, 0 });
        TABLE.put(166, new int[] { 166, 164, 163, 156, 0 });
        TABLE.put(167, new int[] { 167, 161, 0 });
        TABLE.put(168, new int[] { 168, 162, 159, 152, 0 });
    }
    private static final int TABLE_ROWS = 20;
    static class LFSR {
        private final int m;
        private final int[] taps;
        private final int[] initial;
        private int[] reg;
        LFSR(int m, int[] exponents, int[] initial) {
            this.m = m;
            this.initial = initial.clone();
            int[] t = new int[exponents.length - 1];
            for (int i = 1; i < exponents.length; i++) {
                t[i - 1] = exponents[i];
            }
            this.taps = t;
            reset();
        }
        void reset() {
            reg = initial.clone();
        }
        int[] state() {
            return reg;
        }
        int clock() {
            int out = reg[0];
            int feedback = 0;
            for (int e : taps) {
                feedback ^= reg[e];
            }
            System.arraycopy(reg, 1, reg, 0, m - 1);
            reg[m - 1] = feedback;
            return out;
        }
        int feedbackBit() {
            int feedback = 0;
            for (int e : taps) {
                feedback ^= reg[e];
            }
            return feedback;
        }
    }
    static int[] randomInitialVector(int m) {
        SecureRandom rnd = new SecureRandom();
        int[] v = new int[m];
        boolean nonZero = false;
        while (!nonZero) {
            for (int i = 0; i < m; i++) {
                v[i] = rnd.nextInt(2);
                if (v[i] == 1) {
                    nonZero = true;
                }
            }
        }
        return v;
    }
    static int[] bytesToBits(byte[] data) {
        int[] bits = new int[data.length * 8];
        for (int i = 0; i < data.length; i++) {
            for (int b = 0; b < 8; b++) {
                bits[i * 8 + b] = (data[i] >> (7 - b)) & 1;
            }
        }
        return bits;
    }
    static byte[] bitsToBytes(int[] bits) {
        byte[] data = new byte[bits.length / 8];
        for (int i = 0; i < data.length; i++) {
            int v = 0;
            for (int b = 0; b < 8; b++) {
                v = (v << 1) | bits[i * 8 + b];
            }
            data[i] = (byte) v;
        }
        return data;
    }
    static String bitsToString(int[] bits, int m) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bits.length; i++) {
            sb.append(bits[i]);
            if (m <= 16 && i < bits.length - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
    static String polynomialToString(int[] exps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < exps.length; i++) {
            int e = exps[i];
            if (i > 0) {
                sb.append(" + ");
            }
            if (e == 0) {
                sb.append("1");
            } else if (e == 1) {
                sb.append("x");
            } else {
                sb.append("x^").append(e);
            }
        }
        return sb.toString();
    }
    static String printable(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            int c = b & 0xFF;
            sb.append(c >= 32 && c < 127 ? (char) c : '.');
        }
        return sb.toString();
    }
    static String hex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString().trim();
    }
    //Encryption
    static int[] encrypt(int[] messageBits, LFSR lfsr, boolean showTable, int m) {
        int[] out = new int[messageBits.length];
        if (showTable) {
            int rows = Math.min(TABLE_ROWS, messageBits.length);
            System.out.println("\nClock / flip-flop table (first " + rows + " rows"
                    + (messageBits.length > rows ? " of " + messageBits.length : "") + "):");
            System.out.println("Clock | Flip-flops (FF1 ... FF" + m + ") | Feedback | Output | Msg bit | Cipher bit");
        }
        for (int i = 0; i < messageBits.length; i++) {
            int[] before = lfsr.state().clone();
            int feedback = lfsr.feedbackBit();
            int key = lfsr.clock();
            out[i] = messageBits[i] ^ key;
            if (showTable && i < TABLE_ROWS) {
                System.out.printf("%5d | %s | %8d | %6d | %7d | %10d%n",
                        i, bitsToString(before, m), feedback, key, messageBits[i], out[i]);
            }
        }
        return out;
    }
    //Decryption
    static int[] decrypt(int[] cipherBits, int m, int[] exponents, int[] initialVector) {
        LFSR lfsr = new LFSR(m, exponents, initialVector);
        int[] out = new int[cipherBits.length];
        for (int i = 0; i < cipherBits.length; i++) {
            out[i] = cipherBits[i] ^ lfsr.clock();
        }
        return out;
    }
    static int readM(Scanner scanner) {
        while (true) {
            System.out.print("Enter m, the number of flip-flops (2 to 168): ");
            String line = scanner.nextLine().trim();
            try {
                int m = Integer.parseInt(line);
                if (TABLE.containsKey(m)) {
                    return m;
                }
                System.out.println("m = " + m + " is not in the polynomial table. Try again.");
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
    static int[] readInitialVector(Scanner scanner, int m) {
        while (true) {
            System.out.print("Enter the initial vector (" + m + " bits of 0 and 1): ");
            String line = scanner.nextLine().replace(" ", "");
            if (line.length() != m || !line.matches("[01]+") || !line.contains("1")) {
                System.out.println("Invalid vector: it needs exactly " + m + " bits, only 0 and 1, and not all zeros.");
                continue;
            }
            int[] v = new int[m];
            for (int i = 0; i < m; i++) {
                v[i] = line.charAt(i) - '0';
            }
            return v;
        }
    }
    static byte[] readHex(Scanner scanner) {
        while (true) {
            System.out.print("Enter the ciphertext in hex (e.g. A7 E2 8C): ");
            String line = scanner.nextLine().replace(" ", "");
            if (line.isEmpty() || line.length() % 2 != 0 || !line.matches("[0-9a-fA-F]+")) {
                System.out.println("Invalid hex. Use an even number of hex digits.");
                continue;
            }
            byte[] data = new byte[line.length() / 2];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) Integer.parseInt(line.substring(i * 2, i * 2 + 2), 16);
            }
            return data;
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int lastM = 0;
        int[] lastVector = null;
        byte[] lastCipher = null;
        while (true) {
            System.out.println("\n=== LFSR Cipher ===");
            System.out.println("  1. Encrypt");
            System.out.println("  2. Decrypt");
            System.out.println("  3. Exit");
            System.out.print("Choose an option (1-3): ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("3")) {
                System.out.println("Goodbye!");
                break;
            }
            if (choice.equals("1")) {
                System.out.print("Enter the message: ");
                String message = scanner.nextLine();
                if (message.isEmpty()) {
                    System.out.println("The message cannot be empty.");
                    continue;
                }
                int m = readM(scanner);
                int[] exponents = TABLE.get(m);
                System.out.println("\nPolynomial: " + polynomialToString(exponents));
                int[] initialVector = randomInitialVector(m);
                System.out.println("Initial vector (" + m + " bits): " + bitsToString(initialVector, m));
                int[] messageBits = bytesToBits(message.getBytes(StandardCharsets.UTF_8));
                LFSR lfsr = new LFSR(m, exponents, initialVector);
                int[] cipherBits = encrypt(messageBits, lfsr, true, m);
                byte[] cipherBytes = bitsToBytes(cipherBits);
                System.out.println("\n--- Encryption ---");
                System.out.println("Message bits   : " + bitsToString(messageBits, 8));
                System.out.println("Ciphertext bits: " + bitsToString(cipherBits, 8));
                System.out.println("Ciphertext hex : " + hex(cipherBytes));
                System.out.println(
                        "Ciphertext text: " + printable(cipherBytes) + "   (non-printable bytes shown as '.')");
                lastM = m;
                lastVector = initialVector;
                lastCipher = cipherBytes;
                System.out.println("\n(Saved m, initial vector and ciphertext for option 2.)");
            } else if (choice.equals("2")) {
                int m;
                int[] initialVector;
                byte[] cipherBytes;
                boolean useLast = false;
                if (lastCipher != null) {
                    System.out.print("Use the last encryption (m = " + lastM + ")? (y/n): ");
                    useLast = scanner.nextLine().trim().equalsIgnoreCase("y");
                }
                if (useLast) {
                    m = lastM;
                    initialVector = lastVector;
                    cipherBytes = lastCipher;
                } else {
                    m = readM(scanner);
                    initialVector = readInitialVector(scanner, m);
                    cipherBytes = readHex(scanner);
                }
                int[] exponents = TABLE.get(m);
                System.out.println("\nPolynomial: " + polynomialToString(exponents));
                System.out.println("Initial vector (" + m + " bits): " + bitsToString(initialVector, m));
                int[] cipherBits = bytesToBits(cipherBytes);
                int[] plainBits = decrypt(cipherBits, m, exponents, initialVector);
                byte[] plainBytes = bitsToBytes(plainBits);
                System.out.println("\n--- Decryption ---");
                System.out.println("Ciphertext bits: " + bitsToString(cipherBits, 8));
                System.out.println("Decrypted bits : " + bitsToString(plainBits, 8));
                System.out.println("Decrypted text : " + new String(plainBytes, StandardCharsets.UTF_8));
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        scanner.close();
    }
}