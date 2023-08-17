package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.Ingredient;
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
public class RecipeController {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @GetMapping("/recipes/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        if(recipeRepository.findById(id).isPresent()) {
            return ResponseEntity.ok(recipeRepository.findById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/recipes")
    public ResponseEntity<String> addRecipe(@RequestBody Recipe recipe) {
        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            Ingredient ingredient = recipeIngredient.getIngredient();
            if(ingredientRepository.findByName(ingredient.getName()) != null) {
                ingredient = ingredientRepository.findByName(ingredient.getName());
                recipeIngredient.setIngredient(ingredient);
            }
            else {
                Ingredient savedIngredient = ingredientRepository.save(ingredient);
                recipeIngredient.setIngredient(savedIngredient);
            }
            recipeIngredient.setRecipe(recipe);
        }

        recipeRepository.save(recipe);
        return ResponseEntity.ok("Saved recipe " + recipe.getName());
    }



    @PutMapping("/recipes/{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable long id, @RequestBody Recipe recipe) {
        if(recipeRepository.findById(id).isPresent()) {
            Recipe existingRecipe = recipeRepository.findById(id).get();
            existingRecipe.setName(recipe.getName());
            existingRecipe.setDescription(recipe.getDescription());
            existingRecipe.setInstructions(recipe.getInstructions());
            recipeRepository.save(existingRecipe);
            return ResponseEntity.ok("Updated");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable long id) {
        if(recipeRepository.findById(id).isPresent()) {
            Recipe recipe = recipeRepository.findById(id).get();
            List<User> users = userRepository.findAll();
            for(User user : users) {
                if(user.getAddedRecipes().contains(recipe)) {
                    user.getAddedRecipes().remove(recipe);
                    userRepository.save(user);
                }
            }
            recipeRepository.deleteById(id);
            return ResponseEntity.ok("Deleted");
        }
        return ResponseEntity.notFound().build();
    }

}
