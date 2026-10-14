package com.event.hab.events.DTO;

import com.event.hab.events.model.Type;
import jakarta.validation.constraints.*;
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
    @Size(max = 100, min = 3)
    @NotBlank
    private String name;
    @NotNull
    private Type type;
    @Future
    @NotNull
    private LocalDateTime eventDate;
    @Size(max = 300, min = 3)
    @NotBlank
    private String location;
    @DecimalMin(value="0.0", inclusive=true)
    @NotNull
    private BigDecimal price;
    @Positive
    private int maxParticipants;
    @Size(max=10)
    private List<@Size(max=2000) String> imageUrls ;
    @Size(max = 3000)
    @NotBlank
    private String description;
}
