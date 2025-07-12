package com.aigreentick.services.auth.constants;

public class AuthConstants {

    private AuthConstants() {
        // Prevent instantiation
    }

    // User actions
    public static final String USER_LOGIN_SUCCESS = "User logged in successfully";
    public static final String USER_CREATED_SUCCESS = "User created successfully";

    // Role management
    public static final String ROLE_ADDED_TO_DB = "Role added to database successfully";
    public static final String ROLE_REMOVED_FROM_DB = "Role removed from database successfully";
    public static final String ROLES_FETCHED_SUCCESS = "All roles fetched successfully";

    // Role-user assignment
    public static final String ROLE_ASSIGNED_TO_USER = "Role assigned to user successfully";
    public static final String ROLE_REMOVED_FROM_USER = "Role removed from user successfully";
    public static final String USER_ROLES_FETCHED = "User roles fetched successfully";

    
    // -------- Permission Management --------
    public static final String PERMISSION_ADDED_TO_DB = "Permission added successfully";
    public static final String PERMISSION_REMOVED_FROM_DB = "Permission removed successfully";
    public static final String PERMISSIONS_FETCHED_SUCCESS = "All permissions fetched successfully";

    // -------- User-Permission Assignment --------
    public static final String PERMISSION_ASSIGNED_TO_USER = "Permission assigned to user successfully";
    public static final String PERMISSION_REVOKED_FROM_USER = "Permission revoked from user successfully";
    public static final String USER_PERMISSIONS_FETCHED = "User permissions fetched successfully";
}
