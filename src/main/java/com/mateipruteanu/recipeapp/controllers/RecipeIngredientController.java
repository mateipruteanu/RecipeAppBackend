package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.RecipeIngredient;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.repositories.IngredientRepository;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RecipeIngredientController {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @PutMapping("/recipes/{recipeId}/ingredients/{ingredientId}")
    public String addIngredientToRecipe(@PathVariable long recipeId, @PathVariable long ingredientId, @RequestBody String quantity) {
        if(recipeRepository.findById(recipeId).isPresent() && ingredientRepository.findById(ingredientId).isPresent()) {
            Recipe recipe = recipeRepository.findById(recipeId).get();
            recipe.getRecipeIngredients().add(new RecipeIngredient(recipe, ingredientRepository.findById(ingredientId).get(), quantity));
            recipeRepository.save(recipe);
            return "Added";
        }
        return "Recipe not found";
    }

    @DeleteMapping("/recipes/{recipeId}/ingredients/{ingredientId}")
    public String deleteIngredientFromRecipe(@PathVariable long recipeId, @PathVariable long ingredientId) {
        if(recipeRepository.findById(recipeId).isPresent() && ingredientRepository.findById(ingredientId).isPresent()) {
            Recipe recipe = recipeRepository.findById(recipeId).get();
            List<RecipeIngredient> recipeIngredients = recipe.getRecipeIngredients();
            for(RecipeIngredient recipeIngredient : recipeIngredients) {
                if(recipeIngredient.getIngredient().getId() == ingredientId) {
                    recipeIngredients.remove(recipeIngredient);
                    recipeRepository.save(recipe);
                    return "Deleted";
                }
            }
            return "Ingredient not found";
        }
        return "Recipe not found";
    }
}
