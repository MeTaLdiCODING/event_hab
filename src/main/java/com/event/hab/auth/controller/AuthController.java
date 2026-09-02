package com.event.hab.auth.controller;

import com.event.hab.auth.DTO.LoginRequest;
import com.event.hab.auth.DTO.RegisterRequest;
import com.event.hab.auth.Servise.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")

    public String register(@RequestBody RegisterRequest request){
    return authService.register(request);
    }

    @PostMapping("/login")

    public String login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

}
