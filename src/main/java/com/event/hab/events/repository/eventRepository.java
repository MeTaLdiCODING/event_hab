package com.event.hab.events.repository;

import com.event.hab.auth.servise.JwtService;
import com.event.hab.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface eventRepository extends JpaRepository<Event,Long> {

}
