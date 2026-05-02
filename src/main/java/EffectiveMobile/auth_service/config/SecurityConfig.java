package EffectiveMobile.auth_service.config;

import EffectiveMobile.auth_service.security.AuthCustomFilter;
import EffectiveMobile.auth_service.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity){
        return httpSecurity.csrf(csrf -> csrf.disable())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, ("/api/v1/auth/register")).permitAll()
                        .requestMatchers(HttpMethod.POST, ("/api/v1/auth/verify")).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new AuthCustomFilter(service), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
