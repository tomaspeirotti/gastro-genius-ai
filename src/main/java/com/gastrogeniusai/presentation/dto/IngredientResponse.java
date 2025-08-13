package com.gastrogeniusai.presentation.dto;

import com.gastrogeniusai.domain.entity.IngredientCategory;
import com.gastrogeniusai.domain.entity.MeasurementUnit;

import java.math.BigDecimal;

/**
 * DTO for ingredient responses.
 * Contains ingredient information for API responses.
 */
public class IngredientResponse {

    private Long id;
    private String name;
    private BigDecimal quantity;
    private MeasurementUnit unit;
    private String notes;
    private IngredientCategory category;
    private boolean isOptional;
    private Integer orderPosition;

    // Nutritional information (optional, per 100g)
    private BigDecimal caloriesPer100g;
    private BigDecimal proteinPer100g;
    private BigDecimal carbsPer100g;
    private BigDecimal fatPer100g;
    private BigDecimal fiberPer100g;

    // Constructors
    public IngredientResponse() {
    }

    public IngredientResponse(Long id, String name, BigDecimal quantity, MeasurementUnit unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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

    public String getCategoryDisplayName() {
        return category != null ? category.getDisplayName() : null;
    }

    public String getUnitDisplayName() {
        return unit != null ? unit.getDisplayName(quantity != null && quantity.doubleValue() != 1.0) : null;
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
        return "IngredientResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", category=" + category +
                ", isOptional=" + isOptional +
                ", orderPosition=" + orderPosition +
                ", hasNutritionalInfo=" + hasNutritionalInfo() +
                '}';
    }
}
