package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.RecipeIngredient;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.models.Ingredient;
import com.mateipruteanu.recipeapp.repositories.IngredientRepository;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @GetMapping("/ingredients/{id}")
    public Ingredient getIngredient(@PathVariable long id) {
        if(ingredientRepository.findById(id).isPresent()) {
            return ingredientRepository.findById(id).get();
        }
        return new Ingredient();
    }

    @PostMapping("/ingredients")
    public String addIngredient(@RequestBody Ingredient ingredient) {
        ingredientRepository.save(ingredient);
        return "Saved";
    }

    @PutMapping("/ingredients/{id}")
    public String updateIngredient(@PathVariable long id, @RequestBody Ingredient ingredient) {
        if(ingredientRepository.findById(id).isPresent()) {
            Ingredient existingIngredient = ingredientRepository.findById(id).get();
            existingIngredient.setName(ingredient.getName());
            ingredientRepository.save(existingIngredient);
            return "Updated";
        }
        return "Ingredient not found";
    }

    @DeleteMapping("/ingredients/{id}")
    public String deleteIngredient(@PathVariable long id) {
        if(ingredientRepository.findById(id).isPresent()) {
            ingredientRepository.deleteById(id);
            return "Deleted";
        }
        return "Ingredient not found";
    }

}
