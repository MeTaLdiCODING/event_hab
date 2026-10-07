package com.event.hab.review.service;
import com.event.hab.auth.model.User;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.common.castomException.*;
import com.event.hab.common.securityUtils.SecurityUtils;
import com.event.hab.events.model.Event;
import com.event.hab.events.repository.EventRepository;
import com.event.hab.profile.service.UserProfileService;
import com.event.hab.registration.model.RegistrationStatus;
import com.event.hab.registration.repository.RegistrationRepository;
import com.event.hab.review.DTO.ReviewRequest;
import com.event.hab.review.DTO.ReviewResponse;
import com.event.hab.review.ReviewMapper;
import com.event.hab.review.model.Review;
import com.event.hab.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
final UserRepository userRepository;
final EventRepository eventRepository;
final RegistrationRepository registrationRepository;
final ReviewRepository reviewRepository;
final ReviewMapper reviewMapper;
final UserProfileService userProfileService;
    public ReviewService(UserRepository userRepository,
                         EventRepository eventRepository,
                         RegistrationRepository registrationRepository,
                         ReviewRepository reviewRepository,
                         ReviewMapper reviewMapper, UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userProfileService = userProfileService;
    }

    @Transactional
   public ReviewResponse createReview(Long eventId, ReviewRequest request){
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        Event event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
        User organizer = event.getOrganizer();
        Long userId = user.getId();
        //if(event.getStatus()!= Status.COMPLETED){
          //  throw new EventNotCompletedException();}
        if (event.getEventDate().isAfter(LocalDateTime.now())){
            throw new EventNotCompletedException();
        }
        if (!registrationRepository
                .existsByUserIdAndEventIdAndRegistrationStatus
                        (userId, eventId, RegistrationStatus.CONFIRMED)){
           throw new NotParticipatedException();
        }
        if(userId.equals(organizer.getId())){
            throw new CannotReviewSelfException();
        }
        if (reviewRepository.existsByAuthorIdAndEventId(userId,eventId)){
            throw new AlreadyReviewedException();
        }
        if (request.getRating()<1 || request.getRating()>5){
            throw new InvalidRatingException();
        }
        Review review = new Review();
        review.setAuthor(user);
        review.setTarget(organizer);
        review.setEvent(event);
        review.setRating(request.getRating());
        review.setText(request.getText());
        review.setImageUrls(request.getImageUrls());
        reviewRepository.save(review);
        userProfileService.recalculateRating(organizer.getId());
        return reviewMapper.toResponse(review);
    }

    List<ReviewResponse> getReviewsForOrganizer(Long userId){

    }

    List<ReviewResponse> getReviewsForEvent(Long eventId){

    }

    void deleteReview(Long reviewId){

    }
}
