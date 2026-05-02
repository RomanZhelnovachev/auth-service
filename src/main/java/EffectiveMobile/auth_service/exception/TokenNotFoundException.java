package EffectiveMobile.auth_service.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String tokenValue) {
        super("Токен " + tokenValue + " не найден");
    }
}
