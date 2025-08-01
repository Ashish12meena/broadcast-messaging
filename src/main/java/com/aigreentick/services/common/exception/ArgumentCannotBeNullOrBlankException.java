package com.aigreentick.services.common.exception;

public class ArgumentCannotBeNullOrBlankException extends RuntimeException {
    public ArgumentCannotBeNullOrBlankException(String message){
        super(message);
    }
}
