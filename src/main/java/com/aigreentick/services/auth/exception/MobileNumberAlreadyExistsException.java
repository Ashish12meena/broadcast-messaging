package com.aigreentick.services.auth.exception;

public class MobileNumberAlreadyExistsException extends RuntimeException {
    public MobileNumberAlreadyExistsException(String msg){
        super(msg);
    }
    
}
