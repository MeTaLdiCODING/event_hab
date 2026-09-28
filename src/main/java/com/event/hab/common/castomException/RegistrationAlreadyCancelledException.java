package com.event.hab.common.castomException;

public class RegistrationAlreadyCancelledException extends RuntimeException{
    public RegistrationAlreadyCancelledException(){
        super("Регистрация уже отменена");
    }
}
