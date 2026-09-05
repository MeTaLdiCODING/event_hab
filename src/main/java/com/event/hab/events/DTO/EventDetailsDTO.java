package com.event.hab.events.DTO;

import com.event.hab.events.model.Type;
import lombok.*;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsDTO {

    private Long id;
    private String name;
    private Type type;
    private LocalDateTime eventDate;
    private String description;
    private String organizerEmail;
}
