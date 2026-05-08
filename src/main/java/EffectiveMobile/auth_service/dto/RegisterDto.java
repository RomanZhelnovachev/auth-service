package EffectiveMobile.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO для регистрации нового пользователя
 * @param email
 * @author ZhelnovachevRoman
 */
public record RegisterDto(

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат")
        String email
) {
}
