package com.event.hab.events.repository;

import com.event.hab.events.DTO.EventSummaryDTO;
import com.event.hab.events.model.Event;
import com.event.hab.events.model.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query(
            """
            SELECT e FROM Event e
            where e.status = 'ACTIVE'
            AND (:type IS NULL OR e.type = :type)
            AND (:minPrice IS NULL OR e.price >= :minPrice)
            AND (:maxPrice IS NULL OR e.price <= :maxPrice)
            AND (:dateFrom IS NULL OR e.eventDate >= :dateFrom)
            AND (:dateTo IS NULL OR e.eventDate <= :dateTo)
            AND (:search IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    public Page<Event> getAllFilter(
            @Param("type") Type type,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("search") String search,
            Pageable pageable);
}
