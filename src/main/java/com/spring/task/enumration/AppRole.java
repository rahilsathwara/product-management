package com.spring.task.enumration;

public enum AppRole {
    ADMIN_ROLE("ROLE_ADMIN"),
    USER_ROLE("ROLE_USER"),
    MANAGER_ROLE("ROLE_MANAGER");

    private final String roleName;

    AppRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
