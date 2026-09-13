package com.event.hab.events.DTO;

import com.event.hab.events.model.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSummaryDTO {
    private Long id;
    private String name;
    private Type type;
    private LocalDateTime eventDate;
    private String location;
    private String coverImageUrl ;
    private BigDecimal price;

}
