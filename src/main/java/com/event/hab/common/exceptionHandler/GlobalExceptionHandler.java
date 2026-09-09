package com.event.hab.common.exceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler

    public ResponseEntity<String> handleException(InvalidCrede e){

    }
    public ResponseEntity<String> handleException(UsernameNotFoundException e){

    }
    public ResponseEntity<String> handleException(UsernameNotFoundException e){

    }
    public ResponseEntity<String> handleException(UsernameNotFoundException e){

    }
    public ResponseEntity<String> handleException(UsernameNotFoundException e){

    }
}
