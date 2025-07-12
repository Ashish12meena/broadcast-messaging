package com.aigreentick.services.auth.exception;

public class PermissionAlreadyExistsException extends RuntimeException {

    public PermissionAlreadyExistsException(String permissionName) {
        super("Permission '" + permissionName + "' already exists");
    }
}
