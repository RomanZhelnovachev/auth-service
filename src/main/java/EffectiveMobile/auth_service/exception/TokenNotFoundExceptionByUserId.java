package EffectiveMobile.auth_service.exception;

public class TokenNotFoundExceptionByUserId extends RuntimeException {
    public TokenNotFoundExceptionByUserId(String userId) {
        super("У пользователя с ID " + userId + " нет валидного токена");
    }
}
