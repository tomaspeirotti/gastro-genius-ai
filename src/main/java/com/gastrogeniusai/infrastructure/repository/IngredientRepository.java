package com.gastrogeniusai.infrastructure.repository;

import com.gastrogeniusai.domain.entity.Ingredient;
import com.gastrogeniusai.domain.entity.IngredientCategory;
import com.gastrogeniusai.domain.entity.MeasurementUnit;
import com.gastrogeniusai.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Ingredient entity operations.
 * Provides custom query methods for ingredient management and analysis.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Finds all ingredients for a specific recipe.
     * 
     * @param recipe the recipe
     * @return list of ingredients ordered by position
     */
    List<Ingredient> findByRecipeOrderByOrderPositionAsc(Recipe recipe);

    /**
     * Finds all ingredients for a specific recipe ID.
     * 
     * @param recipeId the recipe ID
     * @return list of ingredients ordered by position
     */
    List<Ingredient> findByRecipeIdOrderByOrderPositionAsc(Long recipeId);

    /**
     * Finds ingredients by name (case insensitive).
     * 
     * @param name     the ingredient name to search for
     * @param pageable pagination information
     * @return page of ingredients with matching name
     */
    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Ingredient> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Finds ingredients by category.
     * 
     * @param category the ingredient category
     * @param pageable pagination information
     * @return page of ingredients in the specified category
     */
    Page<Ingredient> findByCategory(IngredientCategory category, Pageable pageable);

    /**
     * Finds ingredients by measurement unit.
     * 
     * @param unit     the measurement unit
     * @param pageable pagination information
     * @return page of ingredients using the specified unit
     */
    Page<Ingredient> findByUnit(MeasurementUnit unit, Pageable pageable);

    /**
     * Finds optional ingredients.
     * 
     * @param pageable pagination information
     * @return page of optional ingredients
     */
    Page<Ingredient> findByIsOptionalTrue(Pageable pageable);

    /**
     * Finds required ingredients.
     * 
     * @param pageable pagination information
     * @return page of required ingredients
     */
    Page<Ingredient> findByIsOptionalFalse(Pageable pageable);

    /**
     * Finds ingredients with nutritional information.
     * 
     * @param pageable pagination information
     * @return page of ingredients that have nutritional data
     */
    @Query("SELECT i FROM Ingredient i WHERE i.caloriesPer100g IS NOT NULL OR " +
            "i.proteinPer100g IS NOT NULL OR i.carbsPer100g IS NOT NULL OR " +
            "i.fatPer100g IS NOT NULL OR i.fiberPer100g IS NOT NULL")
    Page<Ingredient> findWithNutritionalInfo(Pageable pageable);

    /**
     * Finds ingredients without nutritional information.
     * 
     * @param pageable pagination information
     * @return page of ingredients without nutritional data
     */
    @Query("SELECT i FROM Ingredient i WHERE i.caloriesPer100g IS NULL AND " +
            "i.proteinPer100g IS NULL AND i.carbsPer100g IS NULL AND " +
            "i.fatPer100g IS NULL AND i.fiberPer100g IS NULL")
    Page<Ingredient> findWithoutNutritionalInfo(Pageable pageable);

    /**
     * Finds ingredients with quantity above a threshold.
     * 
     * @param minQuantity minimum quantity
     * @param pageable    pagination information
     * @return page of ingredients with quantity >= minQuantity
     */
    Page<Ingredient> findByQuantityGreaterThanEqual(BigDecimal minQuantity, Pageable pageable);

    /**
     * Finds ingredients with quantity below a threshold.
     * 
     * @param maxQuantity maximum quantity
     * @param pageable    pagination information
     * @return page of ingredients with quantity <= maxQuantity
     */
    Page<Ingredient> findByQuantityLessThanEqual(BigDecimal maxQuantity, Pageable pageable);

    /**
     * Counts ingredients in a recipe.
     * 
     * @param recipe the recipe
     * @return number of ingredients in the recipe
     */
    long countByRecipe(Recipe recipe);

    /**
     * Counts ingredients by category.
     * 
     * @param category the ingredient category
     * @return number of ingredients in the category
     */
    long countByCategory(IngredientCategory category);

    /**
     * Counts optional ingredients in a recipe.
     * 
     * @param recipeId the recipe ID
     * @return number of optional ingredients
     */
    long countByRecipeIdAndIsOptionalTrue(Long recipeId);

    /**
     * Counts required ingredients in a recipe.
     * 
     * @param recipeId the recipe ID
     * @return number of required ingredients
     */
    long countByRecipeIdAndIsOptionalFalse(Long recipeId);

    /**
     * Gets the most commonly used ingredients.
     * 
     * @param pageable pagination information
     * @return page of ingredients ordered by usage frequency
     */
    @Query("SELECT i.name, COUNT(i) as usage_count FROM Ingredient i " +
            "GROUP BY LOWER(i.name) ORDER BY COUNT(i) DESC")
    Page<Object[]> findMostUsedIngredients(Pageable pageable);

    /**
     * Gets the most commonly used ingredients by category.
     * 
     * @param category the ingredient category
     * @param pageable pagination information
     * @return page of ingredients in category ordered by usage frequency
     */
    @Query("SELECT i.name, COUNT(i) as usage_count FROM Ingredient i " +
            "WHERE i.category = :category " +
            "GROUP BY LOWER(i.name) ORDER BY COUNT(i) DESC")
    Page<Object[]> findMostUsedIngredientsByCategory(@Param("category") IngredientCategory category,
            Pageable pageable);

    /**
     * Finds ingredients that appear in multiple recipes.
     * 
     * @param minRecipeCount minimum number of recipes
     * @param pageable       pagination information
     * @return page of popular ingredients
     */
    @Query("SELECT i.name FROM Ingredient i " +
            "GROUP BY LOWER(i.name) " +
            "HAVING COUNT(DISTINCT i.recipe) >= :minRecipeCount " +
            "ORDER BY COUNT(DISTINCT i.recipe) DESC")
    Page<String> findPopularIngredients(@Param("minRecipeCount") long minRecipeCount, Pageable pageable);

    /**
     * Gets average quantity for an ingredient across all recipes.
     * 
     * @param ingredientName the ingredient name
     * @param unit           the measurement unit
     * @return average quantity
     */
    @Query("SELECT AVG(i.quantity) FROM Ingredient i " +
            "WHERE LOWER(i.name) = LOWER(:ingredientName) AND i.unit = :unit")
    Optional<BigDecimal> getAverageQuantityForIngredient(@Param("ingredientName") String ingredientName,
            @Param("unit") MeasurementUnit unit);

    /**
     * Finds similar ingredients by name (fuzzy matching).
     * 
     * @param partialName partial ingredient name
     * @param pageable    pagination information
     * @return page of ingredients with similar names
     */
    @Query("SELECT DISTINCT i.name FROM Ingredient i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :partialName, '%')) " +
            "ORDER BY i.name")
    Page<String> findSimilarIngredientNames(@Param("partialName") String partialName, Pageable pageable);

    /**
     * Gets nutritional totals for a recipe.
     * 
     * @param recipeId the recipe ID
     * @return array containing [totalCalories, totalProtein, totalCarbs, totalFat,
     *         totalFiber]
     */
    @Query("SELECT " +
            "SUM(CASE WHEN i.caloriesPer100g IS NOT NULL THEN " +
            "    (i.caloriesPer100g * COALESCE(i.quantity, 0) / 100) ELSE 0 END), " +
            "SUM(CASE WHEN i.proteinPer100g IS NOT NULL THEN " +
            "    (i.proteinPer100g * COALESCE(i.quantity, 0) / 100) ELSE 0 END), " +
            "SUM(CASE WHEN i.carbsPer100g IS NOT NULL THEN " +
            "    (i.carbsPer100g * COALESCE(i.quantity, 0) / 100) ELSE 0 END), " +
            "SUM(CASE WHEN i.fatPer100g IS NOT NULL THEN " +
            "    (i.fatPer100g * COALESCE(i.quantity, 0) / 100) ELSE 0 END), " +
            "SUM(CASE WHEN i.fiberPer100g IS NOT NULL THEN " +
            "    (i.fiberPer100g * COALESCE(i.quantity, 0) / 100) ELSE 0 END) " +
            "FROM Ingredient i WHERE i.recipe.id = :recipeId")
    Object[] calculateNutritionalTotals(@Param("recipeId") Long recipeId);

    /**
     * Finds ingredients that are commonly used together.
     * 
     * @param ingredientName the base ingredient name
     * @param pageable       pagination information
     * @return page of ingredients commonly used with the base ingredient
     */
    @Query("SELECT i2.name, COUNT(i2) as cooccurrence_count " +
            "FROM Ingredient i1 JOIN Ingredient i2 ON i1.recipe = i2.recipe " +
            "WHERE LOWER(i1.name) = LOWER(:ingredientName) AND LOWER(i2.name) != LOWER(:ingredientName) " +
            "GROUP BY LOWER(i2.name) " +
            "ORDER BY COUNT(i2) DESC")
    Page<Object[]> findIngredientsUsedTogether(@Param("ingredientName") String ingredientName,
            Pageable pageable);

    /**
     * Finds ingredients by multiple categories.
     * 
     * @param categories list of categories to search
     * @param pageable   pagination information
     * @return page of ingredients in any of the specified categories
     */
    Page<Ingredient> findByCategoryIn(List<IngredientCategory> categories, Pageable pageable);

    /**
     * Deletes all ingredients for a specific recipe.
     * 
     * @param recipeId the recipe ID
     */
    void deleteByRecipeId(Long recipeId);

    /**
     * Gets the next order position for a recipe's ingredients.
     * 
     * @param recipeId the recipe ID
     * @return the next available order position
     */
    @Query("SELECT COALESCE(MAX(i.orderPosition), 0) + 1 FROM Ingredient i WHERE i.recipe.id = :recipeId")
    Integer getNextOrderPosition(@Param("recipeId") Long recipeId);
}
