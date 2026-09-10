package com.event.hab.common.exceptionHandler;

import com.event.hab.common.DTO.ErrorResponse;
import com.event.hab.common.castomException.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationRequired(AuthenticationRequiredException e){
        log.warn("AuthenticationRequired: {}", e.getMessage());
        var errorDto = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorDto);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> eventNotFound(EventNotFoundException e){
        log.warn("EventNotFound: {}", e.getMessage());
        var errorDto = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> invalidCredentials(InvalidCredentialsException e){
        log.warn("InvalidCredentials: {}", e.getMessage());
        var errorDto = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorDto);
    }

    @ExceptionHandler(InvalidEventDateException.class)
    public ResponseEntity<ErrorResponse> invalidEventDate(InvalidEventDateException e){
        log.warn("InvalidEventDate: {}", e.getMessage());
        var errorDto = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }

    @ExceptionHandler(OrganizerMismatchException.class)
    public ResponseEntity<ErrorResponse> organizerMismatch(OrganizerMismatchException e){
        log.warn("OrganizerMismatch: {}", e.getMessage());
        var errorDto = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorDto);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFound(UserNotFoundException e){
        log.warn("UserNotFound: {}", e.getMessage());
        var errorDto = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExists(UserAlreadyExistsException e){
        log.warn("UserAlreadyExists: {}", e.getMessage());
        var errorDto = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalException(Exception e){
        log.error("GlobalException: {}", e.getMessage());
        var errorDto = new ErrorResponse(
                "Произошла ошибка на сервере. Пожалуйста попробуйте еще раз.", LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }
}
