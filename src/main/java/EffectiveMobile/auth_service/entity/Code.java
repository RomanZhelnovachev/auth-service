package EffectiveMobile.auth_service.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;

/**
 * Сущность кода подтверждения
 * <p>
 *     Модель данных для redis-таблицы code
 * </p>
 *
 * @author ZhelnovachevRoman
 */
@RedisHash("code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Code {

    @Id
    private String email;

    private String code;

    @TimeToLive
    private Long ttl;

    private Instant timeCreated;
}
