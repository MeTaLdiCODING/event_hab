package com.event.hab.review.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @Min(1)
    @Max(5)

    private int rating;
    @NotNull
    @Size(max=1000)
    private String text;
    @Size(max=5)
    private List<@Size(max=2000) String> imageUrls ;
}
