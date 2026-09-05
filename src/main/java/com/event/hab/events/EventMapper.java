package com.event.hab.events;

import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventDetailsDTO;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.DTO.UpdateEventRequest;
import com.event.hab.events.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDetailsDTO toEventDetailsDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    Event toEvent(CreateEventRequest createEventRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    void updateEventFromDto(UpdateEventRequest request, @MappingTarget Event event);
    EventSummaryDTO toEventSummaryDto(Event event);
}
