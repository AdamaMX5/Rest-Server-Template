package de.freeschool.api.util;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Helper class for symmetrical password encryption/decryption of data.
 */
public class PasswordEncryption {
    private static final String ALGORITHM = "AES";
    private static final String PBKDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 128;

    public static class BadPasswordException extends RuntimeException {
        BadPasswordException(Exception e) {
            super(e);
        }
    }

    public static class WeakPasswordException extends Exception {
        WeakPasswordException(String message) {
            super(message);
        }
    }

    public static String encrypt(String valueToEnc, String password, String salt) throws BadPasswordException {
        try {
            SecretKey key = generateKeyFromPassword(password, salt);
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encValue = c.doFinal(valueToEnc.getBytes());
            return Base64.getEncoder().encodeToString(encValue);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new BadPasswordException(e);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedValue, String password, String salt) throws BadPasswordException {
        Cipher c;
        try {
            SecretKey key = generateKeyFromPassword(password, salt);
            c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            throw new BadPasswordException(e);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKey generateKeyFromPassword(String password,
                                                     String salt) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), ITERATIONS,
                KEY_LENGTH
        );
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    public static void ensureStrongPassword(String password) throws WeakPasswordException {
        // Check if password is at least 10 characters long
        if (password.length() < 10) {
            throw new WeakPasswordException("password.too_short");
        }

        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new WeakPasswordException("password.needs_uppercase");
        }

        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new WeakPasswordException("password.needs_lowercase");
        }

        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            throw new WeakPasswordException("password.needs_digit");
        }

        // Check for at least one special character from .,:;!?()
        if (!password.matches(".*[.:;,!?()$%&#].*")) {
            throw new WeakPasswordException("password.needs_special");
        }

        // Check if password contains whitespace characters
        if (password.matches(".*\\s.*")) {
            throw new WeakPasswordException("password.contains_whitespace");
        }
    }
}
