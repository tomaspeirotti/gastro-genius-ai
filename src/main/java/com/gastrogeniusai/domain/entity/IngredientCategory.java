package com.gastrogeniusai.domain.entity;

/**
 * Enumeration representing different categories of ingredients in the
 * GastroGenius AI system.
 * Used to classify and organize ingredients by type for better recipe
 * management.
 */
public enum IngredientCategory {
    VEGETABLES("Vegetables"),
    FRUITS("Fruits"),
    MEAT("Meat"),
    POULTRY("Poultry"),
    SEAFOOD("Seafood"),
    DAIRY("Dairy"),
    EGGS("Eggs"),
    GRAINS("Grains"),
    LEGUMES("Legumes"),
    NUTS_SEEDS("Nuts & Seeds"),
    HERBS("Herbs"),
    SPICES("Spices"),
    OILS_FATS("Oils & Fats"),
    SWEETENERS("Sweeteners"),
    CONDIMENTS("Condiments"),
    SAUCES("Sauces"),
    VINEGAR("Vinegar"),
    ALCOHOL("Alcohol"),
    BEVERAGES("Beverages"),
    BAKING("Baking"),
    PANTRY("Pantry"),
    FROZEN("Frozen"),
    CANNED("Canned"),
    PROCESSED("Processed"),
    OTHER("Other");

    private final String displayName;

    IngredientCategory(String displayName) {
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
     * Checks if the category represents fresh ingredients.
     * 
     * @return true if the category is for fresh ingredients
     */
    public boolean isFresh() {
        return this == VEGETABLES || this == FRUITS || this == MEAT ||
                this == POULTRY || this == SEAFOOD || this == DAIRY ||
                this == EGGS || this == HERBS;
    }

    /**
     * Checks if the category represents protein sources.
     * 
     * @return true if the category is protein-rich
     */
    public boolean isProteinSource() {
        return this == MEAT || this == POULTRY || this == SEAFOOD ||
                this == EGGS || this == LEGUMES || this == NUTS_SEEDS || this == DAIRY;
    }

    /**
     * Checks if the category represents plant-based ingredients.
     * 
     * @return true if the category is plant-based
     */
    public boolean isPlantBased() {
        return this == VEGETABLES || this == FRUITS || this == GRAINS ||
                this == LEGUMES || this == NUTS_SEEDS || this == HERBS || this == SPICES;
    }

    /**
     * Checks if the category represents animal products.
     * 
     * @return true if the category contains animal products
     */
    public boolean isAnimalProduct() {
        return this == MEAT || this == POULTRY || this == SEAFOOD ||
                this == DAIRY || this == EGGS;
    }

    /**
     * Checks if the category represents seasonings or flavor enhancers.
     * 
     * @return true if the category is for seasoning
     */
    public boolean isSeasoning() {
        return this == HERBS || this == SPICES || this == CONDIMENTS ||
                this == SAUCES || this == VINEGAR;
    }

    /**
     * Checks if the category represents shelf-stable ingredients.
     * 
     * @return true if the category is for shelf-stable items
     */
    public boolean isShelfStable() {
        return this == GRAINS || this == LEGUMES || this == NUTS_SEEDS ||
                this == SPICES || this == OILS_FATS || this == SWEETENERS ||
                this == CONDIMENTS || this == BAKING || this == PANTRY ||
                this == CANNED || this == PROCESSED;
    }

    /**
     * Checks if the category requires refrigeration.
     * 
     * @return true if the category typically requires refrigeration
     */
    public boolean requiresRefrigeration() {
        return this == MEAT || this == POULTRY || this == SEAFOOD ||
                this == DAIRY || this == EGGS ||
                (this == VEGETABLES && !isShelfStable()) ||
                (this == FRUITS && !isShelfStable());
    }

    /**
     * Gets the storage recommendation for this category.
     * 
     * @return storage recommendation as string
     */
    public String getStorageRecommendation() {
        if (requiresRefrigeration()) {
            return "Refrigerate";
        } else if (this == FROZEN) {
            return "Keep frozen";
        } else if (isShelfStable()) {
            return "Store in pantry";
        } else {
            return "Store in cool, dry place";
        }
    }

    /**
     * Gets a category by its display name (case insensitive).
     * 
     * @param displayName the display name to search for
     * @return the matching category or null if not found
     */
    public static IngredientCategory fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }

        for (IngredientCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(displayName.trim())) {
                return category;
            }
        }
        return null;
    }

    /**
     * Gets all categories that are suitable for a specific diet type.
     * 
     * @param dietType the diet type to filter by
     * @return array of categories suitable for the diet
     */
    public static IngredientCategory[] getForDiet(DietType dietType) {
        return java.util.Arrays.stream(values())
                .filter(category -> category.isSuitableForDiet(dietType))
                .toArray(IngredientCategory[]::new);
    }

    /**
     * Checks if this category is suitable for a specific diet.
     * 
     * @param dietType the diet type to check
     * @return true if suitable for the diet
     */
    public boolean isSuitableForDiet(DietType dietType) {
        return switch (dietType) {
            case VEGAN -> !isAnimalProduct();
            case VEGETARIAN -> this != MEAT && this != POULTRY && this != SEAFOOD;
            case PESCATARIAN -> this != MEAT && this != POULTRY;
            case KETO -> this != GRAINS && this != SWEETENERS &&
                    (this != FRUITS || this == HERBS); // Most fruits are high-carb
            case PALEO -> !isProcessed() && this != GRAINS && this != LEGUMES &&
                    this != DAIRY && this != PROCESSED;
            case GLUTEN_FREE -> this != GRAINS || isGlutenFreeGrain();
        };
    }

    private boolean isProcessed() {
        return this == PROCESSED || this == CANNED;
    }

    private boolean isGlutenFreeGrain() {
        // This is a simplification - in practice, you'd need more detailed ingredient
        // data
        return false; // Most grains contain gluten, would need specific grain identification
    }

    /**
     * Enumeration representing different diet types for filtering ingredients.
     */
    public enum DietType {
        VEGAN,
        VEGETARIAN,
        PESCATARIAN,
        KETO,
        PALEO,
        GLUTEN_FREE
    }
}
