package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.Ingredient;
import com.mateipruteanu.recipeapp.models.RecipeIngredient;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import com.mateipruteanu.recipeapp.repositories.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }


    // CRUD operations for users
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        if(userRepository.findById(id).isPresent()) {
            return ResponseEntity.ok(userRepository.findById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/users")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        if(userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(409).body("Username already exists");
        }
        userRepository.save(user);
        return ResponseEntity.ok("Saved");
    }

    @PutMapping("users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable long id, @RequestBody User user) {
        if(userRepository.findById(id).isPresent()) {
            User existingUser = userRepository.findById(id).get();
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(user.getPassword());
            userRepository.save(existingUser);
            return ResponseEntity.ok("Updated");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("Deleted user with id " + id);
        }
        return ResponseEntity.notFound().build();
    }


    //User added recipes list
    @GetMapping("/users/{userId}/recipes")
    public ResponseEntity<List<Recipe>> getUserAddedRecipes(@PathVariable long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(user.getAddedRecipes());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userId}/recipes/{recipeId}")
    public ResponseEntity<Recipe> getUserAddedRecipe(@PathVariable long userId, @PathVariable long recipeId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Recipe> addedRecipes = user.getAddedRecipes();

            Optional<Recipe> optionalRecipe = addedRecipes.stream()
                    .filter(recipe -> recipe.getId() == recipeId)
                    .findFirst();

            if (optionalRecipe.isPresent()) {
                Recipe recipe = optionalRecipe.get();
                return ResponseEntity.ok(recipe);
            }
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/users/{userId}/recipes")
    public ResponseEntity<String> addRecipeToUser(@PathVariable long userId, @RequestBody Recipe recipe) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
                Ingredient ingredient = recipeIngredient.getIngredient();
                if(ingredientRepository.findByName(ingredient.getName()) != null) {
                    ingredient = ingredientRepository.findByName(ingredient.getName());
                }
                else {
                    ingredient = ingredientRepository.save(ingredient);
                }
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setRecipe(recipe);
            }

            recipeRepository.save(recipe);
            user.getAddedRecipes().add(recipe);
            userRepository.save(user);
            return ResponseEntity.ok("Recipe added to users list");
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/users/{userId}/recipes/{recipeId}")
    public ResponseEntity<String> removeRecipeFromUser(@PathVariable long userId, @PathVariable long recipeId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Recipe> addedRecipes = user.getAddedRecipes();
            addedRecipes.removeIf(recipe -> recipe.getId() == recipeId);
            userRepository.save(user);
            return ResponseEntity.ok("Recipe removed from users list");
        }
        return ResponseEntity.notFound().build();
    }


    //Favorites List
    @GetMapping("/users/{userId}/favorites")
    public ResponseEntity<List<Recipe>> getUserFavoriteRecipes(@PathVariable long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(user.getFavoriteRecipes());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userId}/favorites/{recipeId}")
    public ResponseEntity<Recipe> getUserFavoriteRecipe(@PathVariable long userId, @PathVariable long recipeId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Recipe> favoriteRecipes = user.getFavoriteRecipes();

            Optional<Recipe> optionalRecipe;
            for(Recipe recipe : favoriteRecipes) {
                if(recipe.getId() == recipeId) {
                    optionalRecipe = Optional.of(recipe);
                    Recipe recipe1 = optionalRecipe.get();
                    return ResponseEntity.ok(recipe1);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/users/{userId}/favorites")
    public ResponseEntity<String> addRecipeToFavorites(@PathVariable long userId, @RequestBody Recipe recipe) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Recipe> optionalRecipe = recipeRepository.findById(recipe.getId());
            if(optionalRecipe.isPresent()) {
                Recipe recipe1 = optionalRecipe.get();
                user.getFavoriteRecipes().add(recipe1);
                userRepository.save(user);
                return ResponseEntity.ok("Recipe added to favorites list for user " + user.getUsername());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{userId}/favorites/{recipeId}")
    public ResponseEntity<String> removeRecipeFromFavorites(@PathVariable long userId, @PathVariable long recipeId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Recipe> favoriteRecipes = user.getFavoriteRecipes();
            favoriteRecipes.removeIf(recipe -> recipe.getId() == recipeId);
            userRepository.save(user);
            return ResponseEntity.ok("Recipe removed from favorites list for user" + user.getUsername());
        }
        return ResponseEntity.notFound().build();
    }

}
