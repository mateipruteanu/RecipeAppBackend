package com.mateipruteanu.recipeapp.controllers;

import com.mateipruteanu.recipeapp.dto.UserDTO;
import com.mateipruteanu.recipeapp.models.Ingredient;
import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.models.RecipeIngredient;
import com.mateipruteanu.recipeapp.models.User;
import com.mateipruteanu.recipeapp.repositories.IngredientRepository;
import com.mateipruteanu.recipeapp.repositories.RecipeRepository;
import com.mateipruteanu.recipeapp.repositories.UserRepository;
import com.mateipruteanu.recipeapp.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    @Secured("ROLE_ADMIN")
    public List<User> getUsers() {
        return userRepository.findAll();
    }


    // CRUD operations for users
    @GetMapping("/{id}")
//    should only be accessible by admin or the user with the id
    public ResponseEntity<UserDTO> getUser(@RequestHeader(value="Authorization") String token, @PathVariable long id) {
        if(userRepository.findById(id).isPresent() && isTokenIDSameAsID(token, id)) {
            UserDTO userDTO = new UserDTO(userRepository.findById(id).get());
            return ResponseEntity.ok(userDTO);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(409).body("Username already exists");
        }
        userRepository.save(user);
        return ResponseEntity.ok("Saved");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@RequestHeader(value="Authorization") String token, @PathVariable long id, @RequestBody User user) {
        if(userRepository.findById(id).isPresent() && isTokenIDSameAsID(token, id)) {
            User existingUser = userRepository.findById(id).get();
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(existingUser);
            return ResponseEntity.ok("Updated");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@RequestHeader(value="Authorization") String token, @PathVariable long id) {
        if(userRepository.findById(id).isPresent() && isTokenIDSameAsID(token, id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("Deleted user with id " + id);
        }
        return ResponseEntity.notFound().build();
    }


    //    checks if user from token has the same id as the user from the argument
    public boolean isTokenIDSameAsID(String token, long id) {
        token = token.substring(7);

        if(tokenRepository.findByToken(token).isPresent()) {
            long userIdFromToken = tokenRepository.findByToken(token).get().getUser().getId();
            if(userRepository.findById(id).isPresent()) {
                return id == userIdFromToken;
            }
        }
        return false;
    }




    //User added recipes list
    @GetMapping("/{userId}/recipes")
    public ResponseEntity<List<Recipe>> getUserAddedRecipes(@PathVariable long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(user.getAddedRecipes());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}/recipes/{recipeId}")
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


    @PostMapping("/{userId}/recipes")
    public ResponseEntity<String> addRecipeToUser(@RequestHeader(value="Authorization") String token, @PathVariable long userId, @RequestBody Recipe recipe) {
        if(userRepository.findById(userId).isPresent() && isTokenIDSameAsID(token, userId)) {
            User user = userRepository.findById(userId).get();
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
            recipe.setAddedBy(user.getUsername());
            recipeRepository.save(recipe);
            user.getAddedRecipes().add(recipe);
            userRepository.save(user);
            return ResponseEntity.ok("Recipe added to users list");
        }

        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{userId}/recipes/{recipeId}")
    public ResponseEntity<String> removeRecipeFromUser(@RequestHeader(value="Authorization") String token, @PathVariable long userId, @PathVariable long recipeId) {
        if(userRepository.findById(userId).isPresent() && isTokenIDSameAsID(token, userId)) {
            User user = userRepository.findById(userId).get();
            List<Recipe> addedRecipes = user.getAddedRecipes();
            addedRecipes.removeIf(recipe -> recipe.getId() == recipeId);
            recipeRepository.deleteById(recipeId);
            userRepository.save(user);
            return ResponseEntity.ok("Recipe removed from users list");
        }
        return ResponseEntity.notFound().build();
    }


    //Favorites List
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<Recipe>> getUserFavoriteRecipes(@RequestHeader(value="Authorization") String token, @PathVariable long userId) {
        if(userRepository.findById(userId).isPresent() && isTokenIDSameAsID(token, userId)) {
            User user = userRepository.findById(userId).get();
            return ResponseEntity.ok(user.getFavoriteRecipes());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<Recipe> getUserFavoriteRecipe(@RequestHeader(value="Authorization") String token, @PathVariable long userId, @PathVariable long recipeId) {
        if(userRepository.findById(userId).isPresent() && isTokenIDSameAsID(token, userId)) {
            User user = userRepository.findById(userId).get();
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

    @PostMapping("/{userId}/favorites")
    public ResponseEntity<String> addRecipeToFavorites(@RequestHeader(value="Authorization") String token, @PathVariable long userId, @RequestBody Recipe recipe) {
        if(userRepository.findById(userId).isPresent() && isTokenIDSameAsID(token, userId)) {
            User user = userRepository.findById(userId).get();
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

    @DeleteMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<String> removeRecipeFromFavorites(@RequestHeader(value="Authorization") String token, @PathVariable long userId, @PathVariable long recipeId) {
        if(userRepository.findById(userId).isPresent() && isTokenIDSameAsID(token, userId)) {
            User user = userRepository.findById(userId).get();
            List<Recipe> favoriteRecipes = user.getFavoriteRecipes();
            favoriteRecipes.removeIf(recipe -> recipe.getId() == recipeId);
            userRepository.save(user);
            return ResponseEntity.ok("Recipe removed from favorites list for user" + user.getUsername());
        }

        return ResponseEntity.notFound().build();
    }


}
