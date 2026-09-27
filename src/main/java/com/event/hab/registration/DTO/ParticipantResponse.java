package com.event.hab.registration.DTO;
import com.event.hab.registration.model.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {

    //Данные о юзере
    private Long userId;
    private String fullName;
    private String email;


    private boolean paid;
    private String ticketNumber;
    private LocalDateTime registeredAt;
    private RegistrationStatus registrationStatus;
}
