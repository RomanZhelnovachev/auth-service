package EffectiveMobile.auth_service.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthToken extends AbstractAuthenticationToken {

    private final AuthPrincipal principal;

    private final String token;

    public AuthToken(AuthPrincipal principal, String token) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return token;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }
}
