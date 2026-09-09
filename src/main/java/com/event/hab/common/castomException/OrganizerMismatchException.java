package com.event.hab.common.castomException;

public class OrganizerMismatchException extends RuntimeException{
    public OrganizerMismatchException(){
        super("Недостаточно прав. Вы не являетесь организатором этого события.");
    }
}
