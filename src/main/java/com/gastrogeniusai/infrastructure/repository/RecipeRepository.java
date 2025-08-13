package com.gastrogeniusai.infrastructure.repository;

import com.gastrogeniusai.domain.entity.Recipe;
import com.gastrogeniusai.domain.entity.RecipeCategory;
import com.gastrogeniusai.domain.entity.RecipeDifficulty;
import com.gastrogeniusai.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Recipe entity operations.
 * Provides custom query methods for recipe management and searching.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Finds all recipes owned by a specific user.
     * 
     * @param owner    the user who owns the recipes
     * @param pageable pagination information
     * @return page of recipes owned by the user
     */
    Page<Recipe> findByOwner(User owner, Pageable pageable);

    /**
     * Finds all recipes owned by a specific user ID.
     * 
     * @param ownerId  the ID of the user who owns the recipes
     * @param pageable pagination information
     * @return page of recipes owned by the user
     */
    Page<Recipe> findByOwnerId(Long ownerId, Pageable pageable);

    /**
     * Finds all public recipes.
     * 
     * @param pageable pagination information
     * @return page of public recipes
     */
    Page<Recipe> findByIsPublicTrue(Pageable pageable);

    /**
     * Finds all private recipes for a specific user.
     * 
     * @param ownerId  the ID of the user
     * @param pageable pagination information
     * @return page of private recipes
     */
    @Query("SELECT r FROM Recipe r WHERE r.owner.id = :ownerId AND r.isPublic = false")
    Page<Recipe> findPrivateRecipesByOwner(@Param("ownerId") Long ownerId, Pageable pageable);

    /**
     * Finds recipes by category.
     * 
     * @param category the recipe category
     * @param pageable pagination information
     * @return page of recipes in the specified category
     */
    Page<Recipe> findByCategory(RecipeCategory category, Pageable pageable);

    /**
     * Finds public recipes by category.
     * 
     * @param category the recipe category
     * @param pageable pagination information
     * @return page of public recipes in the specified category
     */
    Page<Recipe> findByCategoryAndIsPublicTrue(RecipeCategory category, Pageable pageable);

    /**
     * Finds recipes by difficulty level.
     * 
     * @param difficulty the recipe difficulty
     * @param pageable   pagination information
     * @return page of recipes with the specified difficulty
     */
    Page<Recipe> findByDifficulty(RecipeDifficulty difficulty, Pageable pageable);

    /**
     * Finds AI-generated recipes.
     * 
     * @param pageable pagination information
     * @return page of AI-generated recipes
     */
    Page<Recipe> findByIsAiGeneratedTrue(Pageable pageable);

    /**
     * Finds non-AI-generated recipes.
     * 
     * @param pageable pagination information
     * @return page of manually created recipes
     */
    Page<Recipe> findByIsAiGeneratedFalse(Pageable pageable);

    /**
     * Finds recipes with cooking time within a range.
     * 
     * @param minMinutes minimum cooking time in minutes
     * @param maxMinutes maximum cooking time in minutes
     * @param pageable   pagination information
     * @return page of recipes within the time range
     */
    @Query("SELECT r FROM Recipe r WHERE r.cookingTimeMinutes BETWEEN :minMinutes AND :maxMinutes")
    Page<Recipe> findByCookingTimeRange(@Param("minMinutes") Integer minMinutes,
            @Param("maxMinutes") Integer maxMinutes,
            Pageable pageable);

    /**
     * Finds recipes with servings within a range.
     * 
     * @param minServings minimum number of servings
     * @param maxServings maximum number of servings
     * @param pageable    pagination information
     * @return page of recipes within the servings range
     */
    Page<Recipe> findByServingsBetween(Integer minServings, Integer maxServings, Pageable pageable);

    /**
     * Searches recipes by title or description containing the given text.
     * 
     * @param searchTerm the text to search for
     * @param pageable   pagination information
     * @return page of matching recipes
     */
    @Query("SELECT r FROM Recipe r WHERE " +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Recipe> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Searches public recipes by title or description.
     * 
     * @param searchTerm the text to search for
     * @param pageable   pagination information
     * @return page of matching public recipes
     */
    @Query("SELECT r FROM Recipe r WHERE r.isPublic = true AND (" +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Recipe> searchPublicRecipes(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Finds recipes containing specific ingredients.
     * 
     * @param ingredientNames list of ingredient names to search for
     * @param pageable        pagination information
     * @return page of recipes containing any of the specified ingredients
     */
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.ingredients i WHERE " +
            "LOWER(i.name) IN :ingredientNames")
    Page<Recipe> findByIngredientsContaining(@Param("ingredientNames") List<String> ingredientNames,
            Pageable pageable);

    /**
     * Finds recipes containing all specified ingredients.
     * 
     * @param ingredientNames list of ingredient names
     * @param ingredientCount number of ingredients to match
     * @param pageable        pagination information
     * @return page of recipes containing all specified ingredients
     */
    @Query("SELECT r FROM Recipe r WHERE r.id IN (" +
            "SELECT i.recipe.id FROM Ingredient i WHERE LOWER(i.name) IN :ingredientNames " +
            "GROUP BY i.recipe.id HAVING COUNT(DISTINCT i.name) = :ingredientCount)")
    Page<Recipe> findByAllIngredientsContaining(@Param("ingredientNames") List<String> ingredientNames,
            @Param("ingredientCount") long ingredientCount,
            Pageable pageable);

    /**
     * Finds recipes with tags containing the specified text.
     * 
     * @param tag      the tag to search for
     * @param pageable pagination information
     * @return page of recipes with matching tags
     */
    @Query("SELECT r FROM Recipe r JOIN r.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :tag, '%'))")
    Page<Recipe> findByTagsContaining(@Param("tag") String tag, Pageable pageable);

    /**
     * Finds the most recently created recipes.
     * 
     * @param pageable pagination information
     * @return page of recently created recipes
     */
    Page<Recipe> findByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Finds the most recently updated recipes.
     * 
     * @param pageable pagination information
     * @return page of recently updated recipes
     */
    Page<Recipe> findByOrderByUpdatedAtDesc(Pageable pageable);

    /**
     * Finds recipes created after a specific date.
     * 
     * @param date     the date to filter from
     * @param pageable pagination information
     * @return page of recipes created after the date
     */
    Page<Recipe> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);

    /**
     * Finds top-rated recipes.
     * 
     * @param minRating minimum average rating
     * @param pageable  pagination information
     * @return page of highly rated recipes
     */
    @Query("SELECT r FROM Recipe r WHERE r.averageRating >= :minRating AND r.ratingCount > 0 " +
            "ORDER BY r.averageRating DESC, r.ratingCount DESC")
    Page<Recipe> findTopRatedRecipes(@Param("minRating") Double minRating, Pageable pageable);

    /**
     * Finds popular recipes (most rated).
     * 
     * @param pageable pagination information
     * @return page of popular recipes ordered by rating count
     */
    @Query("SELECT r FROM Recipe r WHERE r.ratingCount > 0 ORDER BY r.ratingCount DESC")
    Page<Recipe> findMostPopularRecipes(Pageable pageable);

    /**
     * Counts recipes by owner.
     * 
     * @param owner the recipe owner
     * @return number of recipes owned by the user
     */
    long countByOwner(User owner);

    /**
     * Counts public recipes.
     * 
     * @return number of public recipes
     */
    long countByIsPublicTrue();

    /**
     * Counts recipes by category.
     * 
     * @param category the recipe category
     * @return number of recipes in the category
     */
    long countByCategory(RecipeCategory category);

    /**
     * Counts AI-generated recipes.
     * 
     * @return number of AI-generated recipes
     */
    long countByIsAiGeneratedTrue();

    /**
     * Gets average cooking time for all recipes.
     * 
     * @return average cooking time in minutes
     */
    @Query("SELECT AVG(r.cookingTimeMinutes) FROM Recipe r WHERE r.cookingTimeMinutes IS NOT NULL")
    Double getAverageCookingTime();

    /**
     * Gets average rating for all recipes.
     * 
     * @return average rating
     */
    @Query("SELECT AVG(r.averageRating) FROM Recipe r WHERE r.averageRating IS NOT NULL")
    Double getAverageRating();

    /**
     * Finds a recipe by ID and owner (for security checks).
     * 
     * @param id    the recipe ID
     * @param owner the recipe owner
     * @return Optional containing the recipe if found and owned by user
     */
    Optional<Recipe> findByIdAndOwner(Long id, User owner);

    /**
     * Finds a recipe by ID and owner ID (for security checks).
     * 
     * @param id      the recipe ID
     * @param ownerId the owner's ID
     * @return Optional containing the recipe if found and owned by user
     */
    Optional<Recipe> findByIdAndOwnerId(Long id, Long ownerId);

    /**
     * Complex search with multiple filters.
     * 
     * @param searchTerm     search term for title/description
     * @param category       recipe category (optional)
     * @param difficulty     recipe difficulty (optional)
     * @param minCookingTime minimum cooking time (optional)
     * @param maxCookingTime maximum cooking time (optional)
     * @param isPublic       whether to include only public recipes
     * @param pageable       pagination information
     * @return page of filtered recipes
     */
    @Query("SELECT r FROM Recipe r WHERE " +
            "(:searchTerm IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:category IS NULL OR r.category = :category) AND " +
            "(:difficulty IS NULL OR r.difficulty = :difficulty) AND " +
            "(:minCookingTime IS NULL OR r.cookingTimeMinutes >= :minCookingTime) AND " +
            "(:maxCookingTime IS NULL OR r.cookingTimeMinutes <= :maxCookingTime) AND " +
            "(:isPublic = false OR r.isPublic = true)")
    Page<Recipe> findWithFilters(@Param("searchTerm") String searchTerm,
            @Param("category") RecipeCategory category,
            @Param("difficulty") RecipeDifficulty difficulty,
            @Param("minCookingTime") Integer minCookingTime,
            @Param("maxCookingTime") Integer maxCookingTime,
            @Param("isPublic") boolean isPublic,
            Pageable pageable);
}
