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
import EffectiveMobile.auth_service.service.TokenService;
import EffectiveMobile.auth_service.util.CodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("При успешной регистрации пользователь сохраняется в базу и в кафке отправляется событие")
    void successRegisterUser() {

        when(repository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        try (MockedStatic<CodeGenerator> mocked = Mockito.mockStatic(CodeGenerator.class)) {
            mocked.when(CodeGenerator::generate).thenReturn("1234");
            authService.registerUser(registerDto());
        }
        verify(repository).save(argThat(user -> user.getEmail().equals(EMAIL) && user.getCode().equals("1234") && !user.isConfirmed()));
        verify(producer).send(any(RegisterEvent.class));
    }

    @Test
    @DisplayName("Если пользователь уже существует и почта подтверждена, выбрасывается исключение")
    void invalidRegisterUser() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user(true, CODE)));
        assertThrows(UserAlreadyConfirmed.class, () -> authService.registerUser(registerDto()));verifyNoInteractions(producer);
    }


    @Test
    @DisplayName("Успешное подтверждение пользователя")
    void succesUserConfirmation() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user(false, "1234")));
        authService.userConfirmation(confirmDto());
        verify(repository).save(argThat(user -> user.getEmail().equals(EMAIL) && user.getCode().equals("1234") && user.isConfirmed()));
    }

    @Test
    @DisplayName("При подтверждении введён неверный код")
    void invalidCodeByUserConfirmation() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user(false, "1235")));
        assertThrows(InvalidCodeException.class, ()-> authService.userConfirmation(confirmDto()));
    }

    private RegisterDto registerDto(){
        return new RegisterDto(EMAIL);
    }

    private ConfirmDto confirmDto(){
        return new ConfirmDto(EMAIL, CODE);
    }

    private User user(boolean confirmed, String code){
        return new User(UUID.randomUUID(), EMAIL, confirmed, code, Instant.now(), null);
    }
}