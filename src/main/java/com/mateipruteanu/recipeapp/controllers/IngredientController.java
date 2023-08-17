package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.RecipeIngredient;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.models.Ingredient;
import com.mateipruteanu.recipeapp.repositories.IngredientRepository;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IngredientController {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientRepository.findAll());
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable long id) {
        if(ingredientRepository.findById(id).isPresent()) {
            return ResponseEntity.ok(ingredientRepository.findById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/ingredients")
    public ResponseEntity<String> addIngredient(@RequestBody Ingredient ingredient) {
        ingredientRepository.save(ingredient);
        return ResponseEntity.ok("Saved ingredient " + ingredient.getName());
    }

    @PutMapping("/ingredients/{id}")
    public ResponseEntity<String> updateIngredient(@PathVariable long id, @RequestBody Ingredient ingredient) {
        if(ingredientRepository.findById(id).isPresent()) {
            Ingredient existingIngredient = ingredientRepository.findById(id).get();
            existingIngredient.setName(ingredient.getName());
            ingredientRepository.save(existingIngredient);
            return ResponseEntity.ok("Updated ingredient " + existingIngredient.getName());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/ingredients/{id}")
    public ResponseEntity<String> deleteIngredient(@PathVariable long id) {
        if(ingredientRepository.findById(id).isPresent()) {
            ingredientRepository.deleteById(id);
            return ResponseEntity.ok("Deleted ingredient");
        }
        return ResponseEntity.notFound().build();
    }

}
