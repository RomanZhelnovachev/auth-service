package EffectiveMobile.auth_service.exception;

public class UserNotFoundByEmailException extends RuntimeException {
    public UserNotFoundByEmailException(String email) {
        super("Пользователь с Email " + email + " не найден");
    }
}
