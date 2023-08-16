package com.mateipruteanu.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "RECIPE_INGREDIENT")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    @JsonIgnoreProperties("recipeIngredients")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column
    private String quantity;

    public RecipeIngredient() {
        recipe = new Recipe();
        ingredient = new Ingredient();
        quantity = "";
    }

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, String quantity) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
