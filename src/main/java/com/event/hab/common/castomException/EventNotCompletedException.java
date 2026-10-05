package com.event.hab.common.castomException;

public class EventNotCompletedException extends RuntimeException {
    public EventNotCompletedException(){
        super("Событие еще не завершено.");
    }
}
