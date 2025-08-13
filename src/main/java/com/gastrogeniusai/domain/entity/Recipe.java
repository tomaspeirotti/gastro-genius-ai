package com.gastrogeniusai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe entity representing a cooking recipe in the GastroGenius AI system.
 * Contains all recipe information including ingredients, instructions, and
 * metadata.
 */
@Entity
@Table(name = "recipes", indexes = {
        @Index(name = "idx_recipe_title", columnList = "title"),
        @Index(name = "idx_recipe_owner", columnList = "owner_id"),
        @Index(name = "idx_recipe_category", columnList = "category"),
        @Index(name = "idx_recipe_difficulty", columnList = "difficulty"),
        @Index(name = "idx_recipe_created_at", columnList = "created_at")
})
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Recipe title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Instructions are required")
    @Size(min = 10, message = "Instructions must be at least 10 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @Min(value = 1, message = "Cooking time must be at least 1 minute")
    @Max(value = 1440, message = "Cooking time cannot exceed 1440 minutes (24 hours)")
    @Column(name = "cooking_time_minutes")
    private Integer cookingTimeMinutes;

    @Min(value = 0, message = "Preparation time cannot be negative")
    @Max(value = 720, message = "Preparation time cannot exceed 720 minutes (12 hours)")
    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Min(value = 1, message = "Servings must be at least 1")
    @Max(value = 50, message = "Servings cannot exceed 50")
    @Column(nullable = false)
    private Integer servings = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeCategory category = RecipeCategory.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeDifficulty difficulty = RecipeDifficulty.MEDIUM;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_public")
    private boolean isPublic = false;

    @Column(name = "is_ai_generated")
    private boolean isAiGenerated = false;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    @Column(name = "average_rating")
    private Double averageRating;

    @Min(value = 0, message = "Rating count cannot be negative")
    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @Size(max = 100, message = "Source cannot exceed 100 characters")
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ingredient> ingredients = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Recipe() {
    }

    public Recipe(String title, String description, String instructions, User owner) {
        this.title = title;
        this.description = description;
        this.instructions = instructions;
        this.owner = owner;
    }

    public Recipe(String title, String description, String instructions, Integer cookingTimeMinutes,
            Integer servings, User owner) {
        this(title, description, instructions, owner);
        this.cookingTimeMinutes = cookingTimeMinutes;
        this.servings = servings;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Helper methods
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

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !tags.contains(tag.trim().toLowerCase())) {
            tags.add(tag.trim().toLowerCase());
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean isOwnedBy(User user) {
        return owner != null && owner.equals(user);
    }

    public boolean isOwnedBy(Long userId) {
        return owner != null && owner.getId().equals(userId);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", difficulty=" + difficulty +
                ", servings=" + servings +
                ", cookingTimeMinutes=" + cookingTimeMinutes +
                ", isPublic=" + isPublic +
                ", isAiGenerated=" + isAiGenerated +
                ", createdAt=" + createdAt +
                '}';
    }
}
