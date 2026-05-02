package EffectiveMobile.auth_service.exception;

public class UserAlreadyConfirmed extends RuntimeException {
    public UserAlreadyConfirmed(String email) {
        super("Пользователь с Email " + email + " уже подтверждён");
    }
}
