package EffectiveMobile.auth_service.service;

import EffectiveMobile.auth_service.dto.ConfirmDto;
import EffectiveMobile.auth_service.dto.RegisterDto;
import EffectiveMobile.auth_service.dto.RegisterEvent;
import EffectiveMobile.auth_service.entity.User;
import EffectiveMobile.auth_service.dto.UserDto;
import EffectiveMobile.auth_service.exception.InvalidCodeException;
import EffectiveMobile.auth_service.exception.UserAlreadyConfirmed;
import EffectiveMobile.auth_service.exception.UserNotFoundByEmailException;
import EffectiveMobile.auth_service.exception.UserNotFoundException;
import EffectiveMobile.auth_service.kafka.RegisterProducer;
import EffectiveMobile.auth_service.repository.jpa.UserRepository;
import EffectiveMobile.auth_service.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository repository;

    private final TokenService service;

    private final RegisterProducer producer;

    @Transactional
    public void registerUser(RegisterDto dto) {
        String email = dto.email();
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
        String code = CodeGenerator.generate();
        user.setCode(code);
        repository.save(user);
        log.info("Код подтверждения отправлен на электронную почту {}", email);
        producer.send(new RegisterEvent(
                UUID.randomUUID().toString(),
                email,
                code,
                Instant.now()
        ));
    }

    @Transactional
    public String userConfirmation(ConfirmDto dto){
        String email = dto.email();
        String code = dto.code();
        User user = repository.findByEmail(email).orElseThrow(()-> new UserNotFoundByEmailException(email));
        if(user.isConfirmed()){
            log.info("{} - почта подтверждена", email);
            throw new UserAlreadyConfirmed(email);
        }
        if(!user.getCode().equals(code)){
            log.error("Введён неверный код подтверждения");
            throw new InvalidCodeException("Введён неверный код подтверждения");
        }
      user.setConfirmed(true);
        repository.save(user);
        log.info("Электронная почта {} подтверждена", email);
        return service.saveToken(user.getId().toString());
    }

    @Transactional(readOnly = true)
    public UserDto getUser(UUID userId){
        User user = getUserById(userId);
        return new UserDto(user.getEmail(), user.getTimeCreated());
    }

    private User getUserById(UUID userId){
        return repository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));
    }
}
