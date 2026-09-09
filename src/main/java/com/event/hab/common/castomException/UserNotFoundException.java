package com.event.hab.common.castomException;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("Не верные логин или пароль.");
    }
}
