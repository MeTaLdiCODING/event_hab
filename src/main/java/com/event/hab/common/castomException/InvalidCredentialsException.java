package com.event.hab.common.castomException;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(){
        super("Не верный пароль.");
    }
}
