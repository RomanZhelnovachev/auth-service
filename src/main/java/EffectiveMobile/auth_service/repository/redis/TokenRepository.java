package EffectiveMobile.auth_service.repository.redis;

import EffectiveMobile.auth_service.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для хранения токенов
 * @author ZhelnovachevRoman
 */
@Repository
public interface TokenRepository extends CrudRepository<Token, String> {
    Optional<Token> findByUserId(String userId);

}
