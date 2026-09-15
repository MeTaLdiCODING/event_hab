package com.event.hab.profile.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileSummaryDTO {
    private String fullName;
    private String avatarUrl;
    private double rating;
    private int completedEventsCount;
}
