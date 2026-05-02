package EffectiveMobile.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCodeException.class)
    public ResponseEntity<AppError> catchInvalidCode(InvalidCodeException e) {
        return buildError(HttpStatus.BAD_REQUEST,
                e.getMessage());
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<AppError> catchTokenNotFound(TokenNotFoundException e) {
        return buildError(HttpStatus.UNAUTHORIZED,
                e.getMessage());
    }

    @ExceptionHandler(TokenNotFoundExceptionByUserId.class)
    public ResponseEntity<AppError> catchTokenNotFoundByUserId(TokenNotFoundExceptionByUserId e) {
        return buildError(HttpStatus.UNAUTHORIZED,
                e.getMessage());
    }

    @ExceptionHandler(UserAlreadyConfirmed.class)
    public ResponseEntity<AppError> catchUserAlreadyConfirmed(UserAlreadyConfirmed e) {
        return buildError(HttpStatus.BAD_REQUEST,
                e.getMessage());
    }

    @ExceptionHandler(UserNotFoundByEmailException.class)
    public ResponseEntity<AppError> catchUserNotFoundByEmail(UserNotFoundByEmailException e) {
        return buildError(HttpStatus.NOT_FOUND,
                e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AppError> catchUserNotFound(UserNotFoundException e) {
        return buildError(HttpStatus.NOT_FOUND,
                e.getMessage());
    }

    private ResponseEntity<AppError> buildError(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new AppError(status.value(), message));
    }
}
