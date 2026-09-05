package com.event.hab.events.DTO;

import com.event.hab.events.model.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSummaryDTO {
    private Long id;
    private String name;
    private Type type;
    private LocalDateTime eventDate;
}
