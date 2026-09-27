package com.event.hab.registration;
import com.event.hab.registration.DTO.ParticipantResponse;
import com.event.hab.registration.DTO.RegistrationResponse;
import com.event.hab.registration.model.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface RegistrationMapper {


    @Mapping(target ="eventId", source = "event.id")
    @Mapping(target ="eventName", source = "event.name")
    @Mapping(target ="eventDate", source = "event.eventDate")
    @Mapping(target ="location", source = "event.location")
    RegistrationResponse toResponse(Registration registration);

    @Mapping(target ="userId", source = "user.id")
    @Mapping(target ="fullName", source = "user.fullName")
    @Mapping(target ="email", source = "user.email")
    ParticipantResponse toParticipant(Registration registration);
}
