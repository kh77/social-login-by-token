package com.sm.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureStringUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecureStringUtil.class);

    private static final String STRING_SEED = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom SECURE_RANDOM;

    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException for secure random: "+e.getMessage(),e);
        }
    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int secureRandomIndex = SECURE_RANDOM.nextInt(STRING_SEED.length());
            sb.append(STRING_SEED.charAt(secureRandomIndex));
        }

        return sb.toString();
    }

    public static boolean equals(String first, String second) {
        return MessageDigest.isEqual(first.getBytes(StandardCharsets.UTF_8), second.getBytes(StandardCharsets.UTF_8));
    }
}
