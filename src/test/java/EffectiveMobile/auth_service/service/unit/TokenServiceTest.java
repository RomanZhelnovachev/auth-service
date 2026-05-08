package EffectiveMobile.auth_service.service.unit;

import EffectiveMobile.auth_service.entity.Token;
import EffectiveMobile.auth_service.exception.TokenNotFoundException;
import EffectiveMobile.auth_service.repository.redis.TokenRepository;
import EffectiveMobile.auth_service.service.TokenService;
import EffectiveMobile.auth_service.util.TokenGenerator;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final Long TTL = 86400L;
    private static final String USER_ID = "testId";
    private static final String TOKEN = "testToken";
    private static final Instant TIME = Instant.parse("2026-05-06T00:00:00Z");

    @Mock
    private TokenRepository repository;

    @InjectMocks
    private TokenService service;

    @Test
    @DisplayName("Токен успешно сохраняется в базу")
    void successSaveToken() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        try (MockedStatic<TokenGenerator> mocked = Mockito.mockStatic(TokenGenerator.class)) {
            mocked.when(TokenGenerator::generate).thenReturn(TOKEN);
            service.saveToken(USER_ID);
        }
        verify(repository).save(argThat(token -> token.getTokenValue().equals(TOKEN) && token.getUserId().equals(USER_ID) && token.getTtl().equals(TTL)));
    }

    @Test
    @DisplayName("Токен успешно возвращается, если есть в базе")
    void successGetTokenByValue() {
        Token expected = token();
        when(repository.findById(TOKEN)).thenReturn(Optional.of(expected));
        Token actual = service.getTokenByValue(TOKEN);
        assertThat(actual.getTokenValue()).isEqualTo(TOKEN);
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getTtl()).isEqualTo(TTL);
    }

    @Test
    @DisplayName("Если токена нет в базе, выбрасывается исключение")
    void tokenNotExists(){
        when(repository.findById(TOKEN)).thenReturn(Optional.empty());
        assertThrows(TokenNotFoundException.class, ()-> service.getTokenByValue(TOKEN));
    }

    private Token token(){
        return new Token(TOKEN, USER_ID, TTL, TIME);
    }
}