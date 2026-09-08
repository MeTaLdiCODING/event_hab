package com.event.hab.events.controller;
import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventDetailsDTO;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.EventMapper;
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
    public List<EventSummaryDTO> getAllEvents(){//DTO
      return eventService.getAll();
    }

    @GetMapping("/eventById/{id}")
    public EventDetailsDTO getEventById(@PathVariable Long id){
        return eventService.getEventById(id);
    }

    @PostMapping("/createEvent")
    public EventDetailsDTO createEvent(@RequestBody CreateEventRequest request){
        return eventService.createEvent(request);

    }

    @PutMapping("/updateEvent")
    public void updateEvent(){
    }

    @DeleteMapping("/deleteEvent")
    public void deleteEvent(){
    }


}