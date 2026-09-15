package com.event.hab.profile;

import com.event.hab.profile.DTO.UserProfileDetailsDTO;
import com.event.hab.profile.DTO.UserProfileSummaryDTO;
import com.event.hab.profile.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "fullName", source = "user.fullName")
    UserProfileDetailsDTO toDetailsDto(UserProfile profile);

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    UserProfileSummaryDTO toSummaryDto(UserProfile profile);
}
