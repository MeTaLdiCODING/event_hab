package com.event.hab.common.castomException;

public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException(){
        super("Отзыв не найден.");
    }
}
