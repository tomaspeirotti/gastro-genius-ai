package com.gastrogeniusai.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastrogeniusai.domain.entity.Recipe;
import com.gastrogeniusai.presentation.dto.RecipeRequest;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for AI-powered features using Google Gemini.
 * Handles recipe generation, nutritional analysis, and wine pairing
 * suggestions.
 */
@Service
public class AiService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Autowired
    public AiService(ChatModel chatModel) {
        this.chatModel = chatModel;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates a recipe from a list of ingredients using AI.
     * 
     * @param ingredients list of ingredient names
     * @param cuisine     optional cuisine preference
     * @param difficulty  optional difficulty preference
     * @return AI-generated recipe as JSON string
     */
    public String generateRecipeFromIngredients(List<String> ingredients, String cuisine, String difficulty) {
        String ingredientsList = String.join(", ", ingredients);

        PromptTemplate promptTemplate = new PromptTemplate(
                """
                        You are a professional chef and recipe creator. Create a detailed recipe using the following ingredients: {ingredients}.

                        {cuisineInstruction}
                        {difficultyInstruction}

                        Please return the recipe in the following JSON format:
                        {
                            "title": "Recipe Name",
                            "description": "Brief description of the dish",
                            "instructions": "Step-by-step cooking instructions",
                            "cookingTimeMinutes": 30,
                            "prepTimeMinutes": 15,
                            "servings": 4,
                            "category": "MAIN_COURSE",
                            "difficulty": "MEDIUM",
                            "ingredients": [
                                {
                                    "name": "ingredient name",
                                    "quantity": 1.5,
                                    "unit": "CUP",
                                    "category": "VEGETABLES",
                                    "isOptional": false
                                }
                            ],
                            "tags": ["tag1", "tag2"]
                        }

                        Important guidelines:
                        - Use only the provided ingredients as main ingredients, you can add common seasonings and basic ingredients
                        - Make the recipe realistic and achievable
                        - Provide detailed, step-by-step instructions
                        - Use appropriate measurement units (CUP, TABLESPOON, TEASPOON, GRAM, etc.)
                        - Choose appropriate category (APPETIZER, MAIN_COURSE, SIDE_DISH, DESSERT, etc.)
                        - Choose appropriate difficulty (BEGINNER, EASY, MEDIUM, HARD, EXPERT)
                        - Add relevant tags
                        - Ensure the JSON is valid and properly formatted
                        """);

        Map<String, Object> promptVariables = new HashMap<>();
        promptVariables.put("ingredients", ingredientsList);

        if (cuisine != null && !cuisine.trim().isEmpty()) {
            promptVariables.put("cuisineInstruction", "Style: Create this as a " + cuisine + " cuisine dish.");
        } else {
            promptVariables.put("cuisineInstruction", "");
        }

        if (difficulty != null && !difficulty.trim().isEmpty()) {
            promptVariables.put("difficultyInstruction", "Difficulty: Make this recipe " + difficulty + " level.");
        } else {
            promptVariables.put("difficultyInstruction", "");
        }

        Prompt prompt = promptTemplate.create(promptVariables);
        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getContent();
    }

    /**
     * Analyzes the nutritional content of a recipe using AI.
     * 
     * @param recipe the recipe to analyze
     * @return nutritional analysis as JSON string
     */
    public String analyzeNutrition(Recipe recipe) {
        StringBuilder ingredientsText = new StringBuilder();
        recipe.getIngredients().forEach(ingredient -> {
            ingredientsText.append(String.format("- %s %s %s\n",
                    ingredient.getQuantity(),
                    ingredient.getUnit().getDisplayName(ingredient.getQuantity().doubleValue() != 1.0),
                    ingredient.getName()));
        });

        PromptTemplate promptTemplate = new PromptTemplate(
                """
                        You are a professional nutritionist. Analyze the nutritional content of this recipe:

                        Recipe: {title}
                        Servings: {servings}
                        Ingredients:
                        {ingredients}

                        Please provide a detailed nutritional analysis in the following JSON format:
                        {
                            "perServing": {
                                "calories": 450,
                                "protein": 25.5,
                                "carbohydrates": 35.2,
                                "fat": 18.7,
                                "fiber": 8.3,
                                "sugar": 12.1,
                                "sodium": 890
                            },
                            "perRecipe": {
                                "calories": 1800,
                                "protein": 102.0,
                                "carbohydrates": 140.8,
                                "fat": 74.8,
                                "fiber": 33.2,
                                "sugar": 48.4,
                                "sodium": 3560
                            },
                            "macronutrientRatios": {
                                "proteinPercentage": 23,
                                "carbohydratePercentage": 31,
                                "fatPercentage": 37
                            },
                            "healthScore": 8.5,
                            "dietaryTags": ["high-protein", "low-carb"],
                            "allergens": ["dairy", "nuts"],
                            "vitaminsAndMinerals": [
                                {"name": "Vitamin C", "amount": "45mg", "dailyValue": "50%"},
                                {"name": "Iron", "amount": "8mg", "dailyValue": "44%"}
                            ],
                            "nutritionNotes": "This recipe is rich in protein and provides essential amino acids. High in fiber which aids digestion."
                        }

                        Guidelines:
                        - Provide realistic nutritional estimates based on the ingredients and quantities
                        - All nutritional values should be in grams unless otherwise specified
                        - Health score should be 1-10 (10 being the healthiest)
                        - Include relevant dietary tags and allergen warnings
                        - Provide helpful nutritional notes
                        - Ensure JSON is valid and properly formatted
                        """);

        Map<String, Object> promptVariables = new HashMap<>();
        promptVariables.put("title", recipe.getTitle());
        promptVariables.put("servings", recipe.getServings());
        promptVariables.put("ingredients", ingredientsText.toString());

        Prompt prompt = promptTemplate.create(promptVariables);
        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getContent();
    }

    /**
     * Provides wine pairing suggestions for a recipe using AI sommelier expertise.
     * 
     * @param recipe the recipe to pair
     * @return wine pairing suggestions as JSON string
     */
    public String suggestWinePairing(Recipe recipe) {
        StringBuilder ingredientsText = new StringBuilder();
        recipe.getIngredients().forEach(ingredient -> {
            ingredientsText.append(ingredient.getName()).append(", ");
        });
        // Remove trailing comma and space
        if (ingredientsText.length() > 0) {
            ingredientsText.setLength(ingredientsText.length() - 2);
        }

        PromptTemplate promptTemplate = new PromptTemplate(
                """
                        You are a professional sommelier with extensive knowledge of wine pairings. Recommend the best wine pairings for this recipe:

                        Recipe: {title}
                        Description: {description}
                        Category: {category}
                        Main Ingredients: {ingredients}
                        Cooking Method: Based on the instructions, this appears to be {cookingStyle}

                        Please provide wine pairing suggestions in the following JSON format:
                        {
                            "primaryRecommendation": {
                                "wineType": "Pinot Noir",
                                "specificWines": ["Willamette Valley Pinot Noir", "Burgundy Gevrey-Chambertin"],
                                "reasoning": "The earthy flavors and medium body complement the dish perfectly",
                                "servingTemperature": "60-65°F",
                                "priceRange": "$25-50"
                            },
                            "alternativeRecommendations": [
                                {
                                    "wineType": "Chardonnay",
                                    "specificWines": ["Chablis", "Burgundy Meursault"],
                                    "reasoning": "A crisp white wine that won't overpower the delicate flavors",
                                    "servingTemperature": "45-50°F",
                                    "priceRange": "$20-40"
                                },
                                {
                                    "wineType": "Rosé",
                                    "specificWines": ["Provence Rosé", "Sancerre Rosé"],
                                    "reasoning": "Versatile option that bridges red and white characteristics",
                                    "servingTemperature": "45-50°F",
                                    "priceRange": "$15-30"
                                }
                            ],
                            "nonAlcoholicOptions": [
                                {
                                    "beverage": "Sparkling Apple Cider",
                                    "reasoning": "Crisp acidity and fruit notes complement the dish"
                                },
                                {
                                    "beverage": "Hibiscus Tea",
                                    "reasoning": "Floral notes and slight tartness provide nice contrast"
                                }
                            ],
                            "pairingPrinciples": [
                                "Match the weight of the wine to the weight of the dish",
                                "Consider the dominant flavors and cooking methods",
                                "Look for complementary or contrasting elements"
                            ],
                            "servingSuggestions": "Serve wine 30 minutes before the meal to allow proper temperature. Decant red wines if they're young and tannic."
                        }

                        Guidelines:
                        - Consider the main flavors, textures, and cooking methods
                        - Provide specific wine recommendations with regions when possible
                        - Include both premium and accessible price options
                        - Explain the reasoning behind each recommendation
                        - Include non-alcoholic alternatives
                        - Provide practical serving suggestions
                        - Ensure JSON is valid and properly formatted
                        """);

        Map<String, Object> promptVariables = new HashMap<>();
        promptVariables.put("title", recipe.getTitle());
        promptVariables.put("description",
                recipe.getDescription() != null ? recipe.getDescription() : "No description provided");
        promptVariables.put("category", recipe.getCategory().getDisplayName());
        promptVariables.put("ingredients", ingredientsText.toString());

        // Analyze cooking style from instructions
        String cookingStyle = analyzeInstructionsForCookingStyle(recipe.getInstructions());
        promptVariables.put("cookingStyle", cookingStyle);

        Prompt prompt = promptTemplate.create(promptVariables);
        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getContent();
    }

    /**
     * Validates and cleans AI-generated JSON responses.
     * 
     * @param aiResponse the raw AI response
     * @return cleaned JSON string
     */
    public String validateAndCleanJsonResponse(String aiResponse) {
        try {
            // Try to extract JSON from the response (in case AI adds extra text)
            String jsonStart = aiResponse.indexOf('{') >= 0 ? aiResponse.substring(aiResponse.indexOf('{'))
                    : aiResponse;
            String jsonEnd = jsonStart.lastIndexOf('}') >= 0 ? jsonStart.substring(0, jsonStart.lastIndexOf('}') + 1)
                    : jsonStart;

            // Validate JSON by parsing it
            objectMapper.readTree(jsonEnd);
            return jsonEnd;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("AI generated invalid JSON response: " + e.getMessage(), e);
        }
    }

    /**
     * Parses AI-generated recipe JSON into a RecipeRequest object.
     * 
     * @param aiGeneratedJson the AI-generated recipe JSON
     * @return RecipeRequest object
     */
    public RecipeRequest parseAiGeneratedRecipe(String aiGeneratedJson) {
        try {
            return objectMapper.readValue(aiGeneratedJson, RecipeRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse AI-generated recipe: " + e.getMessage(), e);
        }
    }

    // Private helper methods

    private String analyzeInstructionsForCookingStyle(String instructions) {
        if (instructions == null)
            return "unknown cooking method";

        String lowerInstructions = instructions.toLowerCase();

        if (lowerInstructions.contains("grill") || lowerInstructions.contains("barbecue")) {
            return "grilled";
        } else if (lowerInstructions.contains("roast") || lowerInstructions.contains("bake")) {
            return "roasted/baked";
        } else if (lowerInstructions.contains("fry") || lowerInstructions.contains("sauté")) {
            return "pan-fried/sautéed";
        } else if (lowerInstructions.contains("steam")) {
            return "steamed";
        } else if (lowerInstructions.contains("boil") || lowerInstructions.contains("simmer")) {
            return "boiled/simmered";
        } else if (lowerInstructions.contains("braise")) {
            return "braised";
        } else {
            return "mixed cooking methods";
        }
    }
}
