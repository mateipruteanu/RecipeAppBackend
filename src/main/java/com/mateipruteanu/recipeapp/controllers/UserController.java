package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
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

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        if(userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get();
        }
        return new User();
    }

    @PostMapping("/users")
    public String saveUser(@RequestBody User user) {
        if(userRepository.findByUsername(user.getUsername()) != null) {
            return "Username already exists";
        }
        userRepository.save(user);
        return "Saved";
    }

    @PutMapping("users/{id}")
    public String updateUser(@PathVariable long id, @RequestBody User user) {
        if(userRepository.findById(id).isPresent()) {
            User existingUser = userRepository.findById(id).get();
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(user.getPassword());
            userRepository.save(existingUser);
            return "Updated";
        }
        return "User not found";
    }

    @DeleteMapping("users/{id}")
    public String deleteUser(@PathVariable long id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return "Deleted";
        }
        return "User not found";
    }


    @GetMapping("/users/{userId}/recipes")
    public List<Recipe> getUserAddedRecipes(@PathVariable long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getAddedRecipes();
        }
        return Collections.emptyList();
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
    public String addRecipeToUser(@PathVariable long userId, @RequestBody Recipe recipe) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            recipeRepository.save(recipe);
            user.getAddedRecipes().add(recipe);
            userRepository.save(user);
            return "Recipe added to users list";
        }
        return "User not found";
    }

    @DeleteMapping("/users/{userId}/recipes/{recipeId}")
    public String removeRecipeFromUser(@PathVariable long userId, @PathVariable long recipeId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Recipe> addedRecipes = user.getAddedRecipes();
            addedRecipes.removeIf(recipe -> recipe.getId() == recipeId);
            userRepository.save(user);
            return "Recipe removed from users list";
        }
        return "User not found";
    }



}
