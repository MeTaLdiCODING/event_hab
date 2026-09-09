package com.event.hab.common.castomException;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(){
        super("Аккаунт с таким email уже существует.");
    }
}
