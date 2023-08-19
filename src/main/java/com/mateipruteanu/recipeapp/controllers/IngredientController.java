package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.Ingredient;
import com.mateipruteanu.recipeapp.repositories.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {
    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable long id) {
        if(ingredientRepository.findById(id).isPresent()) {
            return ResponseEntity.ok(ingredientRepository.findById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<String> addIngredient(@RequestBody Ingredient ingredient) {
        ingredientRepository.save(ingredient);
        return ResponseEntity.ok("Saved ingredient " + ingredient.getName());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateIngredient(@PathVariable long id, @RequestBody Ingredient ingredient) {
        if(ingredientRepository.findById(id).isPresent()) {
            Ingredient existingIngredient = ingredientRepository.findById(id).get();
            existingIngredient.setName(ingredient.getName());
            ingredientRepository.save(existingIngredient);
            return ResponseEntity.ok("Updated ingredient " + existingIngredient.getName());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIngredient(@PathVariable long id) {
        if(ingredientRepository.findById(id).isPresent()) {
            ingredientRepository.deleteById(id);
            return ResponseEntity.ok("Deleted ingredient");
        }
        return ResponseEntity.notFound().build();
    }

}
