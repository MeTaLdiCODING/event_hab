package com.event.hab.events.DTO;

import com.event.hab.events.model.Status;
import com.event.hab.events.model.Type;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsDTO {

    private Long id;
    private String name;
    private Type type;
    private LocalDateTime eventDate;
    private String location;
    private BigDecimal price;
    private int maxParticipants;
    private int currentParticipants;
    private Status status;
    private List<String> imageUrls ;
    private String description;
    private Long organizerId;
    private String organizerEmail;
}
