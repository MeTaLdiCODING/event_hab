package com.event.hab.profile.model;

import com.event.hab.auth.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Column(length = 500)
    private String bio;
    private String avatarUrl;
    @Column(nullable = false)
    private boolean allowMessages;
    @Column(nullable = false)
    private double rating= 0;
    @Column(nullable = false)
    private int reviewsCount=0;
    @Column(nullable = false)
    private int completedEventsCount=0;
}
