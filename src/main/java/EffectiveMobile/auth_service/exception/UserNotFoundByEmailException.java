package EffectiveMobile.auth_service.exception;

/**
 * Исключение, выбрасываемое при отсутствии пользователя с таким e-mail
 * @author ZhelnovachevRoman
 */
public class UserNotFoundByEmailException extends RuntimeException {
    public UserNotFoundByEmailException(String email) {
        super("Пользователь с Email " + email + " не найден");
    }
}
