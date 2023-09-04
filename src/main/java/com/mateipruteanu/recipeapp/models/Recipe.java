package com.mateipruteanu.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;

    @Column
    private String addedBy;

    @Column
    private String description;
    @Column
    private String instructions;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("recipe")
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();


    public Recipe() {
        name = "";
        description = "";
        instructions = "";
    }

    public Recipe(String name, String description, String instructions) {
        this.setName(name);
        this.setDescription(description);
        this.setInstructions(instructions);
    }
}
