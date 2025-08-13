package com.gastrogeniusai.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for AI recipe generation requests.
 * Contains ingredients and optional preferences for recipe generation.
 */
public class GenerateRecipeRequest {

    @NotEmpty(message = "At least one ingredient is required")
    @Size(min = 1, max = 20, message = "Please provide between 1 and 20 ingredients")
    private List<String> ingredients;

    @Size(max = 50, message = "Cuisine preference cannot exceed 50 characters")
    private String cuisine;

    @Size(max = 20, message = "Difficulty preference cannot exceed 20 characters")
    private String difficulty;

    private Boolean saveRecipe = true;

    // Constructors
    public GenerateRecipeRequest() {
    }

    public GenerateRecipeRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public GenerateRecipeRequest(List<String> ingredients, String cuisine, String difficulty) {
        this.ingredients = ingredients;
        this.cuisine = cuisine;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getSaveRecipe() {
        return saveRecipe;
    }

    public void setSaveRecipe(Boolean saveRecipe) {
        this.saveRecipe = saveRecipe;
    }

    @Override
    public String toString() {
        return "GenerateRecipeRequest{" +
                "ingredients=" + ingredients +
                ", cuisine='" + cuisine + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", saveRecipe=" + saveRecipe +
                '}';
    }
}
