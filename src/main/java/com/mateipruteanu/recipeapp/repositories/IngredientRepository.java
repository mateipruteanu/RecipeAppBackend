package com.mateipruteanu.recipeapp.repositories;

import com.mateipruteanu.recipeapp.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Ingredient findByName(String name);
}
