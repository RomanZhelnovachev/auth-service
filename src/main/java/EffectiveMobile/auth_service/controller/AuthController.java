package EffectiveMobile.auth_service.controller;

import EffectiveMobile.auth_service.dto.ConfirmDto;
import EffectiveMobile.auth_service.dto.RegisterDto;
import EffectiveMobile.auth_service.dto.UserDto;
import EffectiveMobile.auth_service.security.AuthPrincipal;
import EffectiveMobile.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public void register(@RequestBody
                         RegisterDto dto){
        System.out.println(">>> CONTROLLER HIT");
        service.registerUser(dto);
    }

    @PostMapping("/verify")
    public String confirmation(@RequestBody
                               ConfirmDto dto){
        return service.userConfirmation(dto);
    }

    @GetMapping("/me")
    public UserDto getUser(Authentication authentication){
        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();
        return service.getUser(principal.userId());
    }
}
