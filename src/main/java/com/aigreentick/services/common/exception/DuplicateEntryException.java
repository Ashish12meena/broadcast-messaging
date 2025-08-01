package com.aigreentick.services.common.exception;

public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException(String message){
        super(message);
    }
}
