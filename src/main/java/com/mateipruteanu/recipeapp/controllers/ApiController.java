package com.mateipruteanu.recipeapp.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/")
    public String getInfo() {
        return "Restful API for a recipe app";
    }

    @GetMapping("/secure")
    public String getSecureInfo() {
        return "This is a secure endpoint";
    }

    @GetMapping("/auth/notSecure")
    public String getNotSecureInfo() {
        return "This is not a secure endpoint";
    }
}
