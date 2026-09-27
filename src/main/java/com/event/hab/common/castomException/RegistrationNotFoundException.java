package com.event.hab.common.castomException;

public class RegistrationNotFoundException extends RuntimeException{
    public RegistrationNotFoundException(){
        super("Регистрация не найденна.");
    }
}
