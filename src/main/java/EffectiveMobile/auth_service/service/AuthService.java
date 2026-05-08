package EffectiveMobile.auth_service.service;

import EffectiveMobile.auth_service.dto.ConfirmDto;
import EffectiveMobile.auth_service.dto.RegisterDto;
import EffectiveMobile.auth_service.dto.RegisterEvent;
import EffectiveMobile.auth_service.entity.Code;
import EffectiveMobile.auth_service.entity.User;
import EffectiveMobile.auth_service.dto.UserDto;
import EffectiveMobile.auth_service.exception.InvalidCodeException;
import EffectiveMobile.auth_service.exception.UserAlreadyConfirmed;
import EffectiveMobile.auth_service.exception.UserNotFoundByEmailException;
import EffectiveMobile.auth_service.exception.UserNotFoundException;
import EffectiveMobile.auth_service.kafka.RegisterProducer;
import EffectiveMobile.auth_service.repository.jpa.UserRepository;
import EffectiveMobile.auth_service.repository.redis.CodeRepository;
import EffectiveMobile.auth_service.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Сервис для работы с пользователями системы
 * @author ZhelnovachevRoman
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository repository;

    private final TokenService service;

    private final RegisterProducer producer;

    private final CodeService codeService;

    /**
     * Метод регистрации нового пользователя
     * Если в базе уже есть подтверждённый пользователь, то выбрасывается соответствующее исключение
     * Новый пользователь сохраняется в базу, а сгенерированный код подтверждения отправляется по кафке в сервис нотификации
     * @param registerDto
     */
    @Transactional
    public void registerUser(RegisterDto registerDto) {
        String email = registerDto.email();
        User user = repository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .id(UUID.randomUUID())
                        .email(email)
                        .confirmed(false)
                        .build());
        if (user.isConfirmed()) {
            log.info("Пользователь с Email {} уже существует и почта подтверждена", email);
            throw new UserAlreadyConfirmed(email);
        }
        String codeValue = codeService.generateCode(email);
        repository.save(user);
        log.info("Код подтверждения отправлен на электронную почту {}", email);
        producer.send(new RegisterEvent(
                UUID.randomUUID().toString(),
                email,
                codeValue,
                Instant.now()
        ));
    }

    /**
     * Метод подтверждения пользователя
     * При успешном подтверждении пользователь помечается, как подтверждённый, код доступа удаляется из базы
     * @param confirmDto
     * @return значение выданного токена
     */
    @Transactional
    public String userConfirmation(ConfirmDto confirmDto){
        String email = confirmDto.email();
        String code = confirmDto.code();
        User user = repository.findByEmail(email).orElseThrow(()-> new UserNotFoundByEmailException(email));
        if(user.isConfirmed()){
            log.info("{} - почта подтверждена", email);
            throw new UserAlreadyConfirmed(email);
        }
        if(!codeService.codeVerification(email, code)){
            log.error("Введён неверный код подтверждения");
            throw new InvalidCodeException("Введён неверный код подтверждения");
        }
      user.setConfirmed(true);
        repository.save(user);
        codeService.deleteCode(email);
        log.info("Электронная почта {} подтверждена", email);
        return service.saveToken(user.getId().toString());
    }

    /**
     * Метод передачи данных о пользователе
     * @param userId
     * @return UserDto
     */
    @Transactional(readOnly = true)
    public UserDto getUser(UUID userId){
        User user = getUserById(userId);
        return new UserDto(user.getEmail(), user.getTimeCreated());
    }

    /**
     * Вспомогательный метод получения пользователя по ID
     * @param userId
     * @return сущность пользователя
     */
    private User getUserById(UUID userId){
        return repository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
    }
}
