package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RecipeController {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @GetMapping("/recipes/{id}")
    public Recipe getRecipe(@PathVariable long id) {
        if(recipeRepository.findById(id).isPresent()) {
            return recipeRepository.findById(id).get();
        }
        return new Recipe();
    }

    @PostMapping("/recipes")
    public String addRecipe(@RequestBody Recipe recipe) {
        recipeRepository.save(recipe);
        return "Saved";
    }

    @PutMapping("/recipes/{id}")
    public String updateRecipe(@PathVariable long id, @RequestBody Recipe recipe) {
        if(recipeRepository.findById(id).isPresent()) {
            Recipe existingRecipe = recipeRepository.findById(id).get();
            existingRecipe.setName(recipe.getName());
            existingRecipe.setDescription(recipe.getDescription());
            existingRecipe.setInstructions(recipe.getInstructions());
            recipeRepository.save(existingRecipe);
            return "Updated";
        }
        return "Recipe not found";
    }

    @DeleteMapping("/recipes/{id}")
    public String deleteRecipe(@PathVariable long id) {
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
            return "Deleted";
        }
        return "Recipe not found";
    }
}
