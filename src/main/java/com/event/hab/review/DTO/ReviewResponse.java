package com.event.hab.review.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long authorId;
    private String authorFullName;
    private int rating;
    private String text;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private Long eventId;
    private String eventName;

}
