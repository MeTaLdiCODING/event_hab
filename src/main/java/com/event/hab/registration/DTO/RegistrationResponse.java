package com.event.hab.registration.DTO;
import com.event.hab.registration.model.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    //Данные о мероприятии
    private Long eventId;
    private String eventName;
    private LocalDateTime eventDate;
    private String location;

    private boolean paid;
    private String ticketNumber;
    private LocalDateTime registeredAt;
    private RegistrationStatus registrationStatus;
}
