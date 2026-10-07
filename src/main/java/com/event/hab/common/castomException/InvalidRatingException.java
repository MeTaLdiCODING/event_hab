package com.event.hab.common.castomException;

public class InvalidRatingException extends RuntimeException{
    public InvalidRatingException(){
        super("Рейтинг должен даходится в диапазоне от 1 до 5.");
    }
}
