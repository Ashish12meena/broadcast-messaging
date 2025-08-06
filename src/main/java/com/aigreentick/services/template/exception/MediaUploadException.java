package com.aigreentick.services.template.exception;

import java.io.IOException;

public class MediaUploadException extends RuntimeException {
    public MediaUploadException(String message, IOException ex){
        super(message);
    }
}
