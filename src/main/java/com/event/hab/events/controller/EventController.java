package com.event.hab.events.controller;
import com.event.hab.events.model.Event;
import com.event.hab.events.service.EventService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class EventController {
    final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(){//DTO
        return eventService.getAll();
    }

    @GetMapping("/eventById")
    public void getEventById(){
    }

    @PostMapping("/createEvent")
    public void createEvent(){
    }

    @PutMapping("/updateEvent")
    public void updateEvent(){
    }

    @DeleteMapping("/deleteEvent")
    public void deleteEvent(){
    }


}