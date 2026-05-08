package EffectiveMobile.auth_service.dto;

import java.time.Instant;

/**
 * DTO при запросе своей страницы пользователем
 * @param email
 * @param timeCreated
 * @author ZhelnovachevRoman
 */
public record UserDto(
        String email,
        Instant timeCreated
) {
}
