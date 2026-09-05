package com.event.hab.events.DTO;

import com.event.hab.events.model.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {
    private String name;
    private Type type;
    private LocalDateTime eventDate;
    private String description;
}
