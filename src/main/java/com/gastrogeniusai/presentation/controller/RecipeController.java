package com.gastrogeniusai.presentation.controller;

import com.gastrogeniusai.application.service.RecipeService;
import com.gastrogeniusai.domain.entity.RecipeCategory;
import com.gastrogeniusai.domain.entity.RecipeDifficulty;
import com.gastrogeniusai.presentation.dto.RecipeRequest;
import com.gastrogeniusai.presentation.dto.RecipeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for recipe operations.
 * Handles recipe CRUD operations, search, and filtering.
 */
@RestController
@RequestMapping("/recipes")
@Tag(name = "Recipes", description = "Recipe management and search endpoints")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Creates a new recipe.
     */
    @Operation(summary = "Create a new recipe", description = "Creates a new recipe for the authenticated user", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "201", description = "Recipe created successfully", content = @Content(schema = @Schema(implementation = RecipeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid recipe data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRecipe(
            @Parameter(description = "Recipe data", required = true) @Valid @RequestBody RecipeRequest recipeRequest,
            Authentication authentication) {

        try {
            RecipeResponse response = recipeService.createRecipe(recipeRequest, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe creation failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Gets a recipe by ID.
     */
    @Operation(summary = "Get recipe by ID", description = "Retrieves a recipe by its ID. User can access their own recipes or public recipes.", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Recipe found", content = @Content(schema = @Schema(implementation = RecipeResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRecipeById(
            @Parameter(description = "Recipe ID", required = true) @PathVariable Long id,
            Authentication authentication) {

        try {
            RecipeResponse response = recipeService.getRecipeById(id, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe access failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    /**
     * Gets a public recipe by ID (no authentication required).
     */
    @Operation(summary = "Get public recipe by ID", description = "Retrieves a public recipe by its ID. No authentication required.", responses = {
            @ApiResponse(responseCode = "200", description = "Public recipe found", content = @Content(schema = @Schema(implementation = RecipeResponse.class))),
            @ApiResponse(responseCode = "403", description = "Recipe is not public", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicRecipeById(
            @Parameter(description = "Recipe ID", required = true) @PathVariable Long id) {

        try {
            RecipeResponse response = recipeService.getPublicRecipeById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe access failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    /**
     * Updates a recipe.
     */
    @Operation(summary = "Update recipe", description = "Updates an existing recipe. User can only update their own recipes.", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully", content = @Content(schema = @Schema(implementation = RecipeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid recipe data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateRecipe(
            @Parameter(description = "Recipe ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated recipe data", required = true) @Valid @RequestBody RecipeRequest recipeRequest,
            Authentication authentication) {

        try {
            RecipeResponse response = recipeService.updateRecipe(id, recipeRequest, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe update failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Deletes a recipe.
     */
    @Operation(summary = "Delete recipe", description = "Deletes a recipe. User can only delete their own recipes.", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "204", description = "Recipe deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteRecipe(
            @Parameter(description = "Recipe ID", required = true) @PathVariable Long id,
            Authentication authentication) {

        try {
            recipeService.deleteRecipe(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe deletion failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    /**
     * Gets all recipes for the current user.
     */
    @Operation(summary = "Get user's recipes", description = "Retrieves all recipes owned by the authenticated user", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "User's recipes retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<RecipeResponse>> getUserRecipes(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponse> recipes = recipeService.getUserRecipes(authentication.getName(), pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Gets all public recipes.
     */
    @Operation(summary = "Get public recipes", description = "Retrieves all public recipes. No authentication required.", responses = {
            @ApiResponse(responseCode = "200", description = "Public recipes retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/public")
    public ResponseEntity<Page<RecipeResponse>> getPublicRecipes(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponse> recipes = recipeService.getPublicRecipes(pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Searches recipes with filters.
     */
    @Operation(summary = "Search recipes", description = "Searches recipes with various filters", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<RecipeResponse>> searchRecipes(
            @Parameter(description = "Search term for title/description") @RequestParam(required = false) String q,
            @Parameter(description = "Recipe category filter") @RequestParam(required = false) RecipeCategory category,
            @Parameter(description = "Recipe difficulty filter") @RequestParam(required = false) RecipeDifficulty difficulty,
            @Parameter(description = "Minimum cooking time in minutes") @RequestParam(required = false) Integer minCookingTime,
            @Parameter(description = "Maximum cooking time in minutes") @RequestParam(required = false) Integer maxCookingTime,
            @Parameter(description = "Search only public recipes") @RequestParam(defaultValue = "false") boolean publicOnly,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponse> recipes = recipeService.searchRecipes(
                q, category, difficulty, minCookingTime, maxCookingTime,
                publicOnly, authentication.getName(), pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Searches public recipes (no authentication required).
     */
    @Operation(summary = "Search public recipes", description = "Searches public recipes with filters. No authentication required.", responses = {
            @ApiResponse(responseCode = "200", description = "Public search results retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search/public")
    public ResponseEntity<Page<RecipeResponse>> searchPublicRecipes(
            @Parameter(description = "Search term for title/description") @RequestParam(required = false) String q,
            @Parameter(description = "Recipe category filter") @RequestParam(required = false) RecipeCategory category,
            @Parameter(description = "Recipe difficulty filter") @RequestParam(required = false) RecipeDifficulty difficulty,
            @Parameter(description = "Minimum cooking time in minutes") @RequestParam(required = false) Integer minCookingTime,
            @Parameter(description = "Maximum cooking time in minutes") @RequestParam(required = false) Integer maxCookingTime,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponse> recipes = recipeService.searchRecipes(
                q, category, difficulty, minCookingTime, maxCookingTime,
                true, null, pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Searches recipes by ingredients.
     */
    @Operation(summary = "Search recipes by ingredients", description = "Searches recipes that contain any of the specified ingredients", responses = {
            @ApiResponse(responseCode = "200", description = "Ingredient search results retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search/by-ingredients")
    public ResponseEntity<Page<RecipeResponse>> searchRecipesByIngredients(
            @Parameter(description = "Comma-separated list of ingredient names") @RequestParam List<String> ingredients,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponse> recipes = recipeService.searchRecipesByIngredients(ingredients, pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Gets recipes by category.
     */
    @Operation(summary = "Get recipes by category", description = "Retrieves recipes filtered by category", responses = {
            @ApiResponse(responseCode = "200", description = "Category recipes retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<RecipeResponse>> getRecipesByCategory(
            @Parameter(description = "Recipe category") @PathVariable RecipeCategory category,
            @Parameter(description = "Include only public recipes") @RequestParam(defaultValue = "true") boolean publicOnly,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RecipeResponse> recipes = recipeService.getRecipesByCategory(category, publicOnly, pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Gets top-rated recipes.
     */
    @Operation(summary = "Get top-rated recipes", description = "Retrieves recipes with high average ratings", responses = {
            @ApiResponse(responseCode = "200", description = "Top-rated recipes retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/top-rated")
    public ResponseEntity<Page<RecipeResponse>> getTopRatedRecipes(
            @Parameter(description = "Minimum average rating") @RequestParam(defaultValue = "4.0") Double minRating,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponse> recipes = recipeService.getTopRatedRecipes(minRating, pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Gets most popular recipes.
     */
    @Operation(summary = "Get most popular recipes", description = "Retrieves recipes ordered by popularity (rating count)", responses = {
            @ApiResponse(responseCode = "200", description = "Popular recipes retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/popular")
    public ResponseEntity<Page<RecipeResponse>> getMostPopularRecipes(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponse> recipes = recipeService.getMostPopularRecipes(pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Gets recently created recipes.
     */
    @Operation(summary = "Get recent recipes", description = "Retrieves recently created recipes", responses = {
            @ApiResponse(responseCode = "200", description = "Recent recipes retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/recent")
    public ResponseEntity<Page<RecipeResponse>> getRecentRecipes(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponse> recipes = recipeService.getRecentRecipes(pageable);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Toggles recipe visibility (public/private).
     */
    @Operation(summary = "Toggle recipe visibility", description = "Toggles the public/private status of a recipe", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Recipe visibility toggled", content = @Content(schema = @Schema(implementation = RecipeResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{id}/toggle-visibility")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleRecipeVisibility(
            @Parameter(description = "Recipe ID", required = true) @PathVariable Long id,
            Authentication authentication) {

        try {
            RecipeResponse response = recipeService.toggleRecipeVisibility(id, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Visibility toggle failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    /**
     * Gets user recipe statistics.
     */
    @Operation(summary = "Get user recipe statistics", description = "Retrieves statistics about the user's recipes", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Recipe statistics retrieved", content = @Content(schema = @Schema(implementation = RecipeService.RecipeStatistics.class)))
    })
    @GetMapping("/my/statistics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipeService.RecipeStatistics> getUserRecipeStatistics(
            Authentication authentication) {

        RecipeService.RecipeStatistics statistics = recipeService.getUserRecipeStatistics(authentication.getName());
        return ResponseEntity.ok(statistics);
    }
}
