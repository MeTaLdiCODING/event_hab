package com.event.hab.profile;
import com.event.hab.profile.DTO.UpdateProfileRequest;
import com.event.hab.profile.DTO.UserProfileDetailsDTO;
import com.event.hab.profile.DTO.UserProfileSummaryDTO;
import com.event.hab.profile.model.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "fullName", source = "user.fullName")
    UserProfileDetailsDTO toDetailsDto(UserProfile profile);

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    UserProfileSummaryDTO toSummaryDto(UserProfile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "reviewsCount", ignore = true)
    @Mapping(target = "completedEventsCount", ignore = true)
    void updateProfileFromDto(UpdateProfileRequest request, @MappingTarget UserProfile profile);
}