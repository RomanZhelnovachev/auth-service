package EffectiveMobile.auth_service.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Утилитарный класс для генерации токена
 * @author ZhelnovachevRoman
 */
public class TokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;

    public static String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
