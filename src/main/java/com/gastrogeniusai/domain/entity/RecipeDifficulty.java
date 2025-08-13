package com.gastrogeniusai.domain.entity;

/**
 * Enumeration representing different difficulty levels for recipes in the
 * GastroGenius AI system.
 * Used to help users choose recipes based on their cooking skill level.
 */
public enum RecipeDifficulty {
    BEGINNER("Beginner", "Perfect for cooking newcomers", 1),
    EASY("Easy", "Simple techniques and common ingredients", 2),
    MEDIUM("Medium", "Some cooking experience recommended", 3),
    HARD("Hard", "Advanced techniques and skills required", 4),
    EXPERT("Expert", "Professional-level complexity", 5);

    private final String displayName;
    private final String description;
    private final int level;

    RecipeDifficulty(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    /**
     * Returns the user-friendly display name for the difficulty.
     * 
     * @return formatted difficulty name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns a description of what this difficulty level entails.
     * 
     * @return difficulty description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the numeric level of difficulty (1-5).
     * 
     * @return difficulty level as integer
     */
    public int getLevel() {
        return level;
    }

    /**
     * Checks if this difficulty is suitable for beginners.
     * 
     * @return true if difficulty is BEGINNER or EASY
     */
    public boolean isBeginnerFriendly() {
        return this == BEGINNER || this == EASY;
    }

    /**
     * Checks if this difficulty requires advanced skills.
     * 
     * @return true if difficulty is HARD or EXPERT
     */
    public boolean isAdvanced() {
        return this == HARD || this == EXPERT;
    }

    /**
     * Gets the difficulty level that comes before this one.
     * 
     * @return the previous difficulty level or null if this is BEGINNER
     */
    public RecipeDifficulty getPrevious() {
        return switch (this) {
            case EASY -> BEGINNER;
            case MEDIUM -> EASY;
            case HARD -> MEDIUM;
            case EXPERT -> HARD;
            default -> null;
        };
    }

    /**
     * Gets the difficulty level that comes after this one.
     * 
     * @return the next difficulty level or null if this is EXPERT
     */
    public RecipeDifficulty getNext() {
        return switch (this) {
            case BEGINNER -> EASY;
            case EASY -> MEDIUM;
            case MEDIUM -> HARD;
            case HARD -> EXPERT;
            default -> null;
        };
    }

    /**
     * Gets a difficulty by its display name (case insensitive).
     * 
     * @param displayName the display name to search for
     * @return the matching difficulty or null if not found
     */
    public static RecipeDifficulty fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }

        for (RecipeDifficulty difficulty : values()) {
            if (difficulty.displayName.equalsIgnoreCase(displayName.trim())) {
                return difficulty;
            }
        }
        return null;
    }

    /**
     * Gets a difficulty by its numeric level.
     * 
     * @param level the level to search for (1-5)
     * @return the matching difficulty or null if level is invalid
     */
    public static RecipeDifficulty fromLevel(int level) {
        for (RecipeDifficulty difficulty : values()) {
            if (difficulty.level == level) {
                return difficulty;
            }
        }
        return null;
    }
}
