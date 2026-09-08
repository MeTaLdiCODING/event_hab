package com.event.hab.events.service;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventDetailsDTO;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.DTO.UpdateEventRequest;
import com.event.hab.events.EventMapper;
import com.event.hab.events.model.Event;
import com.event.hab.events.repository.EventRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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
            throw new IllegalArgumentException("Событие с таким id не найдено: " + id);
        }
        return eventMapper.toEventDetailsDto(eventOptional.get());
    }
//
    public EventDetailsDTO createEvent(CreateEventRequest request) {
        Event event = eventMapper.toEvent(request);


        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assert userDetails != null;
        String email =userDetails.getUsername();
        event.setOrganizer(userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));

        eventRepository.save(event);
        return eventMapper.toEventDetailsDto(event);

    }
//
    public String deleteEvent(Long id) {
       String emailOrganizerEvent =  eventRepository.findById(id).orElseThrow().getOrganizer().getEmail();
       UserDetails currentUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       String userEmail = currentUser.getUsername();
       if (!emailOrganizerEvent.equals(userEmail)){
           throw new IllegalArgumentException("Вы не являетесь создателем этого события");
       }else {
        eventRepository.deleteById(id);
        return "Событие с этим id удаленно: "+ id;}
    }
//
    public EventDetailsDTO updateEvent(Long id, UpdateEventRequest request) {
        Optional<Event> event = eventRepository.findById(id);
        String emailOrganizerEvent =  event.orElseThrow().getOrganizer().getEmail();
        UserDetails currentUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = currentUser.getUsername();
        if (!emailOrganizerEvent.equals(userEmail)){
            throw new IllegalArgumentException("Вы не являетесь создателем этого события");
        }else {
           Event eventToUpdate  = event.get();
           eventMapper.updateEventFromDto(request,eventToUpdate);
           eventRepository.save(eventToUpdate);
           return eventMapper.toEventDetailsDto(eventToUpdate);


        }
    }
}
