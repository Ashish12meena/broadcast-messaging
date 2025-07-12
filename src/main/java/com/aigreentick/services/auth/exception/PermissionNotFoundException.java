package com.aigreentick.services.auth.exception;

public class PermissionNotFoundException extends RuntimeException {

    public PermissionNotFoundException(String permissionName) {
        super("Permission '" + permissionName + "' not found");
    }

    public PermissionNotFoundException(String code, String permissionName) {
        super("Permission '" + permissionName + "' not found with code: " + code);
    }
    
}
