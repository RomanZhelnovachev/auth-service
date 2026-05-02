package EffectiveMobile.auth_service.dto;

import java.time.Instant;

public record UserDto(
        String email,
        Instant timeCreated
) {
}
