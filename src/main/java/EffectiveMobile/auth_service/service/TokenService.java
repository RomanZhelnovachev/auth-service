package EffectiveMobile.auth_service.service;

import EffectiveMobile.auth_service.entity.Token;
import EffectiveMobile.auth_service.exception.TokenNotFoundException;
import EffectiveMobile.auth_service.exception.TokenNotFoundExceptionByUserId;
import EffectiveMobile.auth_service.repository.redis.TokenRepository;
import EffectiveMobile.auth_service.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Сервис для работы с токенами доступа
 * @author ZhelnovachevRoman
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenRepository repository;

    private static final Long TTL = 86400L;

    /**
     * Метод сохранения сгенерированного токена в базу
     * @param userId
     * @return значение токена
     */
    public String saveToken(String userId) {
        repository.findByUserId(userId).ifPresent(repository::delete);
        Token token = new Token(TokenGenerator.generate(), userId, TTL, Instant.now());
        repository.save(token);
        log.info("Токен для пользователя с ID {} успешно сохранён", userId);
        return token.getTokenValue();
    }

    /**
     * Метод поиска токена по значению токена
     * @param tokenValue
     * @return токен
     */
    public Token getTokenByValue(String tokenValue) {
        return repository.findById(tokenValue).orElseThrow(()-> new TokenNotFoundException(tokenValue));
    }
}
