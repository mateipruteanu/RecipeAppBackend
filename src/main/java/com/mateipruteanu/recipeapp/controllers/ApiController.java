package com.mateipruteanu.recipeapp.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {
    @GetMapping("/api")
    public String getInfo() {
        return "Restful API for a recipe app";
    }
}
