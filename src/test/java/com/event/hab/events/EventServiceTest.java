package com.event.hab.events;

import com.event.hab.auth.model.User;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.common.castomException.EventNotAvailableException;
import com.event.hab.common.castomException.EventNotFoundException;
import com.event.hab.common.castomException.OrganizerMismatchException;
import com.event.hab.common.castomException.UserNotFoundException;
import com.event.hab.common.securityUtils.SecurityUtils;
import com.event.hab.events.DTO.CreateEventRequest;
import com.event.hab.events.DTO.EventDetailsDTO;
import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.DTO.UpdateEventRequest;
import com.event.hab.events.model.Event;
import com.event.hab.events.model.Status;
import com.event.hab.events.repository.EventRepository;
import com.event.hab.events.service.EventService;
import com.event.hab.registration.model.Registration;
import com.event.hab.registration.model.RegistrationStatus;
import com.event.hab.registration.repository.RegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    // ---- тесты для получение (Get) ----

    @Test
    void testGetEventById_Success() {
        Long eventId = 1L;
        Event event = new Event();
        EventDetailsDTO expectedDto = new EventDetailsDTO();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toEventDetailsDto(event)).thenReturn(expectedDto);

        EventDetailsDTO actualDto = eventService.getEventById(eventId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testGetEventById_NotFound_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(1L));
    }

    @Test
    void testGetAll_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Event event = new Event();
        Page<Event> eventPage = new PageImpl<>(List.of(event));
        EventSummaryDTO summaryDto = new EventSummaryDTO();

        when(eventRepository.getAllFilter(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(eventPage);
        when(eventMapper.toEventSummaryDto(event)).thenReturn(summaryDto);

        Page<EventSummaryDTO> result = eventService.getAll(pageable, null, null, null, null, null, null, "test search");

        assertEquals(1, result.getTotalElements());
        assertEquals(summaryDto, result.getContent().get(0));
    }

    @Test
    void testGetAll_NullSearch_SetsEmptyString() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(Collections.emptyList());

        // ИСПРАВЛЕНИЕ ЗДЕСЬ: добавлен 8-й аргумент (any()) для Pageable
        when(eventRepository.getAllFilter(any(), any(), any(), any(), any(), any(), eq(""), any()))
                .thenReturn(eventPage);

        Page<EventSummaryDTO> result = eventService.getAll(pageable, null, null, null, null, null, null, null);

        assertEquals(0, result.getTotalElements());
        // И здесь тоже добавлен 8-й аргумент (eq(pageable))
        verify(eventRepository, times(1)).getAllFilter(any(), any(), any(), any(), any(), any(), eq(""), eq(pageable));
    }

    // ---- тесты для создание (Create) ----

    @Test
    void testCreateEvent_Success() {
        CreateEventRequest request = new CreateEventRequest();
        Event event = new Event();
        User user = new User();
        EventDetailsDTO expectedDto = new EventDetailsDTO();

        when(eventMapper.toEvent(request)).thenReturn(event);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(eventMapper.toEventDetailsDto(event)).thenReturn(expectedDto);

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("test@test.com");

            EventDetailsDTO actualDto = eventService.createEvent(request);

            assertEquals(expectedDto, actualDto);
            assertEquals(user, event.getOrganizer());
            verify(eventRepository, times(1)).save(event);
        }
    }

    @Test
    void testCreateEvent_UserNotFound_ThrowsException() {
        CreateEventRequest request = new CreateEventRequest();
        Event event = new Event();

        when(eventMapper.toEvent(request)).thenReturn(event);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("nonexistent@test.com");

            assertThrows(UserNotFoundException.class, () -> eventService.createEvent(request));
            verify(eventRepository, never()).save(any(Event.class));
        }
    }

    // ---- тесты для обновление (Update) ----

    @Test
    void testUpdateEvent_Success() {
        Long eventId = 1L;
        UpdateEventRequest request = new UpdateEventRequest();
        Event event = new Event();
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("organizer@test.com");

            eventService.updateEvent(eventId, request);

            verify(eventMapper, times(1)).updateEventFromDto(request, event);
            verify(eventRepository, times(1)).save(event);
            verify(eventMapper, times(1)).toEventDetailsDto(event);
        }
    }

    @Test
    void testUpdateEvent_NotOrganizer_ThrowsException() {
        Long eventId = 1L;
        Event event = new Event();
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("hacker@test.com");

            assertThrows(OrganizerMismatchException.class, () -> eventService.updateEvent(eventId, new UpdateEventRequest()));
            verify(eventRepository, never()).save(any(Event.class));
        }
    }

    @Test
    void testUpdateEvent_NotFound_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(1L, new UpdateEventRequest()));
    }

    // ---- тесты для удаление (Delete) ----

    @Test
    void testDeleteEvent_Success() {
        Long eventId = 1L;
        Event event = new Event();
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("organizer@test.com");

            String result = eventService.deleteEvent(eventId);

            assertEquals("Событие с этим id удаленно: 1", result);
            verify(eventRepository, times(1)).deleteById(eventId);
        }
    }

    @Test
    void testDeleteEvent_NotOrganizer_ThrowsException() {
        Long eventId = 1L;
        Event event = new Event();
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("hacker@test.com");

            assertThrows(OrganizerMismatchException.class, () -> eventService.deleteEvent(eventId));
            verify(eventRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    void testDeleteEvent_NotFound_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L));
    }

    // ---- тесты для отмена (Cancel) ----

    @Test
    void testCancelEvent_Success() {
        Long eventId = 1L;
        Event event = new Event();
        event.setStatus(Status.ACTIVE);
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        Registration reg = new Registration();
        reg.setRegistrationStatus(RegistrationStatus.CONFIRMED);
        List<Registration> registrations = List.of(reg);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.findAllByEventIdAndRegistrationStatus(eventId, RegistrationStatus.CONFIRMED))
                .thenReturn(registrations);

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("organizer@test.com");

            eventService.cancelEvent(eventId);

            assertEquals(Status.CANCELLED, event.getStatus());
            assertEquals(RegistrationStatus.CANCELLED, reg.getRegistrationStatus());
            verify(registrationRepository, times(1)).saveAll(registrations);
            verify(eventRepository, times(1)).save(event);
        }
    }

    @Test
    void testCancelEvent_NoRegistrations_Success() {
        Long eventId = 1L;
        Event event = new Event();
        event.setStatus(Status.ACTIVE);
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        List<Registration> emptyRegistrations = Collections.emptyList();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(registrationRepository.findAllByEventIdAndRegistrationStatus(eventId, RegistrationStatus.CONFIRMED))
                .thenReturn(emptyRegistrations);

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("organizer@test.com");

            eventService.cancelEvent(eventId);

            assertEquals(Status.CANCELLED, event.getStatus());
            verify(registrationRepository, times(1)).saveAll(emptyRegistrations);
            verify(eventRepository, times(1)).save(event);
        }
    }

    @Test
    void testCancelEvent_AlreadyCancelled_ThrowsException() {
        Event event = new Event();
        event.setStatus(Status.CANCELLED);
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("organizer@test.com");

            assertThrows(EventNotAvailableException.class, () -> eventService.cancelEvent(1L));
            verify(eventRepository, never()).save(any(Event.class));
        }
    }

    @Test
    void testCancelEvent_Completed_ThrowsException() {
        Event event = new Event();
        event.setStatus(Status.COMPLETED);
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("organizer@test.com");

            assertThrows(EventNotAvailableException.class, () -> eventService.cancelEvent(1L));
        }
    }

    @Test
    void testCancelEvent_NotOrganizer_ThrowsException() {
        Event event = new Event();
        event.setStatus(Status.ACTIVE);
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        event.setOrganizer(organizer);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserEmail).thenReturn("hacker@test.com");

            assertThrows(OrganizerMismatchException.class, () -> eventService.cancelEvent(1L));
        }
    }

    @Test
    void testCancelEvent_NotFound_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.cancelEvent(1L));
    }
}