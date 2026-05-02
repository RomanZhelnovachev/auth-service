package EffectiveMobile.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmDto(

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат")
        String email,

        @NotBlank(message = "Код подтверждения не может быть пустым")
        @Pattern(regexp = "^[0-9]{4}$", message = "Код должен содержать ровно 4 цифры")
        String code
) {
}
