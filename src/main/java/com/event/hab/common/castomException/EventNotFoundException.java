package com.event.hab.common.castomException;

import com.event.hab.events.model.Event;

public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(){
        super("Данное событие не найденно.");
    }
}
