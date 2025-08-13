package com.gastrogeniusai.domain.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Enumeration representing different measurement units for recipe ingredients.
 * Includes conversion capabilities to grams for nutritional calculations.
 */
public enum MeasurementUnit {
    // Weight units
    GRAM("g", "gram", "grams", UnitType.WEIGHT, BigDecimal.ONE),
    KILOGRAM("kg", "kilogram", "kilograms", UnitType.WEIGHT, new BigDecimal("1000")),
    OUNCE("oz", "ounce", "ounces", UnitType.WEIGHT, new BigDecimal("28.35")),
    POUND("lb", "pound", "pounds", UnitType.WEIGHT, new BigDecimal("453.59")),

    // Volume units (liquid)
    MILLILITER("ml", "milliliter", "milliliters", UnitType.VOLUME, new BigDecimal("1")), // Approximate for water
    LITER("l", "liter", "liters", UnitType.VOLUME, new BigDecimal("1000")),
    FLUID_OUNCE("fl oz", "fluid ounce", "fluid ounces", UnitType.VOLUME, new BigDecimal("29.57")),
    CUP("cup", "cup", "cups", UnitType.VOLUME, new BigDecimal("240")), // US cup
    PINT("pint", "pint", "pints", UnitType.VOLUME, new BigDecimal("473")), // US pint
    QUART("quart", "quart", "quarts", UnitType.VOLUME, new BigDecimal("946")), // US quart
    GALLON("gallon", "gallon", "gallons", UnitType.VOLUME, new BigDecimal("3785")), // US gallon

    // Cooking measurements
    TEASPOON("tsp", "teaspoon", "teaspoons", UnitType.VOLUME, new BigDecimal("5")),
    TABLESPOON("tbsp", "tablespoon", "tablespoons", UnitType.VOLUME, new BigDecimal("15")),

    // Count units
    PIECE("piece", "piece", "pieces", UnitType.COUNT, null),
    ITEM("item", "item", "items", UnitType.COUNT, null),
    SLICE("slice", "slice", "slices", UnitType.COUNT, null),
    CLOVE("clove", "clove", "cloves", UnitType.COUNT, null),
    HEAD("head", "head", "heads", UnitType.COUNT, null),
    BUNCH("bunch", "bunch", "bunches", UnitType.COUNT, null),
    PACKAGE("package", "package", "packages", UnitType.COUNT, null),
    CAN("can", "can", "cans", UnitType.COUNT, null),
    BOTTLE("bottle", "bottle", "bottles", UnitType.COUNT, null),

    // Special measurements
    PINCH("pinch", "pinch", "pinches", UnitType.SPECIAL, new BigDecimal("0.3")),
    DASH("dash", "dash", "dashes", UnitType.SPECIAL, new BigDecimal("0.6")),
    DROP("drop", "drop", "drops", UnitType.SPECIAL, new BigDecimal("0.05")),
    TO_TASTE("to taste", "to taste", "to taste", UnitType.SPECIAL, null);

    private final String abbreviation;
    private final String singular;
    private final String plural;
    private final UnitType type;
    private final BigDecimal gramsConversion; // Conversion factor to grams (for weight) or approximate grams for volume

    MeasurementUnit(String abbreviation, String singular, String plural, UnitType type, BigDecimal gramsConversion) {
        this.abbreviation = abbreviation;
        this.singular = singular;
        this.plural = plural;
        this.type = type;
        this.gramsConversion = gramsConversion;
    }

    /**
     * Gets the appropriate display name based on quantity.
     * 
     * @param plural whether to use plural form
     * @return the display name
     */
    public String getDisplayName(boolean plural) {
        return plural ? this.plural : this.singular;
    }

    /**
     * Gets the abbreviation of the unit.
     * 
     * @return unit abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Gets the singular form of the unit.
     * 
     * @return singular form
     */
    public String getSingular() {
        return singular;
    }

    /**
     * Gets the plural form of the unit.
     * 
     * @return plural form
     */
    public String getPlural() {
        return plural;
    }

    /**
     * Gets the type of this measurement unit.
     * 
     * @return unit type
     */
    public UnitType getType() {
        return type;
    }

    /**
     * Checks if this unit can be converted to grams.
     * 
     * @return true if conversion is possible
     */
    public boolean canConvertToGrams() {
        return gramsConversion != null;
    }

    /**
     * Converts the given quantity to grams.
     * 
     * @param quantity the quantity to convert
     * @return the equivalent weight in grams, or null if conversion is not possible
     */
    public BigDecimal convertToGrams(BigDecimal quantity) {
        if (quantity == null || gramsConversion == null) {
            return null;
        }

        return quantity.multiply(gramsConversion).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Checks if this unit is suitable for weighing ingredients.
     * 
     * @return true if it's a weight unit
     */
    public boolean isWeightUnit() {
        return type == UnitType.WEIGHT;
    }

    /**
     * Checks if this unit is suitable for measuring liquids.
     * 
     * @return true if it's a volume unit
     */
    public boolean isVolumeUnit() {
        return type == UnitType.VOLUME;
    }

    /**
     * Checks if this unit is for counting items.
     * 
     * @return true if it's a count unit
     */
    public boolean isCountUnit() {
        return type == UnitType.COUNT;
    }

    /**
     * Finds a measurement unit by its display name (singular, plural, or
     * abbreviation).
     * 
     * @param name the name to search for (case insensitive)
     * @return the matching unit or null if not found
     */
    public static MeasurementUnit fromDisplayName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String cleanName = name.trim().toLowerCase();

        for (MeasurementUnit unit : values()) {
            if (unit.abbreviation.toLowerCase().equals(cleanName) ||
                    unit.singular.toLowerCase().equals(cleanName) ||
                    unit.plural.toLowerCase().equals(cleanName)) {
                return unit;
            }
        }

        return null;
    }

    /**
     * Gets all units of a specific type.
     * 
     * @param type the unit type to filter by
     * @return array of units of the specified type
     */
    public static MeasurementUnit[] getByType(UnitType type) {
        return java.util.Arrays.stream(values())
                .filter(unit -> unit.type == type)
                .toArray(MeasurementUnit[]::new);
    }

    /**
     * Enumeration representing different types of measurement units.
     */
    public enum UnitType {
        WEIGHT("Weight"),
        VOLUME("Volume"),
        COUNT("Count"),
        SPECIAL("Special");

        private final String displayName;

        UnitType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
