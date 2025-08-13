package com.gastrogeniusai.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Ingredient entity representing an ingredient in a recipe.
 * Contains information about the ingredient name, quantity, unit, and
 * nutritional data.
 */
@Entity
@Table(name = "ingredients", indexes = {
        @Index(name = "idx_ingredient_recipe", columnList = "recipe_id"),
        @Index(name = "idx_ingredient_name", columnList = "name"),
        @Index(name = "idx_ingredient_category", columnList = "category")
})
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingredient name is required")
    @Size(min = 1, max = 100, message = "Ingredient name must be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be positive")
    @DecimalMax(value = "99999.99", message = "Quantity is too large")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal quantity;

    @NotNull(message = "Unit is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementUnit unit;

    @Size(max = 200, message = "Notes cannot exceed 200 characters")
    @Column(length = 200)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientCategory category = IngredientCategory.OTHER;

    @Column(name = "is_optional")
    private boolean isOptional = false;

    @Min(value = 0, message = "Order position cannot be negative")
    @Column(name = "order_position")
    private Integer orderPosition;

    // Nutritional information (optional, per 100g)
    @DecimalMin(value = "0", message = "Calories cannot be negative")
    @Column(name = "calories_per_100g")
    private BigDecimal caloriesPer100g;

    @DecimalMin(value = "0", message = "Protein cannot be negative")
    @Column(name = "protein_per_100g")
    private BigDecimal proteinPer100g;

    @DecimalMin(value = "0", message = "Carbs cannot be negative")
    @Column(name = "carbs_per_100g")
    private BigDecimal carbsPer100g;

    @DecimalMin(value = "0", message = "Fat cannot be negative")
    @Column(name = "fat_per_100g")
    private BigDecimal fatPer100g;

    @DecimalMin(value = "0", message = "Fiber cannot be negative")
    @Column(name = "fiber_per_100g")
    private BigDecimal fiberPer100g;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    // Constructors
    public Ingredient() {
    }

    public Ingredient(String name, BigDecimal quantity, MeasurementUnit unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Ingredient(String name, BigDecimal quantity, MeasurementUnit unit, Recipe recipe) {
        this(name, quantity, unit);
        this.recipe = recipe;
    }

    public Ingredient(String name, BigDecimal quantity, MeasurementUnit unit,
            IngredientCategory category, Recipe recipe) {
        this(name, quantity, unit, recipe);
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public MeasurementUnit getUnit() {
        return unit;
    }

    public void setUnit(MeasurementUnit unit) {
        this.unit = unit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public IngredientCategory getCategory() {
        return category;
    }

    public void setCategory(IngredientCategory category) {
        this.category = category;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean optional) {
        this.isOptional = optional;
    }

    public Integer getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    public BigDecimal getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(BigDecimal caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public BigDecimal getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(BigDecimal proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public BigDecimal getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(BigDecimal carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public BigDecimal getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(BigDecimal fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public BigDecimal getFiberPer100g() {
        return fiberPer100g;
    }

    public void setFiberPer100g(BigDecimal fiberPer100g) {
        this.fiberPer100g = fiberPer100g;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    // Helper methods
    public String getDisplayName() {
        StringBuilder display = new StringBuilder();

        if (quantity != null && unit != null) {
            display.append(quantity.stripTrailingZeros().toPlainString())
                    .append(" ")
                    .append(unit.getDisplayName(quantity.doubleValue() != 1.0));
        }

        if (name != null) {
            if (display.length() > 0) {
                display.append(" ");
            }
            display.append(name);
        }

        if (isOptional) {
            display.append(" (optional)");
        }

        return display.toString();
    }

    public String getQuantityDisplay() {
        if (quantity == null || unit == null) {
            return "";
        }

        return quantity.stripTrailingZeros().toPlainString() + " " +
                unit.getDisplayName(quantity.doubleValue() != 1.0);
    }

    public boolean hasNutritionalInfo() {
        return caloriesPer100g != null || proteinPer100g != null ||
                carbsPer100g != null || fatPer100g != null || fiberPer100g != null;
    }

    public BigDecimal getWeightInGrams() {
        if (quantity == null || unit == null) {
            return null;
        }

        return unit.convertToGrams(quantity);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", category=" + category +
                ", isOptional=" + isOptional +
                ", orderPosition=" + orderPosition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Ingredient that))
            return false;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
