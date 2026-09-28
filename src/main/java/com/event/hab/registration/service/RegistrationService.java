package com.event.hab.registration.service;

import com.event.hab.auth.model.User;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.common.castomException.*;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public List<RegistrationResponse> getAllRegistrations() {
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

    @Transactional
    public RegistrationResponse createRegistration(Long eventId) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        long count = registrationRepository.countByEventIdAndRegistrationStatus(eventId, RegistrationStatus.CONFIRMED);
        if (count >= event.getMaxParticipants()) {
            throw new EventFullException();
        }
        if(event.getStatus()!= Status.ACTIVE){
            throw new EventNotAvailableException();
        }
        if (event.getEventDate().isBefore(LocalDateTime.now())){
            throw new EventNotAvailableException();
        }
        if (registrationRepository
                .existsByUserIdAndEventIdAndRegistrationStatus(user.getId(),eventId, RegistrationStatus.CONFIRMED)){
            throw new AlreadyRegisteredException();
        }
        Optional<Registration> existing = registrationRepository
                .findByUserIdAndEventId(user.getId(),eventId);
        if (existing.isPresent() &&
                existing.get()
                .getRegistrationStatus() == RegistrationStatus.CANCELLED){
           Registration registrationUpdate=(existing.get());
           registrationUpdate.setRegistrationStatus(RegistrationStatus.CONFIRMED);
           registrationRepository.save(registrationUpdate);
           return registrationMapper.toResponse(registrationUpdate);

        }
            Registration registration = new Registration();
            registration.setRegistrationStatus(RegistrationStatus.CONFIRMED);registration.setUser(user);
            registration.setEvent(event);
            registration.setTicketNumber(UUID.randomUUID().toString());
            registration.setPaid(false);
            registrationRepository.save(registration);
            return registrationMapper.toResponse(registration);
    }

    public List<ParticipantResponse> getRegistrationParticipants(Long eventId) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
       if (!event.getOrganizer().getId().equals(user.getId())){
           throw new OrganizerMismatchException();
       }
       List<Registration> registrations = registrationRepository
               .findAllByEventIdAndRegistrationStatus(eventId, RegistrationStatus.CONFIRMED);
       return registrations.stream().map(registrationMapper::toParticipant).toList();

    }

    @Transactional
    public void deleteRegistration(Long eventId) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Registration registration = registrationRepository.findByUserIdAndEventId(user.getId(), eventId)
                .orElseThrow(RegistrationNotFoundException::new);
        if(registration.getRegistrationStatus() == RegistrationStatus.CANCELLED){
            throw new RegistrationAlreadyCancelledException();
        }
        registration.setRegistrationStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }
}
