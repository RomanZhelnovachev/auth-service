package EffectiveMobile.auth_service.exception;

/**
 * Исключение, выбрасываемое при передаче неверного кода подтверждения
 * @author ZhelnovachevRoman
 */
public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException(String message) {
        super(message);
    }
}
