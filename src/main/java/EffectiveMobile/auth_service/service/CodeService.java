package EffectiveMobile.auth_service.service;

import EffectiveMobile.auth_service.entity.Code;
import EffectiveMobile.auth_service.exception.UnregisteredUserException;
import EffectiveMobile.auth_service.repository.redis.CodeRepository;
import EffectiveMobile.auth_service.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Сервис для работы с кодами подтверждения
 * @author ZhelnovachevRoman
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CodeService {

    private final CodeRepository repository;

    private static final Long TTL = 86400L;

    /**
     * Метод временного сохранения кода подтверждения в базу
     * @param email
     * @return четырёхзначный код подтверждения
     */
    public String generateCode(String email){
       String codeValue = CodeGenerator.generate();
        Code code = new Code(email, codeValue, TTL, Instant.now());
        repository.save(code);
        return codeValue;
    }

    /**
     * Метод верификации кода подтверждения
     * @param email
     * @param enteredCode
     * @return результат проверки (булевое значение)
     */
    public boolean codeVerification(String email, String enteredCode){
        Code code = repository.findById(email).orElseThrow(()-> new UnregisteredUserException(email));
        boolean access = false;
        if(code.getCode().equals(enteredCode)){
           access = true;
        }
        return access;
    }

    /**
     * Метод удаления кода подтверждения из базы
     * @param email
     */
    public void deleteCode(String email){
        repository.deleteById(email);
    }
}
