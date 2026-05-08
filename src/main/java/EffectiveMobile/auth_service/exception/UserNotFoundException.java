package EffectiveMobile.auth_service.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое при отсутствии пользователя с определённым ID
 * @author ZhelnovachevRoman
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super("Пользователь с ID " + userId + " не найден");
    }
}
