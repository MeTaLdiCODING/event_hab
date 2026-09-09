package com.event.hab.events.service;
import org.springframework.security.core.context.SecurityContextHolder;
import com.event.hab.common.castomException.OrganizerMismatchException;
import com.event.hab.common.castomException.EventNotFoundException;
import com.event.hab.common.castomException.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import com.event.hab.events.repository.EventRepository;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.events.DTO.UpdateEventRequest;
import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.DTO.EventDetailsDTO;
import org.springframework.stereotype.Service;
import com.event.hab.events.model.Event;
import com.event.hab.events.EventMapper;
import java.util.Optional;
import java.util.List;
@Service
public class EventService {
   final EventRepository eventRepository;
   final EventMapper eventMapper;
   final UserRepository userRepository;

    public EventService(EventRepository eventRepository, EventMapper eventMapper, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userRepository = userRepository;
    }

//
    public List<EventSummaryDTO> getAll() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toEventSummaryDto)
                .toList();
    }
//
    public EventDetailsDTO getEventById(Long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isEmpty()){
            throw new EventNotFoundException();
        }
        return eventMapper.toEventDetailsDto(eventOptional.get());
    }
//
    public EventDetailsDTO createEvent(CreateEventRequest request) {
        Event event = eventMapper.toEvent(request);


        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email =userDetails.getUsername();
        event.setOrganizer(userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new));

        eventRepository.save(event);
        return eventMapper.toEventDetailsDto(event);

    }
//
    public String deleteEvent(Long id) {
       Event existingEvent =  eventRepository.findById(id).orElseThrow(EventNotFoundException::new);
        String emailOrganizerEvent = existingEvent.getOrganizer().getEmail();
       UserDetails currentUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       String userEmail = currentUser.getUsername();
       if (!emailOrganizerEvent.equals(userEmail)){
           throw new OrganizerMismatchException();
       }else {
        eventRepository.deleteById(id);
        return "Событие с этим id удаленно: "+ id;}
    }
//
    public EventDetailsDTO updateEvent(Long id, UpdateEventRequest request) {
        Optional<Event> event = eventRepository.findById(id);
        Event existingEvent =  event.orElseThrow(EventNotFoundException::new);
        String emailOrganizerEvent = existingEvent.getOrganizer().getEmail();
        UserDetails currentUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = currentUser.getUsername();
        if (!emailOrganizerEvent.equals(userEmail)){
            throw new OrganizerMismatchException();
        }else {
           eventMapper.updateEventFromDto(request,existingEvent);
           eventRepository.save(existingEvent);
           return eventMapper.toEventDetailsDto(existingEvent);


        }
    }
}
