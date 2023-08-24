package com.mateipruteanu.recipeapp.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173/")
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

    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public String getAdminInfo() {
        return "This is an admin endpoint";
    }

    @GetMapping("/user")
    public String getUserInfo() {
        return "This is a user endpoint";
    }
}
