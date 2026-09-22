package com.event.hab.registration.model;

import com.event.hab.auth.model.User;
import com.event.hab.events.model.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "registrations")
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    @JoinColumn(name = "event_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;

    @Column(nullable = false)
    private String ticketNumber;

    @Column(nullable = false)
    private boolean paid = false;

    @CreationTimestamp
    private LocalDateTime registeredAt;

}
