package com.gastrogeniusai.domain.entity;

/**
 * Enumeration representing user roles in the GastroGenius AI system.
 * Defines the different levels of access and permissions.
 */
public enum UserRole {
    /**
     * Regular user role - can manage their own recipes and use AI features
     */
    USER,

    /**
     * Premium user role - extended features and capabilities
     */
    PREMIUM,

    /**
     * Administrator role - full system access and user management
     */
    ADMIN,

    /**
     * Moderator role - content moderation capabilities
     */
    MODERATOR;

    /**
     * Returns a user-friendly display name for the role.
     * 
     * @return formatted role name
     */
    public String getDisplayName() {
        return switch (this) {
            case USER -> "User";
            case PREMIUM -> "Premium User";
            case ADMIN -> "Administrator";
            case MODERATOR -> "Moderator";
        };
    }

    /**
     * Checks if the role has administrative privileges.
     * 
     * @return true if the role is ADMIN or MODERATOR
     */
    public boolean hasAdminPrivileges() {
        return this == ADMIN || this == MODERATOR;
    }

    /**
     * Checks if the role has premium features access.
     * 
     * @return true if the role is PREMIUM, ADMIN, or MODERATOR
     */
    public boolean hasPremiumAccess() {
        return this == PREMIUM || this == ADMIN || this == MODERATOR;
    }
}
