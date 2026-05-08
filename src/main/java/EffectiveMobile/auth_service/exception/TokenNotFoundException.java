package EffectiveMobile.auth_service.exception;

/**
 * Исключение, выбрасываемое при отсутствии токена в базе
 * @author ZhelnovachevRoman
 */
public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String tokenValue) {
        super("Токен " + tokenValue + " не найден");
    }
}
