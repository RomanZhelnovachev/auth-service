package EffectiveMobile.auth_service.repository.redis;

import EffectiveMobile.auth_service.entity.Code;
import org.springframework.data.repository.CrudRepository;

/**
 * Репозиторий для хранения кодов подтверждения
 * @author ZhelnovachevRoman
 */
public interface CodeRepository extends CrudRepository<Code, String> {
}
