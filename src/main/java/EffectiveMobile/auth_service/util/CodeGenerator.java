package EffectiveMobile.auth_service.util;

import java.security.SecureRandom;

/**
 * Утилитарный класс для генерации кодов подтверждения
 * @author ZhelnovachevRoman
 */
public class CodeGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generate() {
        int number = SECURE_RANDOM.nextInt(10_000);
        return String.format("%04d", number);
    }
}
