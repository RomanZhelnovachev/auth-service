package EffectiveMobile.auth_service.dto;

import java.time.Instant;

/**
 * Событие для кафки о регистрации нового пользователя в системе
 * @param eventId
 * @param email
 * @param code
 * @param sendTime
 * @author ZhelnovachevRoman
 */
public record RegisterEvent(
        String eventId,
        String email,
        String code,
        Instant sendTime
) {
}
