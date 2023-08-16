package com.mateipruteanu.recipeapp.repositories;

import com.mateipruteanu.recipeapp.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
