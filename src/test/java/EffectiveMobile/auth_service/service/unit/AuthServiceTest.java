package EffectiveMobile.auth_service.service.unit;

import EffectiveMobile.auth_service.dto.ConfirmDto;
import EffectiveMobile.auth_service.dto.RegisterDto;
import EffectiveMobile.auth_service.dto.RegisterEvent;
import EffectiveMobile.auth_service.entity.User;
import EffectiveMobile.auth_service.exception.InvalidCodeException;
import EffectiveMobile.auth_service.exception.UserAlreadyConfirmed;
import EffectiveMobile.auth_service.kafka.RegisterProducer;
import EffectiveMobile.auth_service.repository.jpa.UserRepository;
import EffectiveMobile.auth_service.service.AuthService;
import EffectiveMobile.auth_service.service.CodeService;
import EffectiveMobile.auth_service.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "test@test.com";
    private static final String CODE = "1234";

    @Mock
    private UserRepository repository;

    @Mock
    private TokenService service;

    @Mock
    private RegisterProducer producer;

    @Mock
    private CodeService codeService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("При успешной регистрации пользователь сохраняется в базу и в кафке отправляется событие")
    void successRegisterUser() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        authService.registerUser(registerDto());
        verify(repository).save(argThat(user -> user.getEmail().equals(EMAIL) && !user.isConfirmed()));
        verify(producer).send(any(RegisterEvent.class));
    }

    @Test
    @DisplayName("Если пользователь уже существует и почта подтверждена, выбрасывается исключение")
    void invalidRegisterUser() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user(true)));
        assertThrows(UserAlreadyConfirmed.class, () -> authService.registerUser(registerDto()));verifyNoInteractions(producer);
    }


    @Test
    @DisplayName("Успешное подтверждение пользователя")
    void succesUserConfirmation() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user(false)));
        when(codeService.codeVerification(EMAIL, CODE)).thenReturn(true);
        authService.userConfirmation(confirmDto());
        verify(repository).save(argThat(user -> user.getEmail().equals(EMAIL) && user.isConfirmed()));
    }

    @Test
    @DisplayName("При подтверждении введён неверный код")
    void invalidCodeByUserConfirmation() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user(false)));
        when(codeService.codeVerification(EMAIL, CODE)).thenReturn(false);
        assertThrows(InvalidCodeException.class, ()-> authService.userConfirmation(confirmDto()));
    }

    private RegisterDto registerDto(){
        return new RegisterDto(EMAIL);
    }

    private ConfirmDto confirmDto(){
        return new ConfirmDto(EMAIL, CODE);
    }

    private User user(boolean confirmed){
        return new User(UUID.randomUUID(), EMAIL, confirmed, Instant.now(), null);
    }
}