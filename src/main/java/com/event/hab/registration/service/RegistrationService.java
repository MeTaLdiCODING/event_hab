package com.event.hab.registration.service;

import com.event.hab.auth.model.User;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.common.castomException.EventNotFoundException;
import com.event.hab.common.castomException.RegistrationNotFoundException;
import com.event.hab.common.castomException.UserNotFoundException;
import com.event.hab.common.securityUtils.SecurityUtils;
import com.event.hab.events.model.Event;
import com.event.hab.events.model.Status;
import com.event.hab.events.repository.EventRepository;
import com.event.hab.registration.DTO.ParticipantResponse;
import com.event.hab.registration.DTO.RegistrationResponse;
import com.event.hab.registration.RegistrationMapper;
import com.event.hab.registration.model.Registration;
import com.event.hab.registration.model.RegistrationStatus;
import com.event.hab.registration.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {
    final UserRepository userRepository;
    final EventRepository eventRepository;
    final RegistrationRepository registrationRepository;
    final RegistrationMapper registrationMapper;
    public RegistrationService(
            UserRepository userRepository,
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            RegistrationMapper registrationMapper) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.registrationMapper = registrationMapper;
    }

    public List<RegistrationResponse> getAllRegistration() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        List<Registration> registrations = registrationRepository
                .findAllByUserIdAndRegistrationStatus(user.getId(), RegistrationStatus.CONFIRMED);
       return registrations.stream().map(registrationMapper::toResponse).toList();
    }

    public RegistrationResponse getRegistration(Long eventId) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Registration registration =registrationRepository
                .findByUserIdAndEventId(user.getId(),eventId)
                .orElseThrow(RegistrationNotFoundException::new);
        return registrationMapper.toResponse(registration);

    }

    public RegistrationResponse createRegistration(Long eventId) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        if(event.getStatus()!= Status.ACTIVE){
            throw new EventNotFoundException();//
        }
        if (event.getEventDate().isBefore(LocalDateTime.now())){
            throw new EventNotFoundException();
        }
        if (registrationRepository
                .existsByUserIdAndEventIdAndRegistrationStatus(user.getId(),eventId, RegistrationStatus.CONFIRMED)){
            throw new EventNotFoundException();
        }
        if ((event.getCurrentParticipants() >= event.getMaxParticipants())){
            throw new EventNotFoundException();
        }
        if (registrationRepository
                .findByUserIdAndEventId(user.getId(),eventId)
                .get().getRegistrationStatus() == RegistrationStatus.CANCELLED){
            registrationRepository
                    .save(registrationRepository
                            .findByUserIdAndEventId(user.getId(),eventId).get()
                            .setRegistrationStatus(RegistrationStatus.CONFIRMED));

        }
        else {
            Registration registration = new Registration();
            registration.setRegistrationStatus(RegistrationStatus.CONFIRMED);
            event.setCurrentParticipants(event.getCurrentParticipants()+1);
            registrationRepository.save(registration);
            eventRepository.save(event);


        }

        return null;
    }

    public List<ParticipantResponse> getRegistrationParticipants(Long eventId) {
    }

    public void deleteRegistration(Long eventId) {
    }
}
