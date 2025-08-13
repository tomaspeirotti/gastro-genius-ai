package com.gastrogeniusai.application.service;

import com.gastrogeniusai.domain.entity.*;
import com.gastrogeniusai.infrastructure.repository.IngredientRepository;
import com.gastrogeniusai.infrastructure.repository.RecipeRepository;
import com.gastrogeniusai.infrastructure.repository.UserRepository;
import com.gastrogeniusai.presentation.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling recipe operations.
 * Manages recipe CRUD operations, search, and business logic.
 */
@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository,
            UserRepository userRepository,
            IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Creates a new recipe for the specified user.
     * 
     * @param recipeRequest the recipe data
     * @param username      the owner's username
     * @return the created recipe response
     */
    public RecipeResponse createRecipe(RecipeRequest recipeRequest, String username) {
        User owner = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Recipe recipe = mapToEntity(recipeRequest, owner);
        Recipe savedRecipe = recipeRepository.save(recipe);

        return mapToResponse(savedRecipe);
    }

    /**
     * Updates an existing recipe.
     * 
     * @param recipeId      the recipe ID
     * @param recipeRequest the updated recipe data
     * @param username      the current user's username
     * @return the updated recipe response
     */
    public RecipeResponse updateRecipe(Long recipeId, RecipeRequest recipeRequest, String username) {
        Recipe existingRecipe = getRecipeByIdAndValidateOwnership(recipeId, username);

        updateRecipeEntity(existingRecipe, recipeRequest);
        Recipe updatedRecipe = recipeRepository.save(existingRecipe);

        return mapToResponse(updatedRecipe);
    }

    /**
     * Gets a recipe by ID.
     * 
     * @param recipeId the recipe ID
     * @param username the current user's username (for access control)
     * @return the recipe response
     */
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long recipeId, String username) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));

        // Check if user can access this recipe
        if (!canUserAccessRecipe(recipe, username)) {
            throw new AccessDeniedException("Access denied to recipe: " + recipeId);
        }

        return mapToResponse(recipe);
    }

    /**
     * Gets a public recipe by ID (no authentication required).
     * 
     * @param recipeId the recipe ID
     * @return the recipe response
     */
    @Transactional(readOnly = true)
    public RecipeResponse getPublicRecipeById(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));

        if (!recipe.isPublic()) {
            throw new AccessDeniedException("Recipe is not public: " + recipeId);
        }

        return mapToResponse(recipe);
    }

    /**
     * Gets all recipes for the current user.
     * 
     * @param username the user's username
     * @param pageable pagination information
     * @return page of user's recipes
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getUserRecipes(String username, Pageable pageable) {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return recipeRepository.findByOwner(user, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets all public recipes.
     * 
     * @param pageable pagination information
     * @return page of public recipes
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getPublicRecipes(Pageable pageable) {
        return recipeRepository.findByIsPublicTrue(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Searches recipes with filters.
     * 
     * @param searchTerm     search term for title/description
     * @param category       recipe category filter
     * @param difficulty     recipe difficulty filter
     * @param minCookingTime minimum cooking time filter
     * @param maxCookingTime maximum cooking time filter
     * @param publicOnly     whether to search only public recipes
     * @param username       current user's username (for private recipes)
     * @param pageable       pagination information
     * @return page of filtered recipes
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchRecipes(String searchTerm, RecipeCategory category,
            RecipeDifficulty difficulty, Integer minCookingTime,
            Integer maxCookingTime, boolean publicOnly,
            String username, Pageable pageable) {

        Page<Recipe> recipes = recipeRepository.findWithFilters(
                searchTerm, category, difficulty, minCookingTime, maxCookingTime, publicOnly, pageable);

        return recipes.map(this::mapToResponse);
    }

    /**
     * Searches recipes by ingredients.
     * 
     * @param ingredientNames list of ingredient names
     * @param pageable        pagination information
     * @return page of recipes containing the ingredients
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchRecipesByIngredients(List<String> ingredientNames, Pageable pageable) {
        List<String> lowerCaseNames = ingredientNames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return recipeRepository.findByIngredientsContaining(lowerCaseNames, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets recipes by category.
     * 
     * @param category   the recipe category
     * @param publicOnly whether to include only public recipes
     * @param pageable   pagination information
     * @return page of recipes in the category
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getRecipesByCategory(RecipeCategory category, boolean publicOnly, Pageable pageable) {
        if (publicOnly) {
            return recipeRepository.findByCategoryAndIsPublicTrue(category, pageable)
                    .map(this::mapToResponse);
        } else {
            return recipeRepository.findByCategory(category, pageable)
                    .map(this::mapToResponse);
        }
    }

    /**
     * Gets recipes by difficulty.
     * 
     * @param difficulty the recipe difficulty
     * @param pageable   pagination information
     * @return page of recipes with the difficulty
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getRecipesByDifficulty(RecipeDifficulty difficulty, Pageable pageable) {
        return recipeRepository.findByDifficulty(difficulty, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets top-rated recipes.
     * 
     * @param minRating minimum average rating
     * @param pageable  pagination information
     * @return page of top-rated recipes
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getTopRatedRecipes(Double minRating, Pageable pageable) {
        return recipeRepository.findTopRatedRecipes(minRating, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets most popular recipes.
     * 
     * @param pageable pagination information
     * @return page of popular recipes
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getMostPopularRecipes(Pageable pageable) {
        return recipeRepository.findMostPopularRecipes(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Gets recently created recipes.
     * 
     * @param pageable pagination information
     * @return page of recent recipes
     */
    @Transactional(readOnly = true)
    public Page<RecipeResponse> getRecentRecipes(Pageable pageable) {
        return recipeRepository.findByOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Deletes a recipe.
     * 
     * @param recipeId the recipe ID
     * @param username the current user's username
     */
    public void deleteRecipe(Long recipeId, String username) {
        Recipe recipe = getRecipeByIdAndValidateOwnership(recipeId, username);
        recipeRepository.delete(recipe);
    }

    /**
     * Toggles the public status of a recipe.
     * 
     * @param recipeId the recipe ID
     * @param username the current user's username
     * @return the updated recipe response
     */
    public RecipeResponse toggleRecipeVisibility(Long recipeId, String username) {
        Recipe recipe = getRecipeByIdAndValidateOwnership(recipeId, username);
        recipe.setPublic(!recipe.isPublic());
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return mapToResponse(updatedRecipe);
    }

    /**
     * Gets recipe statistics for a user.
     * 
     * @param username the user's username
     * @return recipe statistics
     */
    @Transactional(readOnly = true)
    public RecipeStatistics getUserRecipeStatistics(String username) {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        long totalRecipes = recipeRepository.countByOwner(user);
        long publicRecipes = recipeRepository.findByOwner(user, Pageable.unpaged())
                .stream()
                .mapToLong(recipe -> recipe.isPublic() ? 1 : 0)
                .sum();

        return new RecipeStatistics(totalRecipes, publicRecipes, totalRecipes - publicRecipes);
    }

    // Private helper methods

    private Recipe getRecipeByIdAndValidateOwnership(Long recipeId, String username) {
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return recipeRepository.findByIdAndOwner(recipeId, user)
                .orElseThrow(() -> new AccessDeniedException("Recipe not found or access denied: " + recipeId));
    }

    private boolean canUserAccessRecipe(Recipe recipe, String username) {
        if (recipe.isPublic()) {
            return true;
        }

        if (username == null) {
            return false;
        }

        User user = userRepository.findByUsernameOrEmail(username).orElse(null);
        return user != null && recipe.isOwnedBy(user);
    }

    private Recipe mapToEntity(RecipeRequest request, User owner) {
        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setCookingTimeMinutes(request.getCookingTimeMinutes());
        recipe.setPrepTimeMinutes(request.getPrepTimeMinutes());
        recipe.setServings(request.getServings());
        recipe.setCategory(request.getCategory());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setPublic(request.getIsPublic() != null ? request.getIsPublic() : false);
        recipe.setSource(request.getSource());
        recipe.setOwner(owner);
        recipe.setTags(request.getTags());

        // Add ingredients
        if (request.getIngredients() != null) {
            for (int i = 0; i < request.getIngredients().size(); i++) {
                IngredientRequest ingredientRequest = request.getIngredients().get(i);
                Ingredient ingredient = mapIngredientToEntity(ingredientRequest, recipe);
                ingredient.setOrderPosition(i + 1);
                recipe.addIngredient(ingredient);
            }
        }

        return recipe;
    }

    private void updateRecipeEntity(Recipe recipe, RecipeRequest request) {
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setCookingTimeMinutes(request.getCookingTimeMinutes());
        recipe.setPrepTimeMinutes(request.getPrepTimeMinutes());
        recipe.setServings(request.getServings());
        recipe.setCategory(request.getCategory());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setImageUrl(request.getImageUrl());
        if (request.getIsPublic() != null) {
            recipe.setPublic(request.getIsPublic());
        }
        recipe.setSource(request.getSource());
        recipe.setTags(request.getTags());

        // Update ingredients
        recipe.getIngredients().clear();
        if (request.getIngredients() != null) {
            for (int i = 0; i < request.getIngredients().size(); i++) {
                IngredientRequest ingredientRequest = request.getIngredients().get(i);
                Ingredient ingredient = mapIngredientToEntity(ingredientRequest, recipe);
                ingredient.setOrderPosition(i + 1);
                recipe.addIngredient(ingredient);
            }
        }
    }

    private Ingredient mapIngredientToEntity(IngredientRequest request, Recipe recipe) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setUnit(request.getUnit());
        ingredient.setNotes(request.getNotes());
        ingredient.setCategory(request.getCategory() != null ? request.getCategory() : IngredientCategory.OTHER);
        ingredient.setOptional(request.getIsOptional() != null ? request.getIsOptional() : false);
        ingredient.setCaloriesPer100g(request.getCaloriesPer100g());
        ingredient.setProteinPer100g(request.getProteinPer100g());
        ingredient.setCarbsPer100g(request.getCarbsPer100g());
        ingredient.setFatPer100g(request.getFatPer100g());
        ingredient.setFiberPer100g(request.getFiberPer100g());
        ingredient.setRecipe(recipe);
        return ingredient;
    }

    private RecipeResponse mapToResponse(Recipe recipe) {
        RecipeResponse response = new RecipeResponse();
        response.setId(recipe.getId());
        response.setTitle(recipe.getTitle());
        response.setDescription(recipe.getDescription());
        response.setInstructions(recipe.getInstructions());
        response.setCookingTimeMinutes(recipe.getCookingTimeMinutes());
        response.setPrepTimeMinutes(recipe.getPrepTimeMinutes());
        response.setTotalTimeMinutes(recipe.getTotalTimeMinutes());
        response.setServings(recipe.getServings());
        response.setCategory(recipe.getCategory());
        response.setDifficulty(recipe.getDifficulty());
        response.setImageUrl(recipe.getImageUrl());
        response.setPublic(recipe.isPublic());
        response.setAiGenerated(recipe.isAiGenerated());
        response.setAverageRating(recipe.getAverageRating());
        response.setRatingCount(recipe.getRatingCount());
        response.setSource(recipe.getSource());
        response.setCreatedAt(recipe.getCreatedAt());
        response.setUpdatedAt(recipe.getUpdatedAt());
        response.setTags(recipe.getTags());

        // Map owner
        if (recipe.getOwner() != null) {
            RecipeResponse.OwnerInfo ownerInfo = new RecipeResponse.OwnerInfo(
                    recipe.getOwner().getId(),
                    recipe.getOwner().getUsername(),
                    recipe.getOwner().getFullName());
            response.setOwner(ownerInfo);
        }

        // Map ingredients
        if (recipe.getIngredients() != null) {
            List<IngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                    .map(this::mapIngredientToResponse)
                    .collect(Collectors.toList());
            response.setIngredients(ingredientResponses);
        }

        return response;
    }

    private IngredientResponse mapIngredientToResponse(Ingredient ingredient) {
        IngredientResponse response = new IngredientResponse();
        response.setId(ingredient.getId());
        response.setName(ingredient.getName());
        response.setQuantity(ingredient.getQuantity());
        response.setUnit(ingredient.getUnit());
        response.setNotes(ingredient.getNotes());
        response.setCategory(ingredient.getCategory());
        response.setOptional(ingredient.isOptional());
        response.setOrderPosition(ingredient.getOrderPosition());
        response.setCaloriesPer100g(ingredient.getCaloriesPer100g());
        response.setProteinPer100g(ingredient.getProteinPer100g());
        response.setCarbsPer100g(ingredient.getCarbsPer100g());
        response.setFatPer100g(ingredient.getFatPer100g());
        response.setFiberPer100g(ingredient.getFiberPer100g());
        return response;
    }

    /**
     * DTO for recipe statistics.
     */
    public static class RecipeStatistics {
        private final long totalRecipes;
        private final long publicRecipes;
        private final long privateRecipes;

        public RecipeStatistics(long totalRecipes, long publicRecipes, long privateRecipes) {
            this.totalRecipes = totalRecipes;
            this.publicRecipes = publicRecipes;
            this.privateRecipes = privateRecipes;
        }

        public long getTotalRecipes() {
            return totalRecipes;
        }

        public long getPublicRecipes() {
            return publicRecipes;
        }

        public long getPrivateRecipes() {
            return privateRecipes;
        }
    }
}
