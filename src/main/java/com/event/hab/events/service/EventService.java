package com.event.hab.events.service;

import com.event.hab.auth.model.User;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventDetailsDTO;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.EventMapper;
import com.event.hab.events.model.Event;
import com.event.hab.events.repository.EventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;
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
    //дальше сервис и контроллер
}
