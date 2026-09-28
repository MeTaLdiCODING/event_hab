package com.event.hab.common.castomException;

public class EventFullException extends RuntimeException{
    public EventFullException(){
        super("На событии нет свободных мест");
    }
}
