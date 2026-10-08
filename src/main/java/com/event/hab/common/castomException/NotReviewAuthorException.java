package com.event.hab.common.castomException;

public class NotReviewAuthorException extends RuntimeException{
    public NotReviewAuthorException(){
        super("Вы не являетесь автором этого отзыва.");
    }
}
