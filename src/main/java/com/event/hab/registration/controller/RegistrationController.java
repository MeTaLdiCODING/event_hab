package com.event.hab.registration.controller;

import com.event.hab.registration.DTO.ParticipantResponse;
import com.event.hab.registration.DTO.RegistrationResponse;
import com.event.hab.registration.service.RegistrationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {
    final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    @GetMapping("/users/me/tickets")
    public List<RegistrationResponse> getAllRegistrations(){
       return registrationService.getAllRegistration();
    }

    @GetMapping("/users/me/tickets/{eventId}")
    public RegistrationResponse getRegistration(@PathVariable Long eventId){
       return registrationService.getRegistration(eventId);
    }

    @PostMapping("/events/{eventId}/register")
    public RegistrationResponse createRegistration(@PathVariable Long eventId){
       return registrationService.createRegistration(eventId);
    }

    @GetMapping("/events/{eventId}/participants")
    public List<ParticipantResponse> getRegistrationParticipants(@PathVariable Long eventId){
        return registrationService.getRegistrationParticipants(eventId);
    }

    @DeleteMapping("/events/{eventId}/register")
    public void deleteRegistration(@PathVariable Long eventId){
        registrationService.deleteRegistration(eventId);
    }



}
