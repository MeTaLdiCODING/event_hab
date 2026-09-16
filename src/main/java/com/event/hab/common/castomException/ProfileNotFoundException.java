package com.event.hab.common.castomException;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException(){
        super("Профиль не найден.");
    }
}
