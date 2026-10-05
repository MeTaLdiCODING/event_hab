package com.event.hab.common.castomException;

public class CannotReviewSelfException extends RuntimeException{
    public CannotReviewSelfException(){
        super("Вы не можете оставить отзыв своему событию.");
    }
}
