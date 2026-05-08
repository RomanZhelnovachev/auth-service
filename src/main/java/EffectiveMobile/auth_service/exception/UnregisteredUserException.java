package EffectiveMobile.auth_service.exception;

/**
 * Исключение, выбрасываемое если пользователь не запрашивал код доступа, а кто-то пытается ввести код под определённым e=mail
 * @author ZhelnovachevRoman
 */
public class UnregisteredUserException extends RuntimeException {
    public UnregisteredUserException(String email) {
        super("Пользователь с EMAIL: " + email + " не запрашивал код доступа");
    }
}
