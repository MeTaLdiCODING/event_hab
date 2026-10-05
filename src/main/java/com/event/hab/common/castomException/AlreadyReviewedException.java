package com.event.hab.common.castomException;

public class AlreadyReviewedException extends RuntimeException{
    public AlreadyReviewedException(){
        super("Вы уже оставили отзыв на это событие");
    }
}
