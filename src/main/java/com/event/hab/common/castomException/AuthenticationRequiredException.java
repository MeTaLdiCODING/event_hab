package com.event.hab.common.castomException;

public class AuthenticationRequiredException extends RuntimeException{
    public AuthenticationRequiredException(){
        super("Ошибка при аутентификации.");
    }
}
