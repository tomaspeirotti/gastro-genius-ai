package com.gastrogeniusai.presentation.dto;

import com.gastrogeniusai.domain.entity.IngredientCategory;
import com.gastrogeniusai.domain.entity.MeasurementUnit;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for ingredient creation and update requests.
 * Contains all necessary information to create or update an ingredient.
 */
public class IngredientRequest {

    @NotBlank(message = "Ingredient name is required")
    @Size(min = 1, max = 100, message = "Ingredient name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be positive")
    @DecimalMax(value = "99999.99", message = "Quantity is too large")
    private BigDecimal quantity;

    @NotNull(message = "Unit is required")
    private MeasurementUnit unit;

    @Size(max = 200, message = "Notes cannot exceed 200 characters")
    private String notes;

    private IngredientCategory category = IngredientCategory.OTHER;

    private Boolean isOptional = false;

    // Nutritional information (optional, per 100g)
    @DecimalMin(value = "0", message = "Calories cannot be negative")
    private BigDecimal caloriesPer100g;

    @DecimalMin(value = "0", message = "Protein cannot be negative")
    private BigDecimal proteinPer100g;

    @DecimalMin(value = "0", message = "Carbs cannot be negative")
    private BigDecimal carbsPer100g;

    @DecimalMin(value = "0", message = "Fat cannot be negative")
    private BigDecimal fatPer100g;

    @DecimalMin(value = "0", message = "Fiber cannot be negative")
    private BigDecimal fiberPer100g;

    // Constructors
    public IngredientRequest() {
    }

    public IngredientRequest(String name, BigDecimal quantity, MeasurementUnit unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public IngredientRequest(String name, BigDecimal quantity, MeasurementUnit unit,
            IngredientCategory category) {
        this(name, quantity, unit);
        this.category = category;
    }

    public IngredientRequest(String name, BigDecimal quantity, MeasurementUnit unit,
            IngredientCategory category, Boolean isOptional) {
        this(name, quantity, unit, category);
        this.isOptional = isOptional;
    }

    // Getters and Setters
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

    public Boolean getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
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

        if (isOptional != null && isOptional) {
            display.append(" (optional)");
        }

        return display.toString();
    }

    public boolean hasNutritionalInfo() {
        return caloriesPer100g != null || proteinPer100g != null ||
                carbsPer100g != null || fatPer100g != null || fiberPer100g != null;
    }

    @Override
    public String toString() {
        return "IngredientRequest{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", category=" + category +
                ", isOptional=" + isOptional +
                ", hasNutritionalInfo=" + hasNutritionalInfo() +
                '}';
    }
}
