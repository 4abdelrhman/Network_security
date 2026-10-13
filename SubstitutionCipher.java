import java.util.Scanner;

public class SubstitutionCipher {
    // MWERTYUIOPASDFGHJKLZXCVBNQ

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // Encryption
    public static String encrypt(String plaintext, String key) {
        key = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                result.append(key.charAt(c - 'A'));
            } else if (c >= 'a' && c <= 'z') {
                result.append(Character.toLowerCase(key.charAt(c - 'a')));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // Decryption
    public static String decrypt(String ciphertext, String key) {
        key = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            int index = key.indexOf(Character.toUpperCase(c));
            if (c >= 'A' && c <= 'Z' && index != -1) {
                result.append(ALPHABET.charAt(index));
            } else if (c >= 'a' && c <= 'z' && index != -1) {
                result.append(Character.toLowerCase(ALPHABET.charAt(index)));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static boolean isValidKey(String key) {
        if (key == null || key.length() != 26) {
            return false;
        }
        boolean[] seen = new boolean[26];
        for (char c : key.toUpperCase().toCharArray()) {
            if (c < 'A' || c > 'Z' || seen[c - 'A']) {
                return false;
            }
            seen[c - 'A'] = true;
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Substitution Cipher ===");
            System.out.println("  1. Encrypt");
            System.out.println("  2. Decrypt");
            System.out.println("  3. Exit");
            System.out.print("Choose an option (1-3): ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("3")) {
                System.out.println("Goodbye!");
                break;
            }
            if (!choice.equals("1") && !choice.equals("2")) {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                continue;
            }
            boolean encrypting = choice.equals("1");
            System.out.print(encrypting
                    ? "Enter the plaintext message: "
                    : "Enter the ciphertext message: ");
            String text = scanner.nextLine();
            String key;
            while (true) {
                System.out.print("Enter the substitution key (26 unique letters): ");
                key = scanner.nextLine().trim();
                if (isValidKey(key)) {
                    break;
                }
                System.out.println("Invalid key! It must contain all 26 English letters exactly once.");
            }
            if (encrypting) {
                System.out.println("Ciphertext: " + encrypt(text, key));
            } else {
                System.out.println("Plaintext: " + decrypt(text, key));
            }
        }
        scanner.close();
    }
}