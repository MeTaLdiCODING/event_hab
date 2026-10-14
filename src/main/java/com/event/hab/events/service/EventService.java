package com.event.hab.events.service;
import com.event.hab.common.castomException.EventNotAvailableException;
import com.event.hab.common.castomException.OrganizerMismatchException;
import com.event.hab.common.castomException.EventNotFoundException;
import com.event.hab.common.castomException.UserNotFoundException;
import com.event.hab.common.securityUtils.SecurityUtils;
import com.event.hab.events.model.Status;
import com.event.hab.events.model.Type;
import com.event.hab.events.repository.EventRepository;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.events.DTO.UpdateEventRequest;
import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.DTO.EventDetailsDTO;
import com.event.hab.registration.model.Registration;
import com.event.hab.registration.model.RegistrationStatus;
import com.event.hab.registration.repository.RegistrationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.event.hab.events.model.Event;
import com.event.hab.events.EventMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
   final EventRepository eventRepository;
   final EventMapper eventMapper;
   final UserRepository userRepository;
   final RegistrationRepository registrationRepository;
    public EventService(EventRepository eventRepository, EventMapper eventMapper, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    private void assertIsOrganizer(Event event){
        String emailOrganizerEvent = event.getOrganizer().getEmail();
        String userEmail = SecurityUtils.getCurrentUserEmail();
        if (!emailOrganizerEvent.equals(userEmail)){
            throw new OrganizerMismatchException();
        }
    }

//
    public Page<EventSummaryDTO> getAll(
            Pageable pageable,
            Type type,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String organizer,
            String search) {
        if (search == null) search = "";
        Page<Event> eventPage = eventRepository.getAllFilter(type,minPrice,maxPrice,dateFrom,dateTo,organizer,search,pageable);
        Page<EventSummaryDTO> dtoPage = eventPage.map(eventMapper::toEventSummaryDto);
        return dtoPage;
    }
//
    public EventDetailsDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(EventNotFoundException::new);
        return eventMapper.toEventDetailsDto(event);
    }
//
    public EventDetailsDTO createEvent(CreateEventRequest request) {
        Event event = eventMapper.toEvent(request);
        String email = SecurityUtils.getCurrentUserEmail();
        event.setOrganizer(userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new));

        eventRepository.save(event);
        return eventMapper.toEventDetailsDto(event);

    }
//
    public String deleteEvent(Long id) {
        Event existingEvent =  eventRepository.findById(id).orElseThrow(EventNotFoundException::new);
        assertIsOrganizer(existingEvent);
        eventRepository.deleteById(id);
        return "Событие с этим id удаленно: "+ id;
    }
//
    public EventDetailsDTO updateEvent(Long id, UpdateEventRequest request) {
        Event existingEvent =  eventRepository.findById(id).orElseThrow(EventNotFoundException::new);
        assertIsOrganizer(existingEvent);
        eventMapper.updateEventFromDto(request, existingEvent);
        eventRepository.save(existingEvent);
        return eventMapper.toEventDetailsDto(existingEvent);


    }

@Transactional
    public EventDetailsDTO cancelEvent(Long id) {
        Event event =  eventRepository.findById(id).orElseThrow(EventNotFoundException::new);
        assertIsOrganizer(event);
        if(event.getStatus()==Status.CANCELLED || event.getStatus()==Status.COMPLETED){
            throw new EventNotAvailableException();
        }
            List<Registration> registrations =registrationRepository.findAllByEventIdAndRegistrationStatus(id, RegistrationStatus.CONFIRMED);
                            registrations.forEach(registration-> registration.setRegistrationStatus(RegistrationStatus.CANCELLED));
            registrationRepository.saveAll(registrations);
            event.setStatus(Status.CANCELLED);
            eventRepository.save(event);
            return eventMapper.toEventDetailsDto(event);

    }
}
