package EffectiveMobile.auth_service.exception;

public record AppError(

        int statusCode,
        String message
) {
}
