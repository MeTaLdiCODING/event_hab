package com.event.hab.events.controller;
import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventDetailsDTO;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.DTO.UpdateEventRequest;
import com.event.hab.events.model.Type;
import com.event.hab.events.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
public class EventController {
    final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public Page<EventSummaryDTO> getAllEvents(
            Pageable pageable,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String search

    ){
      return eventService.getAll(pageable,type,minPrice,maxPrice,dateFrom,dateTo,search);
    }

    @GetMapping("/eventById/{id}")
    public EventDetailsDTO getEventById(@PathVariable Long id){
        return eventService.getEventById(id);
    }

    @PostMapping("/createEvent")
    public EventDetailsDTO createEvent(@RequestBody CreateEventRequest request){
        return eventService.createEvent(request);

    }

    @PutMapping("/updateEvent/{id}")
    public EventDetailsDTO updateEvent(@PathVariable Long id, @RequestBody UpdateEventRequest request){
        return eventService.updateEvent(id,request);
    }

    @DeleteMapping("/deleteEvent/{id}")
    public String deleteEvent(@PathVariable Long id){
        return eventService.deleteEvent(id);
    }


}