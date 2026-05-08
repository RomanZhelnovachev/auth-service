package EffectiveMobile.auth_service.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;

/**
 * Сущность токена доступа
 * <p>
 *     Модель данных для redis-таблицы token
 * </p>
 *
 * @author ZhelnovachevRoman
 */
@RedisHash("token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    private String tokenValue;

    private String userId;

    @TimeToLive
    private Long ttl;

    private Instant timeCreated;
}
