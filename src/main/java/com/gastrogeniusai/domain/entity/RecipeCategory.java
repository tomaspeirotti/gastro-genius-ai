package com.gastrogeniusai.domain.entity;

/**
 * Enumeration representing different categories of recipes in the GastroGenius
 * AI system.
 * Used to classify and organize recipes by type of dish.
 */
public enum RecipeCategory {
    APPETIZER("Appetizer"),
    MAIN_COURSE("Main Course"),
    SIDE_DISH("Side Dish"),
    DESSERT("Dessert"),
    SOUP("Soup"),
    SALAD("Salad"),
    BEVERAGE("Beverage"),
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack"),
    SAUCE("Sauce"),
    MARINADE("Marinade"),
    PASTA("Pasta"),
    PIZZA("Pizza"),
    BREAD("Bread"),
    CAKE("Cake"),
    COOKIE("Cookie"),
    SMOOTHIE("Smoothie"),
    COCKTAIL("Cocktail"),
    VEGAN("Vegan"),
    VEGETARIAN("Vegetarian"),
    GLUTEN_FREE("Gluten Free"),
    KETO("Keto"),
    LOW_CARB("Low Carb"),
    HIGH_PROTEIN("High Protein"),
    HEALTHY("Healthy"),
    COMFORT_FOOD("Comfort Food"),
    INTERNATIONAL("International"),
    FUSION("Fusion"),
    OTHER("Other");

    private final String displayName;

    RecipeCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly display name for the category.
     * 
     * @return formatted category name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the category represents a dietary restriction or preference.
     * 
     * @return true if the category is diet-related
     */
    public boolean isDietaryCategory() {
        return this == VEGAN || this == VEGETARIAN || this == GLUTEN_FREE ||
                this == KETO || this == LOW_CARB || this == HIGH_PROTEIN || this == HEALTHY;
    }

    /**
     * Checks if the category represents a meal time.
     * 
     * @return true if the category is meal-time related
     */
    public boolean isMealTimeCategory() {
        return this == BREAKFAST || this == LUNCH || this == DINNER || this == SNACK;
    }

    /**
     * Checks if the category represents a course type.
     * 
     * @return true if the category is course-related
     */
    public boolean isCourseCategory() {
        return this == APPETIZER || this == MAIN_COURSE || this == SIDE_DISH || this == DESSERT;
    }

    /**
     * Gets a category by its display name (case insensitive).
     * 
     * @param displayName the display name to search for
     * @return the matching category or null if not found
     */
    public static RecipeCategory fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }

        for (RecipeCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(displayName.trim())) {
                return category;
            }
        }
        return null;
    }
}
