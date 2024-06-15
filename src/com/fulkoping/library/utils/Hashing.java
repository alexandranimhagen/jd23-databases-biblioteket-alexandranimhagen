package com.fulkoping.library.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hashing {
    private static final int SALT_LENGTH = 16;

    public static String encrypt(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashedPassword = digest.digest(password.getBytes());

            String finalHash = byteArrayToHexString(hashedPassword) + byteArrayToHexString(salt);
            return finalHash;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean verify(String password, String hashedPassword) {
        if (hashedPassword.length() != 64 + SALT_LENGTH * 2) {
            System.out.println("Incorrect hashed password length: " + hashedPassword.length());
            return false;
        }

        String passwordHash = hashedPassword.substring(0, 64);
        String saltHex = hashedPassword.substring(64);
        byte[] salt = hexStringToByteArray(saltHex);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashedInputPassword = digest.digest(password.getBytes());
            String hashedInputPasswordHex = byteArrayToHexString(hashedInputPassword);

            return passwordHash.equals(hashedInputPasswordHex);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static String byteArrayToHexString(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        int length = s.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
