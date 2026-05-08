package EffectiveMobile.auth_service.exception;

/**
 * Исключение, выбрасываемое при отсутствии токена, закреплённого за конкретным пользователем
 * @author ZhelnovachevRoman
 */
public class TokenNotFoundExceptionByUserId extends RuntimeException {
    public TokenNotFoundExceptionByUserId(String userId) {
        super("У пользователя с ID " + userId + " нет валидного токена");
    }
}
