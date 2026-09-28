package com.event.hab.common.castomException;

public class AlreadyRegisteredException extends RuntimeException{
    public AlreadyRegisteredException(){
        super("Вы уже записаны на это событие.");
    }
}
