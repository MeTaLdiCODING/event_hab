package com.event.hab.review.repository;
import com.event.hab.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByTargetIdOrderByCreatedAtDesc(Long targetId);
    boolean existsByAuthorIdAndEventId(Long authorId, Long eventId);
    List<Review> findByEventId(Long eventId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.target.id = :targetId")
    Double getAverageRatingByTargetId(@Param("targetId") Long targetId);

    long countByTargetId(Long targetId);
}
