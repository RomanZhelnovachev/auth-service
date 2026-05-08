package EffectiveMobile.auth_service.exception;

/**
 * Исключение, выбрасываемое при повторной попытке подтвердить уже подтверждённого пользователя
 * @author ZhelnovachevRoman
 */
public class UserAlreadyConfirmed extends RuntimeException {
    public UserAlreadyConfirmed(String email) {
        super("Пользователь с Email " + email + " уже подтверждён");
    }
}
