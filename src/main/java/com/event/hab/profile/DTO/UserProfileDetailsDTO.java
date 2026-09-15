package com.event.hab.profile.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDetailsDTO {
    private String fullName;
    private String bio;
    private String avatarUrl;
    private double rating;
    private int reviewsCount;
    private int completedEventsCount;
}
