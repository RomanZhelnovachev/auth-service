package EffectiveMobile.auth_service.dto;

import java.time.Instant;

public record RegisterEvent(
        String eventId,
        String email,
        String code,
        Instant sendTime
) {
}
