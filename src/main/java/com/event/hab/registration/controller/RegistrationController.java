package com.event.hab.registration.controller;

import com.event.hab.registration.DTO.RegistrationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    @GetMapping
    public RegistrationResponse getRegistration(){

    }

}
