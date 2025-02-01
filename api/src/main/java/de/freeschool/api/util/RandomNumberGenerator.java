package de.freeschool.api.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomNumberGenerator {
    private static final int NUM_DIGITS = 10;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String generateUid(String domain, String type) {
        return String.format("%s:%s:%s", domain, type, generateRandomString(12));
    }

    public static String generateRandomNumberString() {
        return generateRandomNumberString(NUM_DIGITS);
    }

    public static String generateRandomNumberString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomDigit = random.nextInt(10);
            sb.append(randomDigit);
        }
        return sb.toString();
    }

    public static String generateChecksum(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate checksum", e);
        }
    }
}