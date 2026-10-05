package com.event.hab.common.castomException;

public class NotParticipatedException extends RuntimeException{
    public NotParticipatedException(){
        super("Отзыв могут оставить только пользователи которые были на этом событии.");
    }
}
