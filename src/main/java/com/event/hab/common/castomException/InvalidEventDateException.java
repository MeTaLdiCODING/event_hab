package com.event.hab.common.castomException;

public class InvalidEventDateException extends RuntimeException{
    public InvalidEventDateException(){
        super("Не верная дата события. Событие не может быть в прошлом.");
    }
}
