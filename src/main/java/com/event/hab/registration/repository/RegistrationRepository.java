package com.event.hab.registration.repository;

import com.event.hab.registration.model.Registration;
import com.event.hab.registration.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration,Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

    List<Registration> findAllByUserId(Long userId);

    List<Registration> findAllByEventId(Long eventId);

    List<Registration> findAllByUserIdAndRegistrationStatus(Long userId, RegistrationStatus status);

    List<Registration> findAllByEventIdAndRegistrationStatus(Long eventId, RegistrationStatus status);

    long countByEventIdAndRegistrationStatus(Long eventId, RegistrationStatus status);
}
