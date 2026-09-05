package com.event.hab.events.service;

import com.event.hab.events.EventMapper;
import com.event.hab.events.model.Event;
import com.event.hab.events.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
   final EventRepository eventRepository;
   final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }


    public List<Event> getAll() {
        return eventRepository.findAll();
    }
    //дальше сервис и контроллер
}
