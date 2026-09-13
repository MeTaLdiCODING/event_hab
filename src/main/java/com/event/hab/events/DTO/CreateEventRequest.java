package com.event.hab.events.DTO;

import com.event.hab.events.model.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequest {
    private String name;
    private Type type;
    private LocalDateTime eventDate;
    private String location;
    private BigDecimal price;
    private int maxParticipants;
    private List<String> imageUrls ;
    private String description;
}
