package com.mateipruteanu.recipeapp.models;

import jakarta.persistence.*;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String instructions;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
