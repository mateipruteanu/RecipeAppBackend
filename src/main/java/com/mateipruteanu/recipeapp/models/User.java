package com.mateipruteanu.recipeapp.models;

import com.mateipruteanu.recipeapp.token.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @OneToMany
    @JoinTable(
            name = "user_added_recipes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> addedRecipes = new ArrayList<>();

    @OneToMany
    @JoinTable(
            name = "user_favorite_recipes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> favoriteRecipes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

//    Constructors
    public User() {
        username = "";
        password = "";
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(long id, String username, String password, List<Recipe> addedRecipes, List<Recipe> favoriteRecipes) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.addedRecipes = addedRecipes;
        this.favoriteRecipes = favoriteRecipes;
    }

    public User(String username, String password, List<Recipe> addedRecipes, List<Recipe> favoriteRecipes) {
        this.username = username;
        this.password = password;
        this.addedRecipes = addedRecipes;
        this.favoriteRecipes = favoriteRecipes;
    }



//    User Details specific functions
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }
}
