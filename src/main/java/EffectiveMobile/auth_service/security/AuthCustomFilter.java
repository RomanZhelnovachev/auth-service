package EffectiveMobile.auth_service.security;

import EffectiveMobile.auth_service.entity.Token;
import EffectiveMobile.auth_service.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthCustomFilter extends OncePerRequestFilter {

    private final TokenService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws
            ServletException,
            IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/verify")) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request,
                    response);
            return;
        }
        String tokenValue = header.substring(7);
        Token token = service.getTokenByValue(tokenValue);
        AuthPrincipal principal = new AuthPrincipal(UUID.fromString(token.getUserId()));
        Authentication authentication = new AuthToken(principal,
                tokenValue);
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        filterChain.doFilter(request,
                response);
    }
}
