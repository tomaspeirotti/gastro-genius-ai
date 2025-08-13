package com.gastrogeniusai.presentation.dto;

import com.gastrogeniusai.domain.entity.RecipeCategory;
import com.gastrogeniusai.domain.entity.RecipeDifficulty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for recipe responses.
 * Contains recipe information for API responses.
 */
public class RecipeResponse {

    private Long id;
    private String title;
    private String description;
    private String instructions;
    private Integer cookingTimeMinutes;
    private Integer prepTimeMinutes;
    private Integer totalTimeMinutes;
    private Integer servings;
    private RecipeCategory category;
    private RecipeDifficulty difficulty;
    private String imageUrl;
    private boolean isPublic;
    private boolean isAiGenerated;
    private Double averageRating;
    private Integer ratingCount;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OwnerInfo owner;
    private List<IngredientResponse> ingredients;
    private List<String> tags;

    // Constructors
    public RecipeResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getTotalTimeMinutes() {
        return totalTimeMinutes;
    }

    public void setTotalTimeMinutes(Integer totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isAiGenerated() {
        return isAiGenerated;
    }

    public void setAiGenerated(boolean aiGenerated) {
        this.isAiGenerated = aiGenerated;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OwnerInfo getOwner() {
        return owner;
    }

    public void setOwner(OwnerInfo owner) {
        this.owner = owner;
    }

    public List<IngredientResponse> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Helper methods
    public String getCategoryDisplayName() {
        return category != null ? category.getDisplayName() : null;
    }

    public String getDifficultyDisplayName() {
        return difficulty != null ? difficulty.getDisplayName() : null;
    }

    public String getFormattedCookingTime() {
        if (cookingTimeMinutes == null)
            return null;

        int hours = cookingTimeMinutes / 60;
        int minutes = cookingTimeMinutes % 60;

        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return minutes + "m";
        }
    }

    public String getFormattedTotalTime() {
        if (totalTimeMinutes == null)
            return null;

        int hours = totalTimeMinutes / 60;
        int minutes = totalTimeMinutes % 60;

        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return minutes + "m";
        }
    }

    /**
     * Nested class for owner information in recipe responses.
     */
    public static class OwnerInfo {
        private Long id;
        private String username;
        private String fullName;

        public OwnerInfo() {
        }

        public OwnerInfo(Long id, String username, String fullName) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        @Override
        public String toString() {
            return "OwnerInfo{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", fullName='" + fullName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", difficulty=" + difficulty +
                ", servings=" + servings +
                ", cookingTimeMinutes=" + cookingTimeMinutes +
                ", isPublic=" + isPublic +
                ", isAiGenerated=" + isAiGenerated +
                ", averageRating=" + averageRating +
                ", ratingCount=" + ratingCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
