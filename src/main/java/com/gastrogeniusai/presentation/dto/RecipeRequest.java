package com.gastrogeniusai.presentation.dto;

import com.gastrogeniusai.domain.entity.RecipeCategory;
import com.gastrogeniusai.domain.entity.RecipeDifficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for recipe creation and update requests.
 * Contains all necessary information to create or update a recipe.
 */
public class RecipeRequest {

    @NotBlank(message = "Recipe title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Instructions are required")
    @Size(min = 10, message = "Instructions must be at least 10 characters")
    private String instructions;

    @Min(value = 1, message = "Cooking time must be at least 1 minute")
    @Max(value = 1440, message = "Cooking time cannot exceed 1440 minutes (24 hours)")
    private Integer cookingTimeMinutes;

    @Min(value = 0, message = "Preparation time cannot be negative")
    @Max(value = 720, message = "Preparation time cannot exceed 720 minutes (12 hours)")
    private Integer prepTimeMinutes;

    @NotNull(message = "Servings is required")
    @Min(value = 1, message = "Servings must be at least 1")
    @Max(value = 50, message = "Servings cannot exceed 50")
    private Integer servings;

    @NotNull(message = "Category is required")
    private RecipeCategory category;

    @NotNull(message = "Difficulty is required")
    private RecipeDifficulty difficulty;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    private Boolean isPublic = false;

    @Size(max = 100, message = "Source cannot exceed 100 characters")
    private String source;

    @Valid
    @NotEmpty(message = "At least one ingredient is required")
    private List<IngredientRequest> ingredients = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    // Constructors
    public RecipeRequest() {
    }

    public RecipeRequest(String title, String description, String instructions,
            Integer cookingTimeMinutes, Integer servings,
            RecipeCategory category, RecipeDifficulty difficulty) {
        this.title = title;
        this.description = description;
        this.instructions = instructions;
        this.cookingTimeMinutes = cookingTimeMinutes;
        this.servings = servings;
        this.category = category;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getCookingTimeMinutes() {
        return cookingTimeMinutes;
    }

    public void setCookingTimeMinutes(Integer cookingTimeMinutes) {
        this.cookingTimeMinutes = cookingTimeMinutes;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public RecipeDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(RecipeDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<IngredientRequest> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientRequest> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Helper methods
    public void addIngredient(IngredientRequest ingredient) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(ingredient);
    }

    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        if (tag != null && !tag.trim().isEmpty() && !this.tags.contains(tag.trim().toLowerCase())) {
            this.tags.add(tag.trim().toLowerCase());
        }
    }

    public Integer getTotalTimeMinutes() {
        int total = 0;
        if (prepTimeMinutes != null) {
            total += prepTimeMinutes;
        }
        if (cookingTimeMinutes != null) {
            total += cookingTimeMinutes;
        }
        return total > 0 ? total : null;
    }

    @Override
    public String toString() {
        return "RecipeRequest{" +
                "title='" + title + '\'' +
                ", category=" + category +
                ", difficulty=" + difficulty +
                ", servings=" + servings +
                ", cookingTimeMinutes=" + cookingTimeMinutes +
                ", isPublic=" + isPublic +
                ", ingredientsCount=" + (ingredients != null ? ingredients.size() : 0) +
                ", tagsCount=" + (tags != null ? tags.size() : 0) +
                '}';
    }
}
