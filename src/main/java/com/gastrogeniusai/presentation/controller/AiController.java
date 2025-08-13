package com.gastrogeniusai.presentation.controller;

import com.gastrogeniusai.application.service.AiService;
import com.gastrogeniusai.application.service.RecipeService;
import com.gastrogeniusai.domain.entity.Recipe;
import com.gastrogeniusai.infrastructure.repository.RecipeRepository;
import com.gastrogeniusai.infrastructure.repository.UserRepository;
import com.gastrogeniusai.presentation.dto.GenerateRecipeRequest;
import com.gastrogeniusai.presentation.dto.RecipeRequest;
import com.gastrogeniusai.presentation.dto.RecipeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for AI-powered features.
 * Handles recipe generation, nutritional analysis, and wine pairing
 * suggestions.
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "AI Features", description = "AI-powered recipe generation, nutrition analysis, and wine pairing endpoints")
public class AiController {

    private final AiService aiService;
    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public AiController(AiService aiService,
            RecipeService recipeService,
            RecipeRepository recipeRepository,
            UserRepository userRepository) {
        this.aiService = aiService;
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Generates a recipe from ingredients using AI.
     */
    @Operation(summary = "Generate recipe from ingredients", description = "Uses AI to generate a complete recipe from a list of ingredients with optional cuisine and difficulty preferences", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "201", description = "Recipe generated successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "AI generation failed", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/generate-recipe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateRecipe(
            @Parameter(description = "Recipe generation request with ingredients and preferences", required = true) @Valid @RequestBody GenerateRecipeRequest request,
            Authentication authentication) {

        try {
            // Generate recipe using AI
            String aiResponse = aiService.generateRecipeFromIngredients(
                    request.getIngredients(),
                    request.getCuisine(),
                    request.getDifficulty());

            // Validate and clean the AI response
            String cleanedJson = aiService.validateAndCleanJsonResponse(aiResponse);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("aiGeneratedJson", cleanedJson);

            // If user wants to save the recipe, parse and save it
            if (request.getSaveRecipe() != null && request.getSaveRecipe()) {
                try {
                    RecipeRequest recipeRequest = aiService.parseAiGeneratedRecipe(cleanedJson);
                    RecipeResponse savedRecipe = recipeService.createRecipe(recipeRequest, authentication.getName());

                    // Mark as AI-generated
                    Recipe recipe = recipeRepository.findById(savedRecipe.getId()).orElse(null);
                    if (recipe != null) {
                        recipe.setAiGenerated(true);
                        recipeRepository.save(recipe);
                        savedRecipe.setAiGenerated(true);
                    }

                    response.put("savedRecipe", savedRecipe);
                    response.put("message", "Recipe generated and saved successfully");
                } catch (Exception saveException) {
                    response.put("message", "Recipe generated but could not be saved: " + saveException.getMessage());
                    response.put("saveError", saveException.getMessage());
                }
            } else {
                response.put("message", "Recipe generated successfully (not saved)");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "AI generation failed");
            errorResponse.put("message", "The AI service returned an invalid response. Please try again.");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe generation failed");
            errorResponse.put("message", "An unexpected error occurred during recipe generation");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Analyzes the nutritional content of a recipe using AI.
     */
    @Operation(summary = "Analyze recipe nutrition", description = "Uses AI to analyze the nutritional content of a recipe and provide detailed nutritional information", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Nutritional analysis completed", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Access denied to recipe", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Analysis failed", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/recipes/{id}/nutrition")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> analyzeRecipeNutrition(
            @Parameter(description = "Recipe ID", required = true) @PathVariable Long id,
            Authentication authentication) {

        try {
            // Get the recipe and verify access
            RecipeResponse recipeResponse = recipeService.getRecipeById(id, authentication.getName());
            Recipe recipe = recipeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            // Generate nutritional analysis using AI
            String aiResponse = aiService.analyzeNutrition(recipe);
            String cleanedJson = aiService.validateAndCleanJsonResponse(aiResponse);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("recipeId", id);
            response.put("recipeTitle", recipe.getTitle());
            response.put("nutritionalAnalysis", cleanedJson);
            response.put("message", "Nutritional analysis completed successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe not found");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Nutritional analysis failed");
            errorResponse.put("message", "An unexpected error occurred during nutritional analysis");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Provides wine pairing suggestions for a recipe using AI sommelier expertise.
     */
    @Operation(summary = "Get wine pairing suggestions", description = "Uses AI sommelier expertise to suggest wine pairings for a recipe", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Wine pairing suggestions generated", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Access denied to recipe", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Wine pairing analysis failed", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/recipes/{id}/pairing-suggestion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getWinePairingSuggestion(
            @Parameter(description = "Recipe ID", required = true) @PathVariable Long id,
            Authentication authentication) {

        try {
            // Get the recipe and verify access
            RecipeResponse recipeResponse = recipeService.getRecipeById(id, authentication.getName());
            Recipe recipe = recipeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            // Generate wine pairing suggestions using AI
            String aiResponse = aiService.suggestWinePairing(recipe);
            String cleanedJson = aiService.validateAndCleanJsonResponse(aiResponse);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("recipeId", id);
            response.put("recipeTitle", recipe.getTitle());
            response.put("pairingSuggestions", cleanedJson);
            response.put("message", "Wine pairing suggestions generated successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Recipe not found");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Wine pairing analysis failed");
            errorResponse.put("message", "An unexpected error occurred during wine pairing analysis");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Gets AI service health and capabilities.
     */
    @Operation(summary = "AI service health check", description = "Returns the health status and capabilities of the AI service", responses = {
            @ApiResponse(responseCode = "200", description = "AI service status retrieved", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getAiServiceHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "AI Features");
        response.put("capabilities", Map.of(
                "recipeGeneration", true,
                "nutritionalAnalysis", true,
                "winePairing", true));
        response.put("aiProvider", "Google Gemini");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Gets available AI features and their descriptions.
     */
    @Operation(summary = "Get AI features information", description = "Returns information about available AI features and how to use them", responses = {
            @ApiResponse(responseCode = "200", description = "AI features information retrieved", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getAiFeatures() {
        Map<String, Object> response = new HashMap<>();

        response.put("features", Map.of(
                "recipeGeneration", Map.of(
                        "endpoint", "/ai/generate-recipe",
                        "method", "POST",
                        "description",
                        "Generate recipes from ingredients with optional cuisine and difficulty preferences",
                        "requiresAuth", true),
                "nutritionalAnalysis", Map.of(
                        "endpoint", "/ai/recipes/{id}/nutrition",
                        "method", "GET",
                        "description", "Analyze the nutritional content of any recipe",
                        "requiresAuth", true),
                "winePairing", Map.of(
                        "endpoint", "/ai/recipes/{id}/pairing-suggestion",
                        "method", "GET",
                        "description", "Get expert wine pairing suggestions for any recipe",
                        "requiresAuth", true)));

        response.put("aiProvider", "Google Gemini");
        response.put("version", "1.0");

        return ResponseEntity.ok(response);
    }
}
