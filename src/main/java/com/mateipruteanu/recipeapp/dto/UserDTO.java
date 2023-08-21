package com.mateipruteanu.recipeapp.dto;

import com.mateipruteanu.recipeapp.models.Recipe;
import com.mateipruteanu.recipeapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private long id;
    private String username;
    private String password;
    private String role;
    private List<Recipe> addedRecipes;
    private List<Recipe> favoriteRecipes;

    public UserDTO(User originalUser) {
        this.id = originalUser.getId();
        this.username = originalUser.getUsername();
        this.password = originalUser.getPassword();
        this.role = originalUser.getRole().name();
        this.addedRecipes = originalUser.getAddedRecipes();
        this.favoriteRecipes = originalUser.getFavoriteRecipes();
    }
}
