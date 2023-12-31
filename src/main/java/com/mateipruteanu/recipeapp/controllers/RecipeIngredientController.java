package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.RecipeIngredient;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.repositories.IngredientRepository;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeIngredientController {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @PutMapping("/recipes/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<String> addIngredientToRecipe(@PathVariable long recipeId, @PathVariable long ingredientId, @RequestBody String quantity) {
        if(recipeRepository.findById(recipeId).isPresent() && ingredientRepository.findById(ingredientId).isPresent()) {
            Recipe recipe = recipeRepository.findById(recipeId).get();
            recipe.getRecipeIngredients().add(new RecipeIngredient(recipe, ingredientRepository.findById(ingredientId).get(), quantity));
            recipeRepository.save(recipe);
            return ResponseEntity.ok("Added ingredient");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/recipes/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<String> deleteIngredientFromRecipe(@PathVariable long recipeId, @PathVariable long ingredientId) {
        if(recipeRepository.findById(recipeId).isPresent() && ingredientRepository.findById(ingredientId).isPresent()) {
            Recipe recipe = recipeRepository.findById(recipeId).get();
            List<RecipeIngredient> recipeIngredients = recipe.getRecipeIngredients();
            for(RecipeIngredient recipeIngredient : recipeIngredients) {
                if(recipeIngredient.getIngredient().getId() == ingredientId) {
                    recipeIngredients.remove(recipeIngredient);
                    recipeRepository.save(recipe);
                    return ResponseEntity.ok("Deleted ingredient");
                }
            }
            return ResponseEntity.status(404).body("Ingredient not found");
        }
        return ResponseEntity.status(404).body("Recipe or ingredient not found");
    }
}
