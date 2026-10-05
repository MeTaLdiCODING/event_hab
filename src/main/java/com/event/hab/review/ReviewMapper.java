package com.event.hab.review;

import com.event.hab.review.DTO.ReviewResponse;
import com.event.hab.review.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorFullName", source = "author.fullName")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventName", source = "event.name")
    ReviewResponse toResponse(Review review);
}
