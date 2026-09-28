package com.event.hab.common.castomException;

public class EventNotAvailableException extends RuntimeException{
    public EventNotAvailableException(){
        super("Событие недоступно для записи.");
    }
}
